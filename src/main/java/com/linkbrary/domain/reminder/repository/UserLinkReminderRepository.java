package com.linkbrary.domain.reminder.repository;

import com.linkbrary.domain.reminder.entity.UserLinkReminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserLinkReminderRepository extends JpaRepository<UserLinkReminder, Long> {

}
