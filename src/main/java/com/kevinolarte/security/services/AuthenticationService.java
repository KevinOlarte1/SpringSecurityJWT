package com.kevinolarte.security.services;

import com.kevinolarte.security.dto.LoginUserDto;
import com.kevinolarte.security.dto.RegisterUserDto;
import com.kevinolarte.security.dto.VerifyUserDto;
import com.kevinolarte.security.models.User;
import com.kevinolarte.security.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    public User singUp(RegisterUserDto input){
        User user = new User(input.getUsername(), input.getEmail(), passwordEncoder.encode(input.getPassword()));
        user.setVerificationCode(generateVerificationCode());
        user.setVerificationExpiration(LocalDateTime.now().plusMinutes(15));
        user.setEnable(false);
        sendVerificationEmail(user);
        return userRepository.save(user);

    }

    public User authenticate(LoginUserDto input){

        User user = userRepository.findByEmail(input.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User with email " + input.getEmail() + " not found"));

        if(!user.isEnable()){
            throw new RuntimeException("User with email " + input.getEmail() + " is not enabled");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );
        return user;
    }

    public void verifyUser(VerifyUserDto input){
        Optional<User> optionalUser = userRepository.findByEmail(input.getEmail());
        if(optionalUser.isPresent()){
            User user = optionalUser.get();

            if(!user.getVerificationExpiration().isBefore(LocalDateTime.now())){
                throw new RuntimeException("User with email " + input.getEmail() + " is expired");
            }
            if (!user.getVerificationCode().equals(input.getVerificationCode())){
                user.setEnable(false);
                user.setVerificationCode(null);
                user.setVerificationExpiration(null);
                userRepository.save(user);
            }else{
                throw new RuntimeException("Invalid verification code");
            }
        }
        else{
            throw new RuntimeException("User with email " + input.getEmail() + " not found");
        }
    }
}
