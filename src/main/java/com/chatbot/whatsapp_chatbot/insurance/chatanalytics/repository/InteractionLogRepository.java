package com.chatbot.whatsapp_chatbot.insurance.chatanalytics.repository;

import com.chatbot.whatsapp_chatbot.insurance.chatanalytics.entity.InteractionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface InteractionLogRepository extends JpaRepository<InteractionLog, Long> {



}
