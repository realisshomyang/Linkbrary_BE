package com.linkbrary.domain.directory.service;


import com.linkbrary.common.response.ApiResponse;
import com.linkbrary.common.response.code.ErrorCode;
import com.linkbrary.common.response.exception.handler.UserDirectoryHandler;
import com.linkbrary.domain.directory.dto.CreateUserDirectoryRequestDTO;
import com.linkbrary.domain.directory.dto.UpdateDirectoryNameRequestDTO;
import com.linkbrary.domain.directory.dto.UpdateUserDirectoryLocationRequestDTO;
import com.linkbrary.domain.directory.dto.UserDirectoryResponseDTO;
import com.linkbrary.domain.directory.entity.UserDirectory;
import com.linkbrary.domain.directory.repository.UserDirectoryRepository;
import com.linkbrary.domain.link.repository.UserLinkRepository;
import com.linkbrary.domain.user.entity.Member;
import com.linkbrary.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDirectoryService {
    private final UserDirectoryRepository userDirectoryRepository;
    private final UserService userService;

    public UserDirectoryResponseDTO createDirectory(CreateUserDirectoryRequestDTO createUserDirectoryRequestDTO) {
        Member member = userService.getMemberFromToken();
        Long parentFolderId = createUserDirectoryRequestDTO.getParentFolderId();
        UserDirectory userDirectory;
        if (parentFolderId != null) {
            UserDirectory parentDirectory = userDirectoryRepository.findById(parentFolderId)
                    .orElseThrow(() -> new UserDirectoryHandler(ErrorCode.DIRECTORY_NOT_FOUND));

            userDirectory = UserDirectory.builder()
                    .directoryName(createUserDirectoryRequestDTO.getDirectoryName())
                    .parentFolder(parentDirectory)
                    .member(member)
                    .build();
        } else {
            userDirectory = UserDirectory.builder()
                    .directoryName(createUserDirectoryRequestDTO.getDirectoryName())
                    .member(member)
                    .build();
        }
        userDirectoryRepository.save(userDirectory);
        return UserDirectoryResponseDTO.from(userDirectory);
    }
}
