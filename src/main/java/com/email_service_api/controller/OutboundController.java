package com.email_service_api.controller;

import com.email_service_api.entity.OutgoingEmail;
import com.email_service_api.service.OutgoingEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OutboundController {

    @Autowired
    private OutgoingEmailService outgoingEmailService;

    @PostMapping("/send-email")
    public ResponseEntity<OutgoingEmail> sendEmail(@RequestBody OutgoingEmail outgoingEmail) {

        return ResponseEntity.ok(outgoingEmailService.sendSimpleMessage(outgoingEmail));
    }

}
