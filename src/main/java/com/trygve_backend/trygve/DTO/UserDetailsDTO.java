package com.trygve_backend.trygve.DTO;

import lombok.Data;

@Data
public class UserDetailsDTO {

    private String primaryPhoneNumber;
    private String name;
    private String email;
    private String address;
    private String secondaryPhoneNumber;
}
