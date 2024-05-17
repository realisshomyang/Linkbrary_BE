package com.linkbrary.domain.reminder.dto;

import com.linkbrary.domain.reminder.entity.UserReminderSetting;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@Builder
public class UserReminderSettingResponseDTO {
    private Long id;
    private LocalTime unreadAlertTime;
    private boolean unreadTimeAlert;
    private Long unreadFolderAlertCount;
    private boolean unreadFolderAlert;

    public static UserReminderSettingResponseDTO from(UserReminderSetting entity) {
        return UserReminderSettingResponseDTO.builder()
                .id(entity.getId())
                .unreadAlertTime(entity.getUnreadAlertTime())
                .unreadTimeAlert(entity.isUnreadTimeAlert())
                .unreadFolderAlertCount(entity.getUnreadFolderAlertCount())
                .unreadFolderAlert(entity.isUnreadFolderAlert())
                .build();
    }
}
