package me.vilius.cerulean.controller;

import me.vilius.cerulean.controller.dto.ErrorResponse;
import me.vilius.cerulean.controller.dto.JwtResponse;
import me.vilius.cerulean.controller.dto.LoginRequest;
import me.vilius.cerulean.controller.dto.SignupRequest;
import me.vilius.cerulean.model.User;
import me.vilius.cerulean.service.UserService;
import me.vilius.cerulean.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserInfo(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated");
        }
        User user = userService.findByUsername(principal.getName());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtUtil.generateToken(userDetails.getUsername());
            return ResponseEntity.ok(new JwtResponse(token));
        } catch (BadCredentialsException e) {
            ErrorResponse errorResponse = new ErrorResponse("Invalid username or password", HttpStatus.UNAUTHORIZED.value());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequest signupRequest) {
        if (userService.findByUsername(signupRequest.getUsername()) != null) {
            return ResponseEntity.badRequest().body("Username is already taken");
        }
        if (userService.findByEmail(signupRequest.getEmail()) != null) {
            return ResponseEntity.badRequest().body("Email is already registered");
        }
        if(signupRequest.getRealName() == null || signupRequest.getEmail() == null ||
        signupRequest.getUsername() == null || signupRequest.getPassword() == null){
            return ResponseEntity.badRequest().body("Missing data in signup request");
        }
        userService.registerUser(signupRequest.toUser());
        return ResponseEntity.ok("User registered successfully");
    }

}
