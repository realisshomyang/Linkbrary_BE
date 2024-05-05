package com.linkbrary.domain.directory.controller;


import com.linkbrary.common.response.ApiResponse;
import com.linkbrary.domain.directory.dto.CreateUserDirectoryRequestDTO;
import com.linkbrary.domain.directory.dto.UserDirectoryResponseDTO;
import com.linkbrary.domain.directory.service.UserDirectoryService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/directories")
public class UserDirectoryController {

    private final UserDirectoryService userDirectoryService;

    @Operation(summary = "디렉토리 생성 api")
    @PostMapping
    public ApiResponse<UserDirectoryResponseDTO> createDirectory(@Valid @RequestBody CreateUserDirectoryRequestDTO createUserDirectoryRequestDTO) {
        return ApiResponse.onSuccess(userDirectoryService.createDirectory(createUserDirectoryRequestDTO));
    }
}
