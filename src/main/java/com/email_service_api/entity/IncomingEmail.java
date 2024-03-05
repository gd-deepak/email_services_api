package com.email_service_api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "inbox_data", schema = "email_service")
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
public class IncomingEmail {

    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private UUID Id;

    @Column(name = "message_id", unique = true)
    private String messageId;

    @Column
    private String senderName ;

    @Column
    private String senderId ;

    @Column
    private List<String> recipientIds;
    @Column
    private List<String> bccRecipientsIds;
    @Column
    private List<String> ccRecipientsIds;

    @Column
    private OffsetDateTime dateTime;

    @Column
    private String subject;

    @Column(length = 10000)
    private String mailDesciption;

    @Column
    private Boolean isProcessed;

}
