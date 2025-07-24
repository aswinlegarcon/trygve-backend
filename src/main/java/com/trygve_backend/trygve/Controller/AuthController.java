package com.trygve_backend.trygve.Controller;

import com.trygve_backend.trygve.DTO.AuthRequestDTO;
import com.trygve_backend.trygve.DTO.UserDetailsDTO;
import com.trygve_backend.trygve.Entity.User;
import com.trygve_backend.trygve.Service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/auth")
    public ResponseEntity<?> authenticate(@RequestBody AuthRequestDTO authRequestDTO)
    {
        try{
            User user = authService.authenticateUser(authRequestDTO.getToken());
            return ResponseEntity.ok(user);
        }catch (RuntimeException e){
            return ResponseEntity.status(401).body("Authentication failed: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id)
    {
        try{
            User user = authService.getUserById(id);
            return ResponseEntity.ok(user);
        }catch (RuntimeException e)
        {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PutMapping("/details")
    public ResponseEntity<?> updateUserDetails(@RequestBody UserDetailsDTO userDetailsDTO)
    {
        try {
            User updatedUser = authService.updateUserDetails(userDetailsDTO);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

}
