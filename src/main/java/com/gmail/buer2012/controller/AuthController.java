package com.gmail.buer2012.controller;

import com.gmail.buer2012.config.CustomProperties;
import com.gmail.buer2012.entity.AuthProvider;
import com.gmail.buer2012.entity.EmailConfirmationToken;
import com.gmail.buer2012.entity.Role;
import com.gmail.buer2012.entity.RoleName;
import com.gmail.buer2012.entity.User;
import com.gmail.buer2012.payload.ApiResponse;
import com.gmail.buer2012.payload.JwtAuthenticationResponse;
import com.gmail.buer2012.payload.RestorePasswordRequest;
import com.gmail.buer2012.payload.SignInRequest;
import com.gmail.buer2012.payload.SignUpRequest;
import com.gmail.buer2012.repository.EmailConfirmationTokenRepository;
import com.gmail.buer2012.repository.RoleRepository;
import com.gmail.buer2012.security.JwtTokenProvider;
import com.gmail.buer2012.security.UserPrincipal;
import com.gmail.buer2012.service.EmailSenderService;
import com.gmail.buer2012.service.UserService;
import com.gmail.buer2012.utils.ApiUtils;
import java.util.Collections;
import java.util.Optional;
import javax.validation.Valid;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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
    private CustomProperties customProperties;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody SignInRequest signInRequest) {
        Optional<User> userOptional = userService.findByEmail(signInRequest.getUsernameOrEmail());

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (!user.getEnabled()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"message\": \"You should confirm email first!\"}");
            }
            if(!user.getProvider().equals(AuthProvider.local)) {
                return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "This account was signed up with Facebook, use Facebook login button to login into this account"));
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
        Optional<User> userFromDb = userService.findByEmail(requestEmail);
        if (userFromDb.isPresent()) {
            if (!userFromDb.get().getProvider().equals(AuthProvider.local)) {
                return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "You are trying to sign up using Facebook mail. Please login using Facebook account"));
            }
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
        emailSenderService.sendEmail(requestEmail, location, "Confirm your mail");
    }

    @GetMapping("/confirmEmail")
    public ResponseEntity<?> confirmEmail(@RequestParam String token) {
        Optional<EmailConfirmationToken> emailConfirmationToken =
            emailConfirmationTokenRepository.findByToken(token);
        if(emailConfirmationToken.isPresent()) {
            User user = emailConfirmationToken.get().getUser();
            user.setEnabled(true);
            userService.updateUser(user);

            String coderunnerUri =
                customProperties.getFrontUrl() + "/login?confirmed=true";
            return ResponseEntity.status(HttpStatus.SEE_OTHER)
                .header(HttpHeaders.LOCATION, coderunnerUri).build();
        }
        else {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, "Can't verify token"));
        }
    }

    @PostMapping("/requestRestore")
    public ResponseEntity<?> requestRestore(@RequestBody RestorePasswordRequest restorePasswordRequest) {
        Optional<User> user = userService.findByEmail(restorePasswordRequest.getEmail());
        if(user.isPresent()) {
            if(user.get().getProvider() == AuthProvider.local) {
                String confirmationToken = RandomStringUtils
                    .randomAlphanumeric(32);
                emailConfirmationTokenRepository.save(
                    new EmailConfirmationToken(user.get(), confirmationToken));
                String coderunnerUri =
                    customProperties.getFrontUrl() + "/restore?token="
                        + confirmationToken;
                emailSenderService
                    .sendEmail(user.get().getEmail(), coderunnerUri, "Password recovering");
                return ResponseEntity.ok(0);
            }
            else {
                return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Can't change password for account registered using Facebook"));
            }
        }
        return ResponseEntity.ok(0);
    }

    @PostMapping("/confirmRestore")
    public ResponseEntity<?> restorePassword(@RequestBody RestorePasswordRequest restorePasswordRequest) {
        Optional<EmailConfirmationToken> emailConfirmationToken =
            emailConfirmationTokenRepository.findByToken(restorePasswordRequest.getToken());
        if(emailConfirmationToken.isPresent()) {
            User user = emailConfirmationToken.get().getUser();
            emailConfirmationTokenRepository.delete(emailConfirmationToken.get());
            user.setPassword(
                passwordEncoder.encode(restorePasswordRequest.getPassword()));
            userService.updateUser(user);
            return ResponseEntity.ok(0);
        }
        else {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, "This link is no longer active, please retry restoring the password"));
        }
    }
}
