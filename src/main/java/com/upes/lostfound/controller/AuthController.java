package com.upes.lostfound.controller;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.upes.lostfound.dto.LoginRequest;
import com.upes.lostfound.dto.RegisterRequest;
import com.upes.lostfound.dto.VerifyOtpRequest;
import com.upes.lostfound.model.Otp;
import com.upes.lostfound.model.User;
import com.upes.lostfound.repository.OtpRepository;
import com.upes.lostfound.repository.UserRepository;
import com.upes.lostfound.util.JwtService;

import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpRepository otpRepository;
    private final JwtService jwtService;

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return "Email already exists!";
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");
        user.setVerified(false);
        user.setBlocked(false);
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);

        // Generate 6-digit OTP
        String generatedOtp = String.valueOf(new Random().nextInt(900000) + 100000);

        Otp otp = new Otp();
        otp.setEmail(request.getEmail());
        otp.setOtp(generatedOtp);
        otp.setExpiryTime(LocalDateTime.now().plusMinutes(5));

        otpRepository.save(otp);

        return "User registered. OTP: " + generatedOtp; // temporary (for testing)
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestBody VerifyOtpRequest request) {

        Otp otpRecord = otpRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("OTP not found"));

        if (!otpRecord.getOtp().equals(request.getOtp())) {
            return "Invalid OTP!";
        }

        if (otpRecord.getExpiryTime().isBefore(LocalDateTime.now())) {
            return "OTP expired!";
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setVerified(true);
        userRepository.save(user);

        otpRepository.delete(otpRecord);

        return "OTP verified successfully!";
    }

    @PostMapping("/login")
    public Object login(@RequestBody LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return "Invalid password!";
        }

        if (!user.isVerified()) {
            return "Email not verified!";
        }

        if (user.isBlocked()) {
            return "User is blocked!";
        }

        String token = jwtService.generateToken(user.getEmail());

        return java.util.Map.of("token", token);

    }

}
