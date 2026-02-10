package com.chatbot.whatsapp_chatbot.insurance.production.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "policies")

public class Policy {

    @Id
    @Column(name = "policy_number")
    private String policyNumber;

    @Column(name = "customer_first_name")
    private String customerFirstName;

    @Column(name = "customer_last_name")
    private String customerLastName;

    @Column(name = "customer_phone")
    private Integer customerPhone;

    @Column(name = "policy_type")
    private String policyType;

    @Column(name = "deductible")
    private BigDecimal deductible;

    @Column(name = "max_coverage")
    private BigDecimal maxCoverage;

    @Column(name = "coverage_period")
    private String coveragePeriod;

    @Column(name = "effective_date")
    private LocalDate effectiveDate;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    @Column(name = "monthly_premium")
    private BigDecimal monthlyPremium;

    @Column(name = "payment_cycle")
    private String paymentCycle;

    @Column(name = "status")
    private String policyStatus;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

}
