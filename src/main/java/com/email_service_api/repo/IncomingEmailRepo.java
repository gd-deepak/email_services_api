package com.email_service_api.repo;

import com.email_service_api.entity.IncomingEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IncomingEmailRepo extends JpaRepository<IncomingEmail, UUID>, JpaSpecificationExecutor<IncomingEmail> {
    boolean findByIsProcessed(Boolean value);

    boolean existsByMessageId(String messageId);
}
