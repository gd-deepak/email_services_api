package com.email_service_api.controller;

import com.email_service_api.entity.IncomingEmail;
import com.email_service_api.service.IncomingEmailService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
public class InboundController {
    @Autowired
    private IncomingEmailService incomingEmailService;

//    @GetMapping("/read-email")
//    public ResponseEntity<List<IncomingEmail>> readEmails() throws MessagingException, IOException {
//
//        return ResponseEntity.ok(incomingEmailService.readEmails());
//    }
    @GetMapping("/read-emails")
    public ResponseEntity<List<IncomingEmail>>  readEmails1() throws MessagingException, IOException {

        return ResponseEntity.ok(incomingEmailService.readEmails());
    }
}

