package com.skillup.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageRequest {
    private String sessionId;
    private String content;
    private String type; // "text" | "note" | "note-list"
    private MessageMetadata metadata;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MessageMetadata {
        private String noteId;
        private String noteTitle;
        private String noteContent;
        private List<NoteInfo> notes;
        private String action; // "summarize" | "review" | "list"
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NoteInfo {
        private String id;
        private String title;
        private String content;
    }
}
