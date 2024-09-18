package tn.biramgroup.pointage.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import tn.biramgroup.pointage.Repository.RoleRepository;
import tn.biramgroup.pointage.model.ERole;
import tn.biramgroup.pointage.model.User;
import tn.biramgroup.pointage.services.implement.UserService;
import tn.biramgroup.pointage.springSecurityJwt.AuthRequest;
import tn.biramgroup.pointage.springSecurityJwt.AuthResponse;
import tn.biramgroup.pointage.springSecurityJwt.JwtTokenUtil;

@RestController
@CrossOrigin(origins = "http://192.168.1.18:4200")
public class AuthController {
    @Autowired
    AuthenticationManager authManager;
    @Autowired
    JwtTokenUtil jwtUtil;
    @Autowired
    UserService userService;

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody @Valid AuthRequest request) {
        try {
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(), request.getPassword())
            );

            User user = (User) authentication.getPrincipal();
            String accessToken = jwtUtil.generateAccessToken(user);
            AuthResponse response = new AuthResponse(
                    user.getEmail(),
                    accessToken,
                    user.getId(),
                    user.getUsername(),
                    user.getRoles(),
                    user.getNom(),
                    user.getPrenom(),
                    user.getStatus(),
                    user.getModified(),
                    user.getTotalMinutesWorked()
            );

            return ResponseEntity.ok().body(response);

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
        }
    }

    @PostMapping("/auth/signup")
    public ResponseEntity<User> addNewUser(@RequestBody User user){
        User newUser = userService.addNewUser(user);

        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }
}
