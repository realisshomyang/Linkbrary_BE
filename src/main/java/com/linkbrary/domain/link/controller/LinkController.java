package com.linkbrary.domain.link.controller;

import com.linkbrary.common.response.ApiResponse;
import com.linkbrary.domain.link.dto.CreateLinkRequestDTO;
import com.linkbrary.domain.link.service.LinkService;
import io.swagger.v3.oas.annotations.Operation;
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

}
