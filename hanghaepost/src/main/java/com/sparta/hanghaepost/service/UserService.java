package com.sparta.hanghaepost.service;

import com.sparta.hanghaepost.dto.LoginRequestDto;
import com.sparta.hanghaepost.dto.ResponseDto;
import com.sparta.hanghaepost.dto.SignupRequsetDto;
import com.sparta.hanghaepost.entity.User;
import com.sparta.hanghaepost.entity.UserRoleEnum;
import com.sparta.hanghaepost.jwt.JwtUtil;
import com.sparta.hanghaepost.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    //회원가입
    @Transactional
    public ResponseDto signup(SignupRequsetDto signupRequsetDto) {
        String username = signupRequsetDto.getUsername();
        String password = signupRequsetDto.getPassword();

        //user 중복확인
        Optional<User> found = userRepository.findByUsername(username);
        if (found.isPresent()) {
            throw new IllegalArgumentException("중복된 유저가 있습니다.");
        }

        UserRoleEnum role = UserRoleEnum.USER;

        User user = new User(username, password, role);
        userRepository.save(user);

        return new ResponseDto("회원가입 성공!!", HttpStatus.OK.value());
    }


    @Transactional(readOnly = true)
    public ResponseDto login(LoginRequestDto loginRequestDto, HttpServletResponse response) {
        String username = loginRequestDto.getUsername();
        String password = loginRequestDto.getPassword();

        //사용자 확인
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new IllegalArgumentException("등록된 사용자가 없습니다.")
        );

        //비밀번호 확인
        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, jwtUtil.createToken(user.getUsername(), user.getRole()));

        return new ResponseDto("로그인 성공!!", HttpStatus.OK.value());

    }
}

