package com.trygve_backend.trygve.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class LoginRequestDTO {
    private String idToken;
    private String phoneNumber;
}
