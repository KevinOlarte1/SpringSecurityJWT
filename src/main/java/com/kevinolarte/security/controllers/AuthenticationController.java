package com.kevinolarte.security.controllers;

import com.kevinolarte.security.dto.LoginUserDto;
import com.kevinolarte.security.dto.RegisterUserDto;
import com.kevinolarte.security.dto.VerifyUserDto;
import com.kevinolarte.security.models.User;
import com.kevinolarte.security.responses.LoginResponse;
import com.kevinolarte.security.services.AuthenticationService;
import com.kevinolarte.security.services.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/auth")
@AllArgsConstructor
public class AuthenticationController {
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;


    @PostMapping("/singup")
    public ResponseEntity<User> register(@RequestBody RegisterUserDto registerUserDto){
        User user = authenticationService.singUp(registerUserDto);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto){
        User userauthentication = authenticationService.authenticate(loginUserDto);
        String token = jwtService.generateToken(userauthentication);
        LoginResponse loginResponse = new LoginResponse(
                token,
                jwtService.getExpirationTime()
        );
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestBody VerifyUserDto verifyUserDto){
        try{
            authenticationService.verifyUser(verifyUserDto);
            return ResponseEntity.ok("AcountVerfied");
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/resend")
    public ResponseEntity<?> resendVerificationCode(@RequestParam String email ){
        try {
            authenticationService.resendVerificationCode(email);
            return ResponseEntity.ok("Verification Code Resent");
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
