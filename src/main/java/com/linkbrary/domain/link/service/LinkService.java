package com.linkbrary.domain.link.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.linkbrary.common.response.ApiResponse;
import com.linkbrary.domain.link.dto.*;
import com.linkbrary.domain.link.entity.Link;
import com.linkbrary.domain.link.repository.LinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import static com.linkbrary.common.util.CallExternalApi.*;


@Service
@RequiredArgsConstructor
public class LinkService {

    private final LinkRepository linkRepository;

    public ApiResponse createLink(CreateLinkRequestDTO createLinkRequestDTO) {
        String jsonString = callExternalGetApi(createLinkRequestDTO.getUrl(), 1);
        Link newLink = mapJsonToLink(jsonString, createLinkRequestDTO.getUrl());
        linkRepository.save(newLink);
        return ApiResponse.onSuccess("hello");
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
}



