package com.nguyenvanlam.demoGK.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nguyenvanlam.demoGK.dto.AuthResponse;
import com.nguyenvanlam.demoGK.dto.RequestLogin;
import com.nguyenvanlam.demoGK.dto.UserRegister;
import com.nguyenvanlam.demoGK.entity.Role;
import com.nguyenvanlam.demoGK.entity.User;
import com.nguyenvanlam.demoGK.repository.RoleRepository;
import com.nguyenvanlam.demoGK.repository.UserRepository;
import com.nguyenvanlam.demoGK.utils.JwtUtil;

@CrossOrigin()
@RestController
@RequestMapping("/api/auth")
public class AuthController {
        // @Autowired
        // AuthenticationManager authenticationManager;

        // @Autowired
        // UserRepository userRepository;

        // @Autowired
        // RoleRepository roleRepository;

        // @Autowired
        // PasswordEncoder encoder;

        // @Autowired
        // JwtUtil jwtUtils;

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder encoder, JwtUtil jwtUtil){
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
    }
    
    @PostMapping("/login")
    private ResponseEntity<AuthResponse> loginUser(@RequestBody RequestLogin req) {
        Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		System.out.println(userDetails.getUsername());
		List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority()).collect(Collectors.toList());		

		String jwt = jwtUtil.generateJwtToken(userDetails.getUsername());
			
		return ResponseEntity.ok(new AuthResponse(jwt, roles.get(0)));
    }

    @PostMapping("/register")
    private ResponseEntity<?> registerUser(@RequestBody UserRegister req) {
       try {
            User user = new User();
            user.setName(req.getName());
            user.setEmail(req.getEmail());
            user.setPassword(encoder.encode(req.getPassword()));
            user.setUsername(req.getUsername());

            User userCheck = userRepository.findByUsername(req.getUsername());
            if(userCheck != null) {
                Map<String, Object> error = new HashMap<>();
                error.put("code", 400);
                error.put("message", "username exists");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
            Role role = roleRepository.findByName(req.getRole());
            user.setRole(role);
            userRepository.save(user);
            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("message", "User registered successfully!");
            return ResponseEntity.ok(result);
       } catch(Exception err) {
            err.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("code", 500);
            error.put("message", err.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
       }
    }
}