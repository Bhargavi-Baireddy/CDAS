package com.cdas.dto;

import lombok.Data;

@Data

public class LoginRequest {

    private String empNo;

    private String password;
}