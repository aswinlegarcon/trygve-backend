package com.trygve_backend.trygve.Controller;

import com.trygve_backend.trygve.DTO.*;
import com.trygve_backend.trygve.Entity.User;
import com.trygve_backend.trygve.Service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/user")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/auth")
    public ResponseEntity<?> authenticate(@RequestBody AuthTokenRequestDTO authTokenRequestDTO)
    {
        try{
            User user = authService.authenticateUser(authTokenRequestDTO.getToken());
            return ResponseEntity.ok(user);
        }catch (RuntimeException e){
            return ResponseEntity.status(401).body("Authentication failed: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequestDTO loginRequest) {
        try {
            LoginResponseDTO response = authService.loginUser(loginRequest);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
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

    @PostMapping("/check-phone")
    public ResponseEntity<?> checkPhoneNumber(@RequestBody PhoneNumberDTO phoneNumberDTO)
    {
        boolean isRegistered = authService.isPhoneNumberRegistered(phoneNumberDTO.getPhoneNumber());
        return ResponseEntity.ok(Map.of("isRegistered", isRegistered));
    }

    @PostMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestBody EmailDTO emailDTO)
    {
        boolean isRegistered = authService.isEmailRegistered(emailDTO.getEmail());
        return ResponseEntity.ok(Map.of("isRegistered", isRegistered));
    }

    @PostMapping("/check-user")
    public ResponseEntity<?> checkUser(@RequestBody ValidUserRequestDTO validUserRequestDTO) {
        boolean belongsToSameUser = authService.isUserValid(
                validUserRequestDTO.getEmail(),
                validUserRequestDTO.getPhoneNumber()
        );
        return ResponseEntity.ok(Map.of("belongsToSameUser", belongsToSameUser));
    }

    @PutMapping("/register")
    public ResponseEntity<?> updateUserDetails(@RequestBody UserDetailsDTO userDetailsDTO)
    {
        try {
            User updatedUser = authService.updateUserDetails(userDetailsDTO);
            AuthResponseDTO authResponseDto = new AuthResponseDTO();
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
