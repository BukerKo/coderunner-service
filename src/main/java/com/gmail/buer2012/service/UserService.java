package com.gmail.buer2012.service;

import com.gmail.buer2012.entity.User;
import com.gmail.buer2012.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
    
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    public Page<User> findAll(Pageable page) {
        return userRepository.findAll(page);
    }
    
    
    public User updateUser(User user) {
        User userFromDb = userRepository.findByUsername(user.getUsername()).orElse(null);
        
        userFromDb.setPassword(passwordEncoder.encode(user.getPassword()));
        userFromDb.setEmail(user.getEmail());
        
        return userRepository.save(userFromDb);
    }
    
    
    public User persist(User user) {
        return userRepository.save(user);
    }
    
    @Transactional
    public void deleteByIdIn(List<Long> ids) {
        userRepository.deleteByIdIn(ids);
    }
    
    public Boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    public Boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}