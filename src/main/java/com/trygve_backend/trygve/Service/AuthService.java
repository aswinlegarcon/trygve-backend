package com.trygve_backend.trygve.Service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.trygve_backend.trygve.DTO.LoginRequestDTO;
import com.trygve_backend.trygve.DTO.LoginResponseDTO;
import com.trygve_backend.trygve.DTO.UserDetailsDTO;
import com.trygve_backend.trygve.Entity.User;
import com.trygve_backend.trygve.Repository.UserRepository;
import com.trygve_backend.trygve.Utils.JwtUtil;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    // method to authenticate user using Firebase ID token
    @Transactional
    public User authenticateUser(String idToken)
    {
        try{
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            String phoneNumber = decodedToken.getClaims().get("phone_number").toString();
            if(phoneNumber==null)
            {
                throw new RuntimeException("Phone number not found in token");
            }
            return  userRepository.findByPhoneNumber(phoneNumber).orElseGet( () -> {
                User newUser = new User();
                newUser.setPhoneNumber(phoneNumber);
                return userRepository.save(newUser);
            });
        } catch (FirebaseAuthException e) {
            throw new RuntimeException("Firebase authentication failed: " + e.getMessage(), e);
        }
    }

    public LoginResponseDTO loginUser(LoginRequestDTO loginRequest) {
        try {
            // Verify Firebase ID token
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(loginRequest.getIdToken());
            String phoneNumber = decodedToken.getClaims().get("phone_number").toString();

            if (phoneNumber == null) {
                throw new RuntimeException("Phone number not found in Firebase token");
            }

            // Find user by phone number from Firebase token
            Optional<User> userOpt = userRepository.findByPhoneNumber(phoneNumber);

            if (userOpt.isEmpty()) {
                throw new RuntimeException("User not found with phone number: " + phoneNumber);
            }

            User user = userOpt.get();

            // Optionally verify email matches if provided
            if (loginRequest.getPhoneNumber() != null && !loginRequest.getPhoneNumber().equals(user.getPhoneNumber())) {
                throw new RuntimeException("Phone Number does not match user record");
            }

            // Generate JWT token
            String token = jwtUtil.generateToken(user.getPhoneNumber() != null ? user.getPhoneNumber() : user.getEmail(), user.getId());

            return new LoginResponseDTO(token, "Bearer",user.getId(), user.getEmail(), user.getPhoneNumber(), "Login successful");

        } catch (FirebaseAuthException e) {
            throw new RuntimeException("Firebase authentication failed: " + e.getMessage(), e);
        }
    }


    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

//      method to get user by id
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }


//    method to update user details
    @Transactional
    public User updateUserDetails(UserDetailsDTO userDetailsDTO)
    {
        User user = userRepository.findByPhoneNumber(userDetailsDTO.getPrimaryPhoneNumber())
                .orElseThrow( () -> new RuntimeException("User not found with phone number : "+ userDetailsDTO.getPrimaryPhoneNumber()));

        user.setName(userDetailsDTO.getName());
        user.setEmail(userDetailsDTO.getEmail());
        user.setAddress(userDetailsDTO.getLocation());
        user.setSecondaryPhoneNumber(userDetailsDTO.getSecondaryPhoneNumber());

        return userRepository.save(user);
    }

//    method to get user if available
    public boolean isPhoneNumberRegistered(String phoneNumber)
    {
        logger.info("Checking registration for phone number: '{}'", phoneNumber);
        boolean isPresent = userRepository.findByPhoneNumber(phoneNumber).isPresent();
        logger.info("Is number present in DB: {}", isPresent);
        return isPresent;
    }

    public boolean isEmailRegistered(String email)
    {
        logger.info("Checking registration for email: '{}'", email);
        boolean isPresent = userRepository.findByEmail(email).isPresent();
        logger.info("Is email present in DB: {}", isPresent);
        return isPresent;
    }

    public boolean isUserValid(String email,String phoneNumber)
    {
        User userByEmail = userRepository.findByEmail(email).orElse(null);
        User userByPhone = userRepository.findByPhoneNumber(phoneNumber).orElse(null);

        if (userByEmail == null || userByPhone == null) {
            return false;
        }

        return userByEmail.getId() == userByPhone.getId();
    }


//    method to delete user by id
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        userRepository.delete(user);
        logger.info("User with id {} has been deleted", id);
    }
}
