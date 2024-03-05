package com.email_service_api.service;

import com.email_service_api.entity.OutgoingEmail;

public interface OutgoingEmailService {
    OutgoingEmail saveOutgoingEmailData(OutgoingEmail outgoingEmail);
    OutgoingEmail sendSimpleMessage(OutgoingEmail outgoingEmail) ;
}
