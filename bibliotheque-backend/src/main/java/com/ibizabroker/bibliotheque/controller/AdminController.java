package com.ibizabroker.bibliotheque.controller;

import com.ibizabroker.bibliotheque.entity.UserResponse;
import com.ibizabroker.bibliotheque.entity.Users;
import com.ibizabroker.bibliotheque.service.UserManagementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UserManagementService userManagementService;

    public AdminController(UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }

    @PostMapping("/users")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public ResponseEntity<UserResponse> addUserByAdmin(@RequestBody Users user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userManagementService.create(user));
    }

    @GetMapping("/users")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'BIBLIOTHECAIRE')")
    public List<UserResponse> getAllUsers() {
        return userManagementService.list();
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Integer id) {
        return ResponseEntity.ok(userManagementService.getById(id));
    }

    @PutMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Integer id, @RequestBody Users userDetails) {
        return ResponseEntity.ok(userManagementService.update(id, userDetails));
    }
}
