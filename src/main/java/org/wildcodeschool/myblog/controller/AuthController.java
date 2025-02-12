package org.wildcodeschool.myblog.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.wildcodeschool.myblog.dto.UserRegistrationDTO;
import org.wildcodeschool.myblog.dto.UserLoginDTO;
import org.wildcodeschool.myblog.model.User;
import org.wildcodeschool.myblog.service.AuthenticationService;
import org.wildcodeschool.myblog.service.CustomUserDetailsService;
import org.wildcodeschool.myblog.service.UserService;

import java.util.Set;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationService authenticationService;

    public AuthController(UserService userService, AuthenticationService authenticationService) {
        this.userService = userService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody UserRegistrationDTO userRegistrationDTO) {


        // Enregistrer l'utilisateur avec l'email et le mot de passe
        User registeredUser = userService.registerUser(
                userRegistrationDTO.email(),
                userRegistrationDTO.password(),
                Set.of("USER") // Par défaut, chaque utilisateur aura le rôle "USER"
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<String> authenticate(@RequestBody UserLoginDTO userLoginDTO) {
        String token = authenticationService.authenticate(
                userLoginDTO.getEmail(),
                userLoginDTO.getPassword()
        );
        return ResponseEntity.ok(token);
    }
}
