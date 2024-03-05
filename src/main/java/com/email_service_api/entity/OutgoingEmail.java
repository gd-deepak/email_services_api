package com.email_service_api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;

import java.time.OffsetDateTime;
import java.util.UUID;


@Entity
@Table(name = "email_data", schema = "email_service")
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
public class OutgoingEmail {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private UUID Id;

    @Column
    @Value("${spring.mail.username}")
    private String sender = "deepakgadhave61@gmail.com";

//    @Column
//    private OffsetDateTime ddtm;

    @Column
    private String recipient;

    @Column
    private String subject;

    @Column(length = 10000)
    private String mailDesciption;

}



