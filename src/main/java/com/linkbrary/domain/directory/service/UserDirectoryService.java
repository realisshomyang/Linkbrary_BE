package com.linkbrary.domain.directory.service;


import com.linkbrary.common.response.ApiResponse;
import com.linkbrary.common.response.code.ErrorCode;
import com.linkbrary.common.response.exception.handler.UserDirectoryHandler;
import com.linkbrary.domain.directory.dto.CreateUserDirectoryRequestDTO;
import com.linkbrary.domain.directory.dto.UserDirectoryResponseDTO;
import com.linkbrary.domain.directory.entity.UserDirectory;
import com.linkbrary.domain.directory.repository.UserDirectoryRepository;
import com.linkbrary.domain.user.entity.Member;
import com.linkbrary.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserDirectoryService {

    private final UserDirectoryRepository userDirectoryRepository;
    private final UserService userService;

    @Transactional
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

    @Transactional(readOnly = true)
    public UserDirectoryResponseDTO getDirectory(Long id) {
        UserDirectory userDirectory = userDirectoryRepository.findById(id).orElseThrow(() -> new UserDirectoryHandler(ErrorCode.DIRECTORY_NOT_FOUND));
        return UserDirectoryResponseDTO.from(userDirectory);
    }
}
