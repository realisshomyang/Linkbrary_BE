package com.linkbrary.domain.directory.repository;

import com.linkbrary.domain.directory.entity.UserDirectory;
import com.linkbrary.domain.user.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDirectoryRepository extends JpaRepository<UserDirectory,Long> {
    List<UserDirectory> findByMember(Member member);
}
