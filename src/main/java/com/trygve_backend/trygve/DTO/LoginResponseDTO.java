package com.trygve_backend.trygve.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseDTO {
    private String token;
    private String tokenType = "Bearer";
    private Long userId;
    private String email;
    private String phoneNumber;
    private String message;
}
