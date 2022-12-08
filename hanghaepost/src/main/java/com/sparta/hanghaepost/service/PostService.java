package com.sparta.hanghaepost.service;

import com.sparta.hanghaepost.dto.*;
import com.sparta.hanghaepost.entity.Post;
import com.sparta.hanghaepost.entity.User;
import com.sparta.hanghaepost.jwt.JwtUtil;
import com.sparta.hanghaepost.repository.PostRepository;
import com.sparta.hanghaepost.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;


    //게시글 작성
    @Transactional
    public PostResponseDto writePost(PostRequestDto requestDto, HttpServletRequest request) {

        String token = jwtUtil.resolveToken(request);
        Claims claims;

        //유효한 토큰일 경우에만 게시글 작성 가능
        if (token != null) {
            if (jwtUtil.validateToken(token)) {
                claims = jwtUtil.getUserInfoFromToken(token);
            } else {
                throw new IllegalArgumentException("Token Error");
            }

            //토큰에서 가져온 사용자 정보를 사용하여 DB 조회
            User user = userRepository.findByUsername(claims.getSubject()).orElseThrow(
                    () -> new IllegalArgumentException("사용자가 존재하지 않습니다.")
            );

            Post post = new Post(requestDto, user.getUsername());
            postRepository.save(post);
            return new PostResponseDto(post);
        } else {
            return null;
        }
    }

    //게시글 전체 조회
    @Transactional(readOnly = true)
    public List<Post> getPosts() {
        return postRepository.findAllByOrderByModifiedAtDesc();
    }

    //선택한 게시글 조회
    @Transactional(readOnly = true)
    public PostResponseDto getPost(Long id) {
        Post post = checkPost(postRepository, id);
        return new PostResponseDto(post);
    }

    //선택한 게시글 수정
    @Transactional
    public PostResponseDto updatePost(Long id, PostRequestDto requestDto, HttpServletRequest request) {
        //Request에서 Token가져오기
        String token = jwtUtil.resolveToken(request);
        Claims claims;

        //토큰이 있는 경우에만 선택한 게시글 수정
        if (token != null) {
            //token 검증
            if (jwtUtil.validateToken(token)) {
                claims = jwtUtil.getUserInfoFromToken(token);
            } else {
                throw new IllegalArgumentException("Token Error");
            }

            //토큰에서 가져온 사용자 정보를 사용하여 DB조회
            User user = userRepository.findByUsername(claims.getSubject()).orElseThrow(
                    () -> new IllegalArgumentException("사용자가 존재하지 않습니다.")
            );

            //로그인한 user가 선택한 게시글이 맞는지 확인
//            Post post = postRepository.findByIdAndUserId(id, user.getId()).orElseThrow(
//                    () -> new IllegalArgumentException("선택한 게시글을 찾을 수 없습니다.")
//            );
            Post post = checkPost(postRepository, id);
            post.update(requestDto);
            return new PostResponseDto(post);
        } else {
            return null;
        }
    }

    //선택한 게시글 삭제
    @Transactional
    public ResponseDto deletePost(Long id, HttpServletRequest request) {
        //Request에서 Token가져오기
        String token = jwtUtil.resolveToken(request);
        Claims claims;

        //토큰이 있는 경우에만 선택한 게시글 수삭제
        if (token != null) {
            //token 검증
            if (jwtUtil.validateToken(token)) {
                claims = jwtUtil.getUserInfoFromToken(token);
            } else {
                throw new IllegalArgumentException("Token Error");
            }

            //토큰에서 가져온 사용자 정보를 사용하여 DB조회
            User user = userRepository.findByUsername(claims.getSubject()).orElseThrow(
                    () -> new IllegalArgumentException("사용자가 존재하지 않습니다.")
            );

            //로그인한 user가 선택한 게시글이 맞는지 확인
//            Post post = postRepository.findByIdAndUserId(id, user.getId()).orElseThrow(
//                    () -> new IllegalArgumentException("선택한 게시글을 찾을 수 없습니다.")
//            );
            Post post = checkPost(postRepository, id);
            postRepository.delete(post);
            return new ResponseDto("게시글 삭제 성공",HttpStatus.OK.value());
        } else {
            return null;
        }
    }

    private Post checkPost(PostRepository postRepository, Long id) {
        return postRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("선택한 게시글을 찾을 수 없습니다.")
        );
    }
}