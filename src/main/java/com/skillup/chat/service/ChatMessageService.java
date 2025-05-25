package com.skillup.chat.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillup.chat.dto.ChatMessageRequest;
import com.skillup.chat.model.ChatMessage;
import com.skillup.chat.model.ChatSession;
import com.skillup.chat.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatSessionService chatSessionService;
    private final ObjectMapper objectMapper;

    @Autowired
    public ChatMessageService(ChatMessageRepository chatMessageRepository, ChatSessionService chatSessionService, ObjectMapper objectMapper) {
        this.chatMessageRepository = chatMessageRepository;
        this.chatSessionService = chatSessionService;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public ChatMessage createUserMessage(String sessionId, ChatMessageRequest request, String userId) {
        ChatSession chatSession = chatSessionService.getChatSessionById(sessionId, userId);
        
        ChatMessage.ChatMessageBuilder builder = ChatMessage.builder()
                .chatSession(chatSession)
                .content(request.getContent())
                .role(ChatMessage.MessageRole.user);
        
        // Ajouter le type de message s'il est spécifié
        if (request.getType() != null) {
            builder.messageType(request.getType());
        }
        
        // Ajouter les métadonnées s'il y en a
        if (request.getMetadata() != null) {
            try {
                String metadataJson = objectMapper.writeValueAsString(request.getMetadata());
                builder.metadata(metadataJson);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Erreur lors de la conversion des métadonnées en JSON", e);
            }
        }
        
        ChatMessage chatMessage = builder.build();
        return chatMessageRepository.save(chatMessage);
    }

    @Transactional(readOnly = true)
    public ChatMessage getMessageById(String messageId) {
        return chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message non trouvé avec l'ID: " + messageId));
    }

    @Transactional(readOnly = true)
    public List<ChatMessage> getSessionMessages(String sessionId, String userId) {
        // Vérifie que l'utilisateur a accès à cette session
        chatSessionService.getChatSessionById(sessionId, userId);
        
        return chatMessageRepository.findByChatSessionIdOrderByTimestampAsc(sessionId);
    }

    @Transactional
    public ChatMessage saveMessage(ChatMessage message) {
        return chatMessageRepository.save(message);
    }
}
