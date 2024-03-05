package com.email_service_api.repo;

import com.email_service_api.entity.OutgoingEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OutgoingEmailRepo extends JpaRepository<OutgoingEmail, UUID>, JpaSpecificationExecutor<OutgoingEmail> {

}
