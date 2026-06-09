package com.cdas.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "delays_data")

@Data

public class DelayData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    private String shopCode;

    private String shopDesc;

    private String eqptName;

    private String subEqptName;

    private String agency;

    private LocalDateTime delayFrom;

    private LocalDateTime delayUpto;

    private String delayDuration;

    private String delayDesc;

    private String userEntered;

    private LocalDateTime createdAt;
}