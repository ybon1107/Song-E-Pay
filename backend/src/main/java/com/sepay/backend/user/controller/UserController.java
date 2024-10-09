package com.sepay.backend.user.controller;

import com.sepay.backend.user.dto.UserDTO;
import com.sepay.backend.user.service.UserService;
import com.sepay.backend.user.service.SMSService;  // SMSService import
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    final UserService userService;
    final SMSService smsService;

    // 로그인 API
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String userId, @RequestParam String password) {
        try {
            UserDTO user = userService.login(userId, password);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            System.out.println("Login error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during login.");
        }
    }

    @PostMapping("/send-verification-code")
    public ResponseEntity<?> sendVerificationCode(@RequestParam("phoneNo") String phoneNo) {
        try {
            // phoneNo 확인 로그 추가
            log.info("Request to send verification code to phone number: " + phoneNo);

            // 인증 코드 생성 및 전송
            String verificationCode = smsService.generateVerificationCode();

            // 생성된 인증 코드 로그 추가
            log.info("Generated verification code: " + verificationCode);

            smsService.sendVerificationCode(phoneNo, verificationCode);

            // 인증번호 전송 성공 로그
            log.info("Verification code sent successfully to " + phoneNo);

            return ResponseEntity.ok("Verification code sent to " + phoneNo);
        } catch (Exception e) {
            // 에러 로그
            log.error("Error sending verification code: " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send verification code.");
        }
    }

    // SMS 인증 코드 검증 API
    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@RequestParam String phoneNo, @RequestParam String code) {
        boolean isValid = smsService.verifyCode(phoneNo, code);  // 서비스에서 인증 코드 검증
        if (isValid) {
            return ResponseEntity.ok("Verification successful. Proceed to next step.");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Verification failed: Invalid code.");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDTO userDTO) {
        try {
            userService.register(userDTO);
            return ResponseEntity.ok("success");
        } catch (Exception e) {
            log.error("Error registering user: {}", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Registration failed.");
        }
    }
}
