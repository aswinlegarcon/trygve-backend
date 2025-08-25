package com.trygve_backend.trygve.DTO;

import lombok.Data;

@Data
public class LoginRequestDTO {
    private String email;
    private String phoneNumber;
}
