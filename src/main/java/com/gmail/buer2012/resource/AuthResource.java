package com.gmail.buer2012.resource;


import com.gmail.buer2012.entity.Role;
import com.gmail.buer2012.entity.RoleName;
import com.gmail.buer2012.entity.User;
import com.gmail.buer2012.payload.ApiResponse;
import com.gmail.buer2012.payload.SignInRequest;
import com.gmail.buer2012.payload.SignUpRequest;
import com.gmail.buer2012.repository.RoleRepository;
import com.gmail.buer2012.security.JwtTokenProvider;
import com.gmail.buer2012.security.UserPrincipal;
import com.gmail.buer2012.service.UserService;
import com.gmail.buer2012.utils.ApiUtils;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.Collections;

@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthResource {
    
    private AuthenticationManager authenticationManager;
    private UserService userService;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private JwtTokenProvider tokenProvider;
    private ApiUtils apiUtils;
    
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody SignInRequest signInRequest) {
        
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                signInRequest.getUsernameOrEmail(),
                signInRequest.getPassword()
        );
        Authentication authentication = authenticationManager.authenticate(token);
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        String jwt = tokenProvider.generateToken(authentication);
        RoleName role = apiUtils.getRoleFromAuthorities(authentication.getAuthorities());
        String username = ((UserPrincipal) authentication.getPrincipal()).getUsername();
        return ResponseEntity.ok().body(new JwtAuthenticationResponse(jwt, role, username));
    }
    
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        if (userService.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Username is already taken!"));
        }
        
        if (userService.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Email Address already in use!"));
        }
        
        // Creating user's account
        User user = new User(signUpRequest.getEmail(), passwordEncoder.encode(signUpRequest.getPassword()), signUpRequest.getUsername());
        
        Role userRole = roleRepository.findByName(RoleName.ROLE_USER);
        
        user.setRoles(Collections.singleton(userRole));
        
        User result = userService.persist(user);
        
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/users/{username}")
                .buildAndExpand(result.getUsername()).toUri();
        
        return ResponseEntity.created(location).body(new ApiResponse(true, "User registered successfully"));
    }
}
