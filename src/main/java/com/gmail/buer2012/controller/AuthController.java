package com.gmail.buer2012.controller;


import com.gmail.buer2012.entity.*;
import com.gmail.buer2012.payload.ApiResponse;
import com.gmail.buer2012.payload.JwtAuthenticationResponse;
import com.gmail.buer2012.payload.SignInRequest;
import com.gmail.buer2012.payload.SignUpRequest;
import com.gmail.buer2012.repository.EmailConfirmationTokenRepository;
import com.gmail.buer2012.repository.RoleRepository;
import com.gmail.buer2012.security.JwtTokenProvider;
import com.gmail.buer2012.security.UserPrincipal;
import com.gmail.buer2012.service.EmailSenderService;
import com.gmail.buer2012.service.UserService;
import com.gmail.buer2012.utils.ApiUtils;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.util.Collections;
import java.util.Optional;

@AllArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {
    
    private AuthenticationManager authenticationManager;
    private UserService userService;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private JwtTokenProvider tokenProvider;
    private ApiUtils apiUtils;
    private EmailConfirmationTokenRepository emailConfirmationTokenRepository;
    private EmailSenderService emailSenderService;
    
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody SignInRequest signInRequest) {
        Optional<User> userOptional = userService.findByEmail(signInRequest.getUsernameOrEmail());
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (!user.getEnabled()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"message\": \"You should confirm email first!\"}");
            }
        }
        
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                signInRequest.getUsernameOrEmail(),
                signInRequest.getPassword()
        );
        Authentication authentication = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        String jwt = tokenProvider.generateToken(authentication);
        RoleName role = apiUtils.getRoleFromAuthorities(authentication.getAuthorities());
        UserPrincipal userPrincipal = ((UserPrincipal) authentication.getPrincipal());
        String username = userPrincipal.getUsername();
        AuthProvider provider = userPrincipal.getProvider();
        return ResponseEntity.ok().body(new JwtAuthenticationResponse(jwt, role, username, provider));
    }
    
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        String requestUsername = signUpRequest.getUsername();
        String requestEmail = signUpRequest.getEmail();
        String requestPassword = signUpRequest.getPassword();
        
        if (userService.existsByEmail(requestEmail)) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Email Address is already in use!"));
        }
        
        User user = new User(requestEmail, passwordEncoder.encode(requestPassword), requestUsername, AuthProvider.local, false);
        Role userRole = roleRepository.findByName(RoleName.ROLE_USER);
        user.setRoles(Collections.singleton(userRole));
        User persistedUser = userService.persist(user);
        
        sendConfirmationEmail(requestEmail, persistedUser);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(persistedUser);
    }
    
    private void sendConfirmationEmail(String requestEmail, User user) {
        String emailConfirmationToken = RandomStringUtils.randomAlphanumeric(32);
        emailConfirmationTokenRepository.save(new EmailConfirmationToken(user, emailConfirmationToken));
        String location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/auth/confirmEmail")
                .queryParam("token", emailConfirmationToken)
                .build().toString();
        emailSenderService.sendEmail(requestEmail, location);
    }
    
    @GetMapping("/confirmEmail")
    public ResponseEntity<?> confirmEmail(@RequestParam String token) {
        User user = emailConfirmationTokenRepository.findByToken(token).getUser();
        user.setEnabled(true);
        userService.updateUser(user);
        
        String coderunnerUri = "https://coderunner.tcomad.tk/login?confirmed=true";
        return ResponseEntity.status(HttpStatus.SEE_OTHER).header(HttpHeaders.LOCATION, coderunnerUri).build();
    }
}
