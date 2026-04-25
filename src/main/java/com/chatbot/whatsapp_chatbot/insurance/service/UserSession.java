package com.chatbot.whatsapp_chatbot.insurance.service;

import com.chatbot.whatsapp_chatbot.insurance.production.entity.Policy;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class UserSession {
    private String phoneNumber;
    private String policyNumber;
    private List<String> actionsTaken = new ArrayList<>();
    private boolean requestedRepresentative = false;
    private LocalDateTime sessionStart = LocalDateTime.now();
    private Policy policy;

    public UserSession(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void addAction(String action) {
        actionsTaken.add(action);
    }

    // Manual getters/setters
//    public String getPolicyNumber() {
//        return policyNumber;
//    }

//    public void setPolicyNumber(String policyNumber) {
//        this.policyNumber = policyNumber;
//    }
//
//    public String getPhoneNumber() {
//        return phoneNumber;
//    }
//
//    public List<String> getActionsTaken() {
//        return actionsTaken;
//    }
//
//    public boolean isRequestedRepresentative() {
//        return requestedRepresentative;
//    }
//
//    public void setRequestedRepresentative(boolean requestedRepresentative) {
//        this.requestedRepresentative = requestedRepresentative;
//    }
//
//    public LocalDateTime getSessionStart() {
//        return sessionStart;
//    }

}
