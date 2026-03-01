package com.chatbot.whatsapp_chatbot.insurance.chatanalytics.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "insurance_chatanalytics")

public class InteractionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "policy_number")
    private String policyNumber;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "actions_taken", columnDefinition = "text[]")  // ← PostgreSQL array!
    private List<String> actionsTaken;

    @Column(name = "requested_representative")
    private Boolean requestedRepresentative;

    @Column(name = "session_start")
    private LocalDateTime sessionStartTime;

    @Column(name = "session_end")
    private LocalDateTime sessionEndTime;

    @Column(name = "session_duration_seconds")
    private Integer sessionDurationSeconds;

    // Manual getters/setters (Lombok not working)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<String> getActionsTaken() {
        return actionsTaken;
    }

    public void setActionsTaken(List<String> actionsTaken) {
        this.actionsTaken = actionsTaken;
    }

    public Boolean getRequestedRepresentative() {
        return requestedRepresentative;
    }

    public void setRequestedRepresentative(Boolean requestedRepresentative) {
        this.requestedRepresentative = requestedRepresentative;
    }

    public LocalDateTime getSessionStartTime() {
        return sessionStartTime;
    }

    public void setSessionStartTime(LocalDateTime sessionStartTime) {
        this.sessionStartTime = sessionStartTime;
    }

    public LocalDateTime getSessionEndTime() {
        return sessionEndTime;
    }

    public void setSessionEndTime(LocalDateTime sessionEndTime) {
        this.sessionEndTime = sessionEndTime;
    }

    public Integer getSessionDurationSeconds() {
        return sessionDurationSeconds;
    }

    public void setSessionDurationSeconds(Integer sessionDurationSeconds) {
        this.sessionDurationSeconds = sessionDurationSeconds;
    }

}
