package com.email_service_api.service;

import com.email_service_api.entity.IncomingEmail;
import jdk.dynalink.linker.LinkerServices;

import java.util.List;

public interface IncomingEmailService {
    IncomingEmail saveInComingEmail(IncomingEmail incomingEmail);
    List<IncomingEmail> readEmails();
}
