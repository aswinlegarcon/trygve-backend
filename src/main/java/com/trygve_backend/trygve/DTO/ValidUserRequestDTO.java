package com.trygve_backend.trygve.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValidUserRequestDTO {
    private String email;
    private String phoneNumber;
}
