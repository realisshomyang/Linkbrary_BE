package com.linkbrary.domain.notification;

import com.linkbrary.domain.reminder.entity.UserDirectoryReminder;
import com.linkbrary.domain.reminder.repository.UserDirectoryReminderRepository;
import com.linkbrary.domain.user.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static com.linkbrary.common.util.CallExternalApi.sendNotification;

@Service
@Slf4j
@RequiredArgsConstructor
public class SendNotificationService {
    private final UserDirectoryReminderRepository userDirectoryReminderRepository;

    @Scheduled(cron = "0 * * * * *", zone = "Asia/Seoul")
    public void sendDirectoryReminderByMinute() {
        LocalDateTime currentTime = LocalDateTime.now();
        DayOfWeek currentDay = currentTime.getDayOfWeek();
        LocalTime currentHourMinute = currentTime.toLocalTime().withSecond(0).withNano(0);
        // 현재 시간 ±30초 범위 설정
        LocalTime startRange = currentHourMinute.minusSeconds(30);
        LocalTime endRange = currentHourMinute.plusSeconds(30);
        List<UserDirectoryReminder> userDirectoryReminders = userDirectoryReminderRepository.findAllByReminderDaysAndReminderTimeBetween(currentDay.toString(), startRange, endRange);
        for (UserDirectoryReminder reminder : userDirectoryReminders) {
            Member user = reminder.getMember();
            String token = user.getToken();
            if (token != null) {
                String title = user.getNickname() + "님이 설정하신 시간입니다!";
                String body = "“" + reminder.getUserDirectory().getDirectoryName() + "” 디렉토리를 확인하세요!";
                Long id = reminder.getUserDirectory().getId();
                sendNotification(token, title, body, id.toString());
            }
        }
    }

}

