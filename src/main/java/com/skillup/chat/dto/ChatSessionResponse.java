package com.skillup.chat.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatSessionResponse {
    private String id;
    private String title;
    private Instant createdAt;
    private Instant updatedAt;
    private List<ChatMessageResponse> messages = new ArrayList<>();
}
