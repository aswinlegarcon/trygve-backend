package com.trygve_backend.trygve.Service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.trygve_backend.trygve.DTO.UserDetailsDTO;
import com.trygve_backend.trygve.Entity.User;
import com.trygve_backend.trygve.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

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

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    @Transactional
    public User updateUserDetails(UserDetailsDTO userDetailsDTO)
    {
        User user = userRepository.findByPhoneNumber(userDetailsDTO.getPrimaryPhoneNumber())
                .orElseThrow( () -> new RuntimeException("User not found with phone number : "+ userDetailsDTO.getPrimaryPhoneNumber()));

        user.setName(userDetailsDTO.getName());
        user.setEmail(userDetailsDTO.getEmail());
        user.setAddress(userDetailsDTO.getAddress());
        user.setSecondaryPhoneNumber(userDetailsDTO.getSecondaryPhoneNumber());

        return userRepository.save(user);
    }
}
