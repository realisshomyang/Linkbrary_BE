package com.linkbrary.domain.reminder.repository;

import com.linkbrary.domain.directory.entity.UserDirectory;
import com.linkbrary.domain.reminder.entity.UserDirectoryReminder;
import com.linkbrary.domain.user.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDirectoryReminderRepository extends JpaRepository<UserDirectoryReminder,Long> {
    List<UserDirectoryReminder> findAllByMember(Member member);
    boolean existsByUserDirectory(UserDirectory userDirectory);

}
