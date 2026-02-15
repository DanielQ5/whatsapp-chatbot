package com.chatbot.whatsapp_chatbot.insurance.production.repository;

import com.chatbot.whatsapp_chatbot.insurance.production.entity.Policy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository

public interface PolicyRepository extends JpaRepository<Policy, String> {

    Optional<Policy> findByPolicyNumber(String policyNumber);
}
