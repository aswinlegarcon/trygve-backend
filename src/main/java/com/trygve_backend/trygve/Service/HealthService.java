package com.trygve_backend.trygve.Service;

import com.trygve_backend.trygve.DTO.HealthDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class HealthService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public String getSpringBootStatus() {
        return "UP";
    }

    public String getMysqlStatus() {
        String springBootStatus = "UP";
        String mysqlStatus = "DOWN";
        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            return "UP"; // Simulating a successful connection
        } catch (Exception e) {
            return "DOWN"; // Simulating a failed connection
        }
    }

    public HealthDTO getHealth() {
        String springBootStatus = getSpringBootStatus();
        String mysqlStatus = getMysqlStatus();
        return new HealthDTO(springBootStatus, mysqlStatus);
    }
}
