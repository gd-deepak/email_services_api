package com.email_service_api.service;

import com.email_service_api.entity.IncomingEmail;
import com.email_service_api.entity.OutgoingEmail;
import com.email_service_api.repo.IncomingEmailRepo;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMultipart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Service
public class IncomingEmailServiceImpl implements IncomingEmailService {

    @Value("${spring.mail.imap.host}")
    private String imapHost;

    @Value("${spring.mail.imap.port}")
    private String imapPort;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    @Autowired
    IncomingEmailRepo repo;
    @Autowired
    OutgoingEmailService outgoingEmailService;

    @Override
    public IncomingEmail saveInComingEmail(IncomingEmail email) {
        System.out.println("message Id : "+email.getMessageId());
        return repo.save(email);
    }


    public List<IncomingEmail> readEmails() {
        List<IncomingEmail> emails = new ArrayList<>();
        Store store = connectToEmailServer();

        if (store != null) {
            try {
                Folder inbox = store.getFolder("INBOX");
                inbox.open(Folder.READ_ONLY);
                Message[] messages = inbox.getMessages();

                for (Message message : messages) {
                    IncomingEmail incomingEmail = extractEmailData(message);
                    emails.add(incomingEmail);
                }

                inbox.close(false);
                store.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return emails;
    }

    protected Store connectToEmailServer() {
        Properties properties = new Properties();
        properties.put("mail.imap.ssl.enable", "true");
        properties.put("mail.imap.host", imapHost);
        properties.put("mail.imap.port", imapPort);

        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Store store = session.getStore("imap");
            store.connect(imapHost, username, password);
            return store;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    protected IncomingEmail extractEmailData(Message message) throws MessagingException, IOException {
        IncomingEmail incomingEmail = new IncomingEmail();
        incomingEmail.setSubject(message.getSubject());
        incomingEmail.setMessageId(message.getHeader("Message-ID")[0].replaceAll("[<>]", ""));
        incomingEmail.setSenderId(extractEmailAddress(message.getFrom()[0]));
        incomingEmail.setMailDesciption(getTextFromMessage(message).replaceAll("\\r\\n", " ").replaceAll("\\s+", " "));
        incomingEmail.setSenderName(extractNameFromAddress(message.getFrom()[0]));
        incomingEmail.setRecipientIds(extractEmailAddresses(message.getRecipients(Message.RecipientType.TO)));
        incomingEmail.setCcRecipientsIds(extractEmailAddresses(message.getRecipients(Message.RecipientType.CC)));
        incomingEmail.setBccRecipientsIds(extractEmailAddresses(message.getRecipients(Message.RecipientType.BCC)));
//        incomingEmail.setDateTime(OffsetDateTime.ofInstant(message.getSentDate().toInstant(), ZoneId.systemDefault()));
        incomingEmail.setIsProcessed(false);

        return incomingEmail;
    }

    protected String extractEmailAddress(Address address) {
        if (address instanceof InternetAddress) {
            InternetAddress internetAddress = (InternetAddress) address;
            return internetAddress.getAddress();
        }
        return "";
    }
    protected List<String> extractEmailAddresses(Address[] addresses) {
        List<String> emails = new ArrayList<>();
        if (addresses != null) {
            for (Address address : addresses) {
                String email = extractEmailAddress(address);
                if (!email.isEmpty()) {
                    emails.add(email);
                }
            }
        }
        return emails;
    }
    protected String extractNameFromAddress(Address address) {
        if (address instanceof InternetAddress) {
            InternetAddress internetAddress = (InternetAddress) address;
            String personal = internetAddress.getPersonal();
            if (personal != null && !personal.isEmpty()) {
                return personal;
            }
        }
        return "";
    }

    protected String getTextFromMessage(Message message) throws MessagingException, IOException {
        if (message.isMimeType("text/plain")) {
            return message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            return getTextFromMimeMultipart(mimeMultipart);
        } else {
            return ""; // Handle other types of content as needed
        }
    }

    protected String getTextFromMimeMultipart(MimeMultipart mimeMultipart) throws MessagingException, IOException {
        StringBuilder result = new StringBuilder();
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result.append(bodyPart.getContent());
            } else if (bodyPart.isMimeType("multipart/*")) {
                result.append(getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent()));
            }
        }
        return result.toString();
    }

    @Scheduled(fixedRate = 10000)
    public void storeIncomingEmails() {
        System.out.println("Checking for new emails ");
        List<IncomingEmail> incomingEmails = this.readEmails();
        for (IncomingEmail entity : incomingEmails) {
            if (!repo.existsByMessageId(entity.getMessageId())) {
                this.saveInComingEmail(entity);
            }
        }
    }

    @Scheduled(fixedRate = 10000)
    public void ProcessIncomingEmails() throws IOException {
        List<IncomingEmail> incomingEmails = repo.findAll();
        if (!incomingEmails.isEmpty()) {
            for (IncomingEmail entity : incomingEmails) {
                System.out.println("processing mails");
                if (entity.getIsProcessed().equals(Boolean.FALSE)) {
                    System.out.println("sending email");
                    OutgoingEmail outgoingEmail = new OutgoingEmail();
                    outgoingEmail.setRecipient(entity.getSenderId());
                    outgoingEmail.setSubject("Re: " + entity.getSubject());
                    String emailDescription = readEmailDescriptionFromFile("src/main/resources/email_description.txt");
                    outgoingEmail.setMailDesciption("Hello " + entity.getSenderName() + ",  \n" + emailDescription);
                    outgoingEmailService.sendSimpleMessage(outgoingEmail);
                    entity.setIsProcessed(true);
                    repo.save(entity);
                }
            }
        }
    }

    protected String readEmailDescriptionFromFile(String filePath) throws IOException {
        return Files.readString(Paths.get(filePath));
    }
}

