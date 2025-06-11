// package org.brokong.morakbackend.post.controller;
//
// import lombok.RequiredArgsConstructor;
// import org.brokong.morakbackend.global.response.ResponseDto;
// import org.brokong.morakbackend.post.dto.PostResponseDto;
// import org.brokong.morakbackend.post.service.PostService;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;
//
// @RestController
// @RequiredArgsConstructor
// @RequestMapping("${api.prefix}/posts")
// public class PostController {
//
// 	private final PostService postService;
//
//
// 	@PostMapping
// 	public ResponseEntity<ResponseDto<PostResponseDto>> createPost() {
//
// 		postService.createPost();
//
// 	}
//
// }
