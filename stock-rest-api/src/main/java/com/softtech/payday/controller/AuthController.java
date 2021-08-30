package com.softtech.payday.controller;

import com.softtech.payday.exception.AppException;
import com.softtech.payday.exception.RestApiException;
import com.softtech.payday.model.role.Role;
import com.softtech.payday.model.role.RoleName;
import com.softtech.payday.model.user.User;
import com.softtech.payday.payload.ApiResponse;
import com.softtech.payday.payload.JwtAuthenticationResponse;
import com.softtech.payday.payload.LoginRequest;
import com.softtech.payday.payload.SignUpRequest;
import com.softtech.payday.repository.RoleRepository;
import com.softtech.payday.repository.UserRepository;
import com.softtech.payday.repository.redis.RedisRepository;
import com.softtech.payday.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.net.URI;
import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final String USER_ROLE_NOT_SET = "User role not set";

    @Autowired
    private JavaMailSender sender;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private RedisRepository redisRepository;

    @PostMapping("/signin")
    public ResponseEntity<JwtAuthenticationResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsernameOrEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtTokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }

    @GetMapping("/confirm/{id}")
    public ResponseEntity<ApiResponse> validateUserEmail(@PathVariable(value = "id") String id) {

        User user = userRepository.findByEmailConfirmAndUserConfirmed(id, false)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("Not found or already confirmed")));


        user.setUserConfirmed(true);
        userRepository.save(user);

        return new ResponseEntity<>(new ApiResponse(Boolean.TRUE, "User just confirmed"), HttpStatus.OK);
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        if (Boolean.TRUE.equals(userRepository.existsByUsername(signUpRequest.getUsername()))) {
            throw new RestApiException(HttpStatus.BAD_REQUEST, "Username is already taken");
        }

        if (Boolean.TRUE.equals(userRepository.existsByEmail(signUpRequest.getEmail()))) {
            throw new RestApiException(HttpStatus.BAD_REQUEST, "Email is already taken");
        }

        String username = signUpRequest.getUsername().toLowerCase();

        String email = signUpRequest.getEmail().toLowerCase();

        String password = passwordEncoder.encode(signUpRequest.getPassword());

        User user = new User(username, email, password);

        List<Role> roles = new ArrayList<>();

        if (userRepository.count() == 0) {
            roles.add(roleRepository.findByName(RoleName.ROLE_USER)
                    .orElseThrow(() -> new AppException(USER_ROLE_NOT_SET)));
            roles.add(roleRepository.findByName(RoleName.ROLE_ADMIN)
                    .orElseThrow(() -> new AppException(USER_ROLE_NOT_SET)));
        } else {
            roles.add(roleRepository.findByName(RoleName.ROLE_USER)
                    .orElseThrow(() -> new AppException(USER_ROLE_NOT_SET)));
        }

        user.setRoles(roles);

        UUID uuidpart1 = UUID.randomUUID();
        UUID uuidpart2 = UUID.randomUUID();
        String uuid = uuidpart1 + "" + uuidpart2;

        user.setEmailConfirm(uuid);
        user.setUserConfirmed(false);
        user.setBalance(BigDecimal.ZERO);

        User result = userRepository.save(user);

        URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/users/{userId}")
                .buildAndExpand(result.getId()).toUri();


        String url = "http://localhost:8080/api/auth/confirm/" + uuidpart1 + uuidpart2;

        sendMail(signUpRequest.getEmail(), url);

        return ResponseEntity.created(location).body(new ApiResponse(Boolean.TRUE, "User registered successfully"));
    }

    public String sendMail(String email, String url) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("paydaytradetrade@gmail.com");
        message.setTo(email);
        message.setSubject("Confirm your email");
        message.setText(url);
        sender.send(message);

        return "success";

    }

}
