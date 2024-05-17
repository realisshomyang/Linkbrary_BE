package com.linkbrary.domain.reminder.controller;

import com.linkbrary.common.response.ApiResponse;
import com.linkbrary.domain.reminder.dto.*;
import com.linkbrary.domain.reminder.service.ReminderService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reminders")
public class ReminderController {

    private final ReminderService reminderService;

    @Operation(summary = "유저가 설정한 리마인더 설정(10개 ,, etc) 생성하는 api")
    @PutMapping("/users")
    public ApiResponse<UserReminderSettingResponseDTO> createUserReminderSetting(@Valid @RequestBody CreateUserReminderSettingRequestDTO createUserReminderSettingRequestDTO) {
        return ApiResponse.onSuccess(reminderService.updateUserReminderSetting(createUserReminderSettingRequestDTO));
    }

}
