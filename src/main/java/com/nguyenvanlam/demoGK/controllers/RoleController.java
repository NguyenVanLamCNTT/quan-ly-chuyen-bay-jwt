package com.nguyenvanlam.demoGK.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nguyenvanlam.demoGK.dto.RoleRequest;
import com.nguyenvanlam.demoGK.entity.Role;
import com.nguyenvanlam.demoGK.repository.RoleRepository;

@CrossOrigin()
@RestController
@RequestMapping("api/roles")
public class RoleController {
    
    private final RoleRepository roleRepository;
    public RoleController(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @PostMapping("")
    private ResponseEntity<?> createRole(@RequestBody RoleRequest req) {
        Role roleCheck = roleRepository.findByName(req.getName());
        if(roleCheck != null) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 400);
            error.put("message", "username exists");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
        Role role = new Role();
        role.setName(req.getName());
        roleRepository.save(role);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "Role created successfully!");
        return ResponseEntity.ok(result);
    }

    @GetMapping("")
    private ResponseEntity<List<Role>> getRoles() {
        return ResponseEntity.ok().body(roleRepository.findAll());
    }

}
