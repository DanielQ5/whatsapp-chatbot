package com.chatbot.whatsapp_chatbot.insurance.service;

import com.chatbot.whatsapp_chatbot.insurance.chatanalytics.entity.InteractionLog;
import com.chatbot.whatsapp_chatbot.insurance.chatanalytics.repository.InteractionLogRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;


@Service
public class InteractionLogService {

    private final InteractionLogRepository interactionLogRepository;

    public InteractionLogService(InteractionLogRepository interactionLogRepository) {
        this.interactionLogRepository = interactionLogRepository;
    }

    public void saveInteractionLog(UserSession session) {
        // 1. Create new InteractionLog object
        InteractionLog interactionLog = new InteractionLog();
        // 2. Set all fields from session
        interactionLog.setPhoneNumber(session.getPhoneNumber());
        interactionLog.setPolicyNumber(session.getPolicyNumber());
        interactionLog.setActionsTaken(session.getActionsTaken());
        interactionLog.setSessionStartTime(session.getSessionStart());
        interactionLog.setRequestedRepresentative(session.isRequestedRepresentative());
        // 3. Calculate duration

        LocalDateTime sessionEnd = LocalDateTime.now();
        Duration interactionDuration = Duration.between(session.getSessionStart(), sessionEnd);
        interactionLog.setSessionDurationSeconds((int) interactionDuration.getSeconds());


        interactionLog.setSessionEndTime(sessionEnd);
        // 4. Save: logRepo.save(log)
        interactionLogRepository.save(interactionLog);
    }

}
