package com.netflix.clone.controller;

import com.netflix.clone.dto.request.UserRequest;
import com.netflix.clone.dto.response.MessageResponse;
import com.netflix.clone.dto.response.PageResponse;
import com.netflix.clone.dto.response.UserResponse;
import com.netflix.clone.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<MessageResponse> createUser(@RequestBody UserRequest userRequest) {
        return ResponseEntity.ok(userService.createUser(userRequest));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<MessageResponse> updateUser(@PathVariable Long userId, @RequestBody UserRequest userRequest) {
        return ResponseEntity.ok(userService.updateUser(userId, userRequest));
    }

    @GetMapping
    public ResponseEntity<PageResponse<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
            return ResponseEntity.ok(userService.getUsers(page,size,search));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<MessageResponse> deleteUser(@PathVariable Long userId, Authentication authentication) {
        String currentUserEmail = authentication.getName();
        return ResponseEntity.ok(userService.deleteUser(userId, currentUserEmail));
    }

    @PutMapping("/{userId}/toggle-status")
    public ResponseEntity<MessageResponse> toggleStatus(@PathVariable Long userId, Authentication authentication) {
        String currentUserEmail = authentication.getName();
        return ResponseEntity.ok(userService.toggleUserStatus(userId, currentUserEmail));
    }

    @PutMapping("/{userId}/change-role")
    public ResponseEntity<MessageResponse> changeUserRole(@PathVariable Long userId, @RequestBody UserRequest userRequest) {
        return ResponseEntity.ok(userService.changeUserRole(userId, userRequest));
    }
}
