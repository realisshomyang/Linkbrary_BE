package com.linkbrary.domain.link.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.linkbrary.common.response.ApiResponse;
import com.linkbrary.common.response.code.ErrorCode;
import com.linkbrary.common.response.exception.handler.UserDirectoryHandler;
import com.linkbrary.common.response.exception.handler.UserLinkHandler;
import com.linkbrary.domain.directory.entity.UserDirectory;
import com.linkbrary.domain.directory.repository.UserDirectoryRepository;
import com.linkbrary.domain.directory.service.UserDirectoryService;
import com.linkbrary.domain.link.dto.*;
import com.linkbrary.domain.link.entity.Link;
import com.linkbrary.domain.link.entity.UserLink;
import com.linkbrary.domain.link.repository.LinkRepository;
import com.linkbrary.domain.link.repository.UserLinkRepository;
import com.linkbrary.domain.user.entity.Member;
import com.linkbrary.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;

import static com.linkbrary.common.util.CallExternalApi.*;


@Service
@RequiredArgsConstructor
public class LinkService {

    private final LinkRepository linkRepository;
    private final UserLinkRepository userLinkRepository;
    private final UserService userService;
    private final UserDirectoryRepository userDirectoryRepository;
    private final UserDirectoryService userDirectoryService;

    public ApiResponse createLink(CreateLinkRequestDTO createLinkRequestDTO) {
        String jsonString = callExternalGetApi(createLinkRequestDTO.getUrl(), 1);
        Link newLink = mapJsonToLink(jsonString, createLinkRequestDTO.getUrl());
        linkRepository.save(newLink);
        return ApiResponse.onSuccess("hello");
    }

    public ApiResponse getLink(Long linkId) {
        return ApiResponse.onSuccess(LinkResponseDTO.from(linkRepository.findById(linkId).orElseThrow(() -> new UserLinkHandler(ErrorCode.LINK_NOT_FOUND))));
    }

    public ApiResponse createUserLink(CreateUserLinkRequestDTO createUserLinkRequestDTO) {
        Member member = userService.getMemberFromToken();
        if (userLinkRepository.existsByMemberAndUrl(member.getId(), createUserLinkRequestDTO.getUrl())) {
            throw new UserLinkHandler(ErrorCode.LINK_ALREADY_EXIST);
        }
        if (linkRepository.existsByUrl(createUserLinkRequestDTO.getUrl())) {
            Link link = getExistingLink(createUserLinkRequestDTO.getUrl());
            if (createUserLinkRequestDTO.isAutoFolderSave()) {
                return handleAutoFolderSave(createUserLinkRequestDTO, link, member);
            } else {
                return handleManualFolderSave(createUserLinkRequestDTO, link, member);
            }
        } else {
            Link newLink = createLinkEntity(createUserLinkRequestDTO);
            linkRepository.save(newLink);
            if (createUserLinkRequestDTO.isAutoFolderSave()) {
                return handleAutoFolderSave(createUserLinkRequestDTO, newLink, member);
            } else {
                return handleManualFolderSave(createUserLinkRequestDTO, newLink, member);
            }
        }
    }

    private Link getExistingLink(String url) {
        return linkRepository.findByUrl(url)
                .orElseThrow(() -> new UserLinkHandler(ErrorCode.LINK_NOT_FOUND));
    }

    public Link createLinkEntity(CreateUserLinkRequestDTO makeLinkRequestDTO) {
        String jsonString = callExternalGetApi(makeLinkRequestDTO.getUrl(), 1);
        Link newLink = mapJsonToLink(jsonString, makeLinkRequestDTO.getUrl());
        linkRepository.save(newLink);
        return newLink;
    }

    public static Link mapJsonToLink(String jsonString, String url) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(jsonString);
            JsonNode embedNode = jsonNode.get("embed");
            float[] vectorEmbedding = new float[3072];
            if (embedNode != null && embedNode.isArray()) {
                for (int i = 0; i < embedNode.size(); i++) {
                    vectorEmbedding[i] = (float) embedNode.get(i).asDouble();
                }
            }
            return Link.builder()
                    .title(jsonNode.get("title").asText())
                    .content(jsonNode.get("content").asText())
                    .summary(jsonNode.get("summary").asText())
                    .url(url)
                    .thumbnail(jsonNode.get("thumbnail").asText())
                    .embedding(vectorEmbedding)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to map JSON to Link object", e);
        }
    }

    private ApiResponse handleAutoFolderSave(CreateUserLinkRequestDTO createUserLinkRequestDTO, Link link, Member member) {
        // 모든 사용자 디렉토리 이름 가져오기
        String userDirectories = userDirectoryService.getAllDirectoryNames();
        // Replace newline characters with escaped newline characters
        // 외부 API 호출하여 DirectoryInfo 가져오기
        DirectoryInfo directoryInfoOpt = handleDirectoryRecommendationPostRequest(link.getSummary() + "\n 제목: " + link.getTitle(), userDirectories, "/directory");
        System.out.println(directoryInfoOpt.getId());
        System.out.println(directoryInfoOpt.getName());
        // DirectoryInfo의 ID가 null인지 확인
        if (directoryInfoOpt.getId() == null) {
            // ID가 null인 경우 새 디렉토리 생성
            UserDirectory newDirectory = UserDirectory.builder()
                    .directoryName(directoryInfoOpt.getName())
                    .member(member)
                    .parentFolder(null)
                    .build();
            userDirectoryRepository.save(newDirectory);

            // 새 디렉토리에 UserLink 생성 후 저장
            UserLink userLink = createUserLink(createUserLinkRequestDTO, link, member, newDirectory);
            userLinkRepository.save(userLink);
            // 성공 응답 반환
            return ApiResponse.onSuccess(UserLinkResponseDTO.from(userLink));
        } else {
            // ID가 존재하는 경우 해당 ID로 UserDirectory 찾기
            UserDirectory userDirectory = userDirectoryRepository.findById(directoryInfoOpt.getId()).orElseGet(() -> {
                // 디렉토리가 존재하지 않으면 새 디렉토리 생성
                UserDirectory newDirectory = UserDirectory.builder()
                        .directoryName(directoryInfoOpt.getName())
                        .member(member)
                        .parentFolder(null)
                        .build();
                userDirectoryRepository.save(newDirectory);
                return newDirectory;
            });
            // 찾거나 생성된 디렉토리에 UserLink 생성 후 저장
            UserLink userLink = createUserLink(createUserLinkRequestDTO, link, member, userDirectory);
            userLinkRepository.save(userLink);
            // 성공 응답 반환
            return ApiResponse.onSuccess(UserLinkResponseDTO.from(userLink));
        }
    }


    private ApiResponse handleManualFolderSave(CreateUserLinkRequestDTO createUserLinkRequestDTO, Link link, Member member) {
        UserDirectory userDirectory = userDirectoryRepository.findById(createUserLinkRequestDTO.getFolderId())
                .orElseThrow(() -> new UserDirectoryHandler(ErrorCode.DIRECTORY_NOT_FOUND));
        UserLink userLink = createUserLink(createUserLinkRequestDTO, link, member, userDirectory);
        userLinkRepository.save(userLink);
        return ApiResponse.onSuccess(UserLinkResponseDTO.from(userLink));
    }

    private UserLink createUserLink(CreateUserLinkRequestDTO createUserLinkRequestDTO, Link link, Member member, UserDirectory userDirectory) {
        String title = createUserLinkRequestDTO.isAutoTitleSave() ? link.getTitle() : createUserLinkRequestDTO.getTitle();
        return UserLink.builder()
                .userDirectory(userDirectory)
                .title(title)
                .summary(link.getSummary())
                .memo(createUserLinkRequestDTO.getMemo())
                .link(link)
                .member(member)
                .build();
    }

    public String getDirectory() {
        String userDirectories = userDirectoryService.getAllDirectoryNames();
        // Replace newline characters with escaped newline characters
        Link link = linkRepository.findById(12L).orElseThrow(() -> new UserLinkHandler(ErrorCode.LINK_NOT_FOUND));
        DirectoryInfo directoryInfo = handleDirectoryRecommendationPostRequest(link.getContent(), userDirectories, "/directory");
        return directoryInfo.getName() + directoryInfo.getId().toString();
    }

    public String testEmbedding(String keyword) {
        return Arrays.toString(handleEmbeddingPostRequest(keyword));
    }
}



