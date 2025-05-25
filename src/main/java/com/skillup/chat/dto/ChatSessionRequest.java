package com.skillup.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatSessionRequest {
    private String title;
    private String initialMessage; // Optional initial message to start the session
}
