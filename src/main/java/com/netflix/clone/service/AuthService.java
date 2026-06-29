package com.netflix.clone.service;

import com.netflix.clone.dto.request.UserRequest;
import com.netflix.clone.dto.response.EmailValidationResponse;
import com.netflix.clone.dto.response.LoginResponse;
import com.netflix.clone.dto.response.MessageResponse;

public interface AuthService {
    MessageResponse signup(UserRequest userRequest);

    LoginResponse login(String email, String password);

    EmailValidationResponse validateEmail(String email);

    MessageResponse verifyEmail(String token);

    MessageResponse resendVerification(String email);

    MessageResponse forgotPassword(String email);

    MessageResponse resetPassword(String token, String newPassword);

    MessageResponse changePassword(String email, String currentPassword, String newPassword);

    LoginResponse currentUser(String email);
}
