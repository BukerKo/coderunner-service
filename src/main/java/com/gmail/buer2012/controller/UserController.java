package com.gmail.buer2012.controller;

import com.gmail.buer2012.entity.User;
import com.gmail.buer2012.repository.UserRepository;
import com.gmail.buer2012.security.CurrentUser;
import com.gmail.buer2012.security.UserPrincipal;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class UserController {
    
    private UserRepository userRepository;
    
    @GetMapping("/user/me")
//    @PreAuthorize("hasRole('USER')")
    public User getCurrentUser(@CurrentUser UserPrincipal userPrincipal) {
        return userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id : " + userPrincipal.getId()));
    }
}