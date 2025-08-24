package com.trygve_backend.trygve.Controller;

import com.trygve_backend.trygve.DTO.AuthRequestDTO;
import com.trygve_backend.trygve.DTO.AuthResponseDto;
import com.trygve_backend.trygve.DTO.PhoneNumberDTO;
import com.trygve_backend.trygve.DTO.UserDetailsDTO;
import com.trygve_backend.trygve.Entity.User;
import com.trygve_backend.trygve.Service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
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

    @PostMapping("/check-user")
    public ResponseEntity<?> checkPhoneNumber(@RequestBody PhoneNumberDTO phoneNumberDTO)
    {
        boolean isRegistered = authService.isPhoneNumberRegistered(phoneNumberDTO.getPhoneNumber());
        return ResponseEntity.ok(Map.of("isRegistered", isRegistered));
    }

    @PutMapping("/register")
    public ResponseEntity<?> updateUserDetails(@RequestBody UserDetailsDTO userDetailsDTO)
    {
        try {
            User updatedUser = authService.updateUserDetails(userDetailsDTO);
            AuthResponseDto authResponseDto = new AuthResponseDto();
            if(updatedUser.getEmail()==null || updatedUser.getName()==null || updatedUser.getAddress()==null)
            {
                authResponseDto.setStatus(false);
                authResponseDto.setMessage("Profile incomplete, please update all details");
                return ResponseEntity.status(400).body(authResponseDto);
            }
            authResponseDto.setStatus(true);
            authResponseDto.setMessage("Profile updated successfully");
            return ResponseEntity.status(200).body(authResponseDto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
//  delete user by id
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        try {
            authService.deleteUser(id);
            return ResponseEntity.ok("User deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("User not found " +e.getMessage());
        }
    }

}
