package com.linkbrary.domain.reminder.service;

import com.linkbrary.common.response.code.ErrorCode;
import com.linkbrary.common.response.exception.handler.UserHandler;
import com.linkbrary.domain.reminder.dto.*;
import com.linkbrary.domain.reminder.entity.UserReminderSetting;
import com.linkbrary.domain.user.entity.Member;
import com.linkbrary.domain.user.repository.UserReminderSettingRepository;
import com.linkbrary.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReminderService {
    private final UserService userService;
    private final UserReminderSettingRepository userReminderSettingRepository;
    public UserReminderSettingResponseDTO getUserReminderSetting() {
        Member member = userService.getMemberFromToken();
        UserReminderSetting userReminderSetting = userReminderSettingRepository.findByMember(member).orElseThrow(() -> new UserHandler(ErrorCode.USER_REMINDER_SETTING_NOT_FOUND));
        return UserReminderSettingResponseDTO.from(userReminderSetting);
    }

    public UserReminderSettingResponseDTO updateUserReminderSetting(CreateUserReminderSettingRequestDTO createUserReminderSettingRequestDTO){
        Member member = userService.getMemberFromToken();
        UserReminderSetting userReminderSetting = userReminderSettingRepository.findByMember(member).orElseThrow(()-> new UserHandler(ErrorCode.USER_REMINDER_SETTING_NOT_FOUND));
        userReminderSetting.updateUserReminderSetting(createUserReminderSettingRequestDTO.getUnreadAlertTime(), createUserReminderSettingRequestDTO.isUnreadTimeAlert(), createUserReminderSettingRequestDTO.getUnreadFolderAlertCount(), createUserReminderSettingRequestDTO.isUnreadFolderAlert());
        userReminderSetting = userReminderSettingRepository.save(userReminderSetting);
        return UserReminderSettingResponseDTO.from(userReminderSetting);
    }

}
