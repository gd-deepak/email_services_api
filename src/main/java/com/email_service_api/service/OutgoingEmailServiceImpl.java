package com.email_service_api.service;

import com.email_service_api.entity.OutgoingEmail;
import com.email_service_api.repo.OutgoingEmailRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Service
public class OutgoingEmailServiceImpl implements OutgoingEmailService {

    @Autowired
    OutgoingEmailRepo outgoingEmailRepo;
    @Autowired
    private JavaMailSender javaMailSender;


    @Override
    public OutgoingEmail saveOutgoingEmailData(OutgoingEmail outgoingEmail) {
//        outgoingEmail.setDdtm(OffsetDateTime.from(LocalDateTime.now()));
        return outgoingEmailRepo.save(outgoingEmail);
    }

    @Override
    public OutgoingEmail sendSimpleMessage(OutgoingEmail outgoingEmail) {
        System.out.println("sending email now");
        OutgoingEmail data = saveOutgoingEmailData(outgoingEmail);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(data.getRecipient());
        message.setSubject(data.getSubject());
        message.setText(data.getMailDesciption());
        javaMailSender.send(message);
        return data;
}
}
