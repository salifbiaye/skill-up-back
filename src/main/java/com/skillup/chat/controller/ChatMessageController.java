package com.skillup.chat.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillup.chat.dto.AIResponseRequest;
import com.skillup.chat.dto.ChatMessageRequest;
import com.skillup.chat.dto.ChatMessageResponse;
import com.skillup.chat.model.ChatMessage;
import com.skillup.chat.service.AIService;
import com.skillup.chat.service.ChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/chat-sessions/{sessionId}")
public class ChatMessageController {

    private final ChatMessageService chatMessageService;
    private final AIService aiService;
    private final ObjectMapper objectMapper;

    @Autowired
    public ChatMessageController(ChatMessageService chatMessageService, AIService aiService, ObjectMapper objectMapper) {
        this.chatMessageService = chatMessageService;
        this.aiService = aiService;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/messages")
    public ResponseEntity<List<ChatMessageResponse>> getSessionMessages(
            @PathVariable String sessionId,
            Authentication authentication) {
        String userId = authentication.getName();
        List<ChatMessage> messages = chatMessageService.getSessionMessages(sessionId, userId);
        List<ChatMessageResponse> responseMessages = messages.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseMessages);
    }

    @PostMapping("/messages")
    public ResponseEntity<ChatMessageResponse> sendMessage(
            @PathVariable String sessionId,
            @RequestBody ChatMessageRequest request,
            Authentication authentication) {
        try {
            // Utiliser l'ID de session de l'URL par priorité, sinon celui du corps de la requête
            String effectiveSessionId = sessionId;
            if (request.getSessionId() != null) {
                System.out.println("SessionID dans le corps de la requête: " + request.getSessionId());
                System.out.println("Utilisation de l'ID de session de l'URL: " + sessionId);
            }
            
            System.out.println("Requête reçue - SessionID effectif: " + effectiveSessionId);
            System.out.println("Contenu du message: " + request.getContent());
            System.out.println("Type du message: " + request.getType());
            System.out.println("Métadonnées: " + (request.getMetadata() != null ? objectMapper.writeValueAsString(request.getMetadata()) : "null"));
            
            String userId = authentication.getName();
            System.out.println("UserID: " + userId);
            
            ChatMessage message = chatMessageService.createUserMessage(effectiveSessionId, request, userId);
            return ResponseEntity.ok(convertToResponse(message));
        } catch (Exception e) {
            System.err.println("Erreur lors du traitement du message: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur lors du traitement du message", e);
        }
    }

    @PostMapping("/ai-response")
    public ResponseEntity<ChatMessageResponse> getAIResponse(
            @PathVariable String sessionId,
            @RequestBody AIResponseRequest request,
            Authentication authentication) {
        try {
            System.out.println("Requête AI reçue - SessionID: " + sessionId);
            
            // Vérifier si messageId est fourni
            if (request.getMessageId() == null || request.getMessageId().isEmpty()) {
                System.err.println("Erreur: messageId est requis pour générer une réponse IA");
                throw new IllegalArgumentException("messageId est requis pour générer une réponse IA");
            }
            
            System.out.println("MessageID: " + request.getMessageId());
            
            String userId = authentication.getName();
            System.out.println("UserID: " + userId);
            
            ChatMessage aiResponse = aiService.generateAIResponse(sessionId, request, userId);
            return ResponseEntity.ok(convertToResponse(aiResponse));
        } catch (Exception e) {
            System.err.println("Erreur lors de la génération de la réponse IA: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la génération de la réponse IA", e);
        }
    }
    
    /**
     * Convertit un ChatMessage en ChatMessageResponse avec les métadonnées désérialisées
     */
    private ChatMessageResponse convertToResponse(ChatMessage message) {
        ChatMessageResponse response = new ChatMessageResponse();
        response.setId(message.getId());
        response.setSessionId(message.getChatSession().getId());
        response.setContent(message.getContent());
        response.setRole(message.getRole().name());
        response.setTimestamp(message.getTimestamp());
        response.setType(message.getMessageType());
        
        // Désérialiser les métadonnées si elles existent
        if (message.getMetadata() != null && !message.getMetadata().isEmpty()) {
            try {
                response.setMetadata(objectMapper.readValue(message.getMetadata(), Object.class));
            } catch (JsonProcessingException e) {
                // En cas d'erreur, laisser les métadonnées à null
                System.out.println("Erreur lors de la désérialisation des métadonnées: " + e.getMessage());
            }
        }
        
        return response;
    }
}
