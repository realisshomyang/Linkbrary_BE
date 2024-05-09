package com.linkbrary.domain.link.controller;

import com.linkbrary.common.response.ApiResponse;
import com.linkbrary.domain.link.dto.CreateUserLinkRequestDTO;
import com.linkbrary.domain.link.dto.CreateLinkRequestDTO;
import com.linkbrary.domain.link.dto.UpdateLinkLocationDTO;
import com.linkbrary.domain.link.dto.UpdateUserLinkRequestDTO;
import com.linkbrary.domain.link.service.LinkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/links")
public class LinkController {

    private final LinkService linkService;

    @Operation(summary = "단일 링크(크롤링+ gpt 처리) 생성용 api user 사용 x for admin")
    @PostMapping
    public ApiResponse createLink(@Valid @RequestBody CreateLinkRequestDTO createLinkRequestDTO) {
        return linkService.createLink(createLinkRequestDTO);
    }

    @Operation(summary = "단일 링크(크롤링+ gpt 처리) 조회용 api user 사용 x for admin")
    @GetMapping
    public ApiResponse getLink(@Parameter(description = "단일 링크(user링크 아님) ID") @RequestParam Long linkId) {
        return linkService.getLink(linkId);
    }

    @Operation(summary = "유저 링크 저장")
    @PostMapping("/users")
    public ApiResponse createUserLink(@Valid @RequestBody CreateUserLinkRequestDTO createUserLinkRequestDTO) {
        return linkService.createUserLink(createUserLinkRequestDTO);
    }

    @Operation(summary = "유저 링크 삭제")
    @DeleteMapping("/users/{id}")
    private ApiResponse deleteUserLink(@PathVariable Long id) {
        return linkService.deleteUserLink(id);
    }

    @Operation(summary = "유저 링크 업데이트")
    @PutMapping("/users")
    private ApiResponse updateUserLink(@Valid @RequestBody UpdateUserLinkRequestDTO updateLinkRequestDTO) {
        return linkService.updateUserLink(updateLinkRequestDTO);
    }

}
