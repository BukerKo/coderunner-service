package com.gmail.buer2012.service;

import com.gmail.buer2012.entity.User;
import com.gmail.buer2012.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
    
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    public Optional<User> findByEmail(String email) {
        return  userRepository.findByEmail(email);
    }
    
    public User updateUser(User user) {
        User userFromDb = userRepository.findByEmail(user.getEmail()).orElse(null);
        
        userFromDb.setPassword(user.getPassword());
        userFromDb.setEmail(user.getEmail());
        userFromDb.setEnabled(user.getEnabled());
        
        return userRepository.save(userFromDb);
    }
    
    public User persist(User user) {
        return userRepository.save(user);
    }
    
    public Boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}