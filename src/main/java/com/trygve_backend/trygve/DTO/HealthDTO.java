package com.trygve_backend.trygve.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HealthDTO {
    private String springBootStatus;
    private String mysqlStatus;
}
