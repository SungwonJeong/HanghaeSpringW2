package com.sparta.hanghaepost.dto;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PostListResponseDto extends ResponseDto{
    List<PostResponseDto> postList = new ArrayList<>();

    public PostListResponseDto() {
        super("게시글 목록 조회 성공", HttpStatus.OK.value());
    }

    public void addPost(PostResponseDto responseDto) {
        postList.add(responseDto);
    }
}
