package com.trygve_backend.trygve.Controller;

import com.trygve_backend.trygve.DTO.HealthDTO;
import com.trygve_backend.trygve.Service.HealthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @Autowired
    HealthService healthService;

    @GetMapping("/health")
    public ResponseEntity<HealthDTO> getHealth()
    {
        HealthDTO status = healthService.getHealth();
        return ResponseEntity.ok(status);
    }
}
