package com.linkbrary.domain.reminder.repository;

import com.linkbrary.domain.reminder.entity.UserDirectoryReminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserDirectoryReminderRepository extends JpaRepository<UserDirectoryReminder,Long> {
}
