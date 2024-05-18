package com.linkbrary.domain.reminder.repository;


import com.linkbrary.domain.link.entity.UserLink;
import com.linkbrary.domain.reminder.entity.UserLinkReminder;
import com.linkbrary.domain.user.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserLinkReminderRepository extends JpaRepository<UserLinkReminder, Long> {
    List<UserLinkReminder> findAllByMember(Member member);
    boolean existsByUserLink(UserLink userLink);

}
