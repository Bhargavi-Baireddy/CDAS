package com.cdas.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")

@Data

public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "emp_no")
    private String empNo;

    private String password;

    @Column(name = "emp_name")
    private String empName;

    private String dept;

    private String designation;

    private String role;

    private Boolean active;

    @Column(name = "created_at", updatable = false,
            insertable = false,
            columnDefinition = "timestamp DEFAULT current_timestamp()")
    private LocalDateTime createdAt;
}