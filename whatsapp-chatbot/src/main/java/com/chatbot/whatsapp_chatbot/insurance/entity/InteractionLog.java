package com.chatbot.whatsapp_chatbot.insurance.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
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
    private Integer phoneNumber;

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

}
