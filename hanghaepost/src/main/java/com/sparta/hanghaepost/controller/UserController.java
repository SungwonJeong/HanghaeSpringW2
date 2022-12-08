package com.sparta.hanghaepost.controller;

import com.sparta.hanghaepost.dto.LoginRequestDto;
import com.sparta.hanghaepost.dto.ResponseDto;
import com.sparta.hanghaepost.dto.SignupRequsetDto;
import com.sparta.hanghaepost.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseDto signup(@RequestBody @Valid SignupRequsetDto signupRequsetDto) {
        return userService.signup(signupRequsetDto);
    }

    @PostMapping("/login")
    public ResponseDto login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) {
        return userService.login(loginRequestDto, response);
    }

}
