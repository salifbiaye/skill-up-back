package com.skillup.chat.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillup.chat.dto.ChatMessageRequest;
import com.skillup.chat.dto.ChatMessageResponse;
import com.skillup.chat.dto.ChatSessionRequest;
import com.skillup.chat.dto.ChatSessionResponse;
import com.skillup.chat.model.ChatMessage;
import com.skillup.chat.model.ChatSession;
import com.skillup.chat.service.ChatMessageService;
import com.skillup.chat.service.ChatSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/chat-sessions")
public class ChatSessionController {

    private final ChatSessionService chatSessionService;
    private final ObjectMapper objectMapper;

    private final ChatMessageService chatMessageService;
    
    @Autowired
    public ChatSessionController(ChatSessionService chatSessionService, 
                                 ChatMessageService chatMessageService,
                                 ObjectMapper objectMapper) {
        this.chatSessionService = chatSessionService;
        this.chatMessageService = chatMessageService;
        this.objectMapper = objectMapper;
    }

    @PostMapping
    public ResponseEntity<ChatSessionResponse> createChatSession(
            @RequestBody ChatSessionRequest request,
            Authentication authentication) {
        System.out.println("Début de la création de session");
        System.out.println("Requête reçue: " + request);
        System.out.println("Authentification: " + (authentication != null ? "OK" : "NULL"));
        try {
            // Vérifier l'authentification
            if (authentication == null) {
                System.err.println("Erreur: Utilisateur non authentifié");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            String userId = authentication.getName();
            System.out.println("Création d'une session de chat pour l'utilisateur: " + userId);
            
            // Vérifier le titre
            if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
                System.err.println("Erreur: Titre de session manquant");
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Le titre de la session est requis");
                return ResponseEntity.badRequest().body(null);
            }
            
            System.out.println("Titre de la session: " + request.getTitle());
            
            // Créer la session de chat
            ChatSession chatSession = chatSessionService.createChatSession(request, userId);
            System.out.println("Session créée avec l'ID: " + chatSession.getId());
            
            // Si un message initial est fourni, l'ajouter à la session
            if (request.getInitialMessage() != null && !request.getInitialMessage().isEmpty()) {
                System.out.println("Ajout du message initial: " + request.getInitialMessage());
                ChatMessageRequest messageRequest = new ChatMessageRequest();
                messageRequest.setContent(request.getInitialMessage());
                
                // Créer le message utilisateur initial
                chatMessageService.createUserMessage(chatSession.getId(), messageRequest, userId);
                
                // Recharger la session pour inclure le message initial
                chatSession = chatSessionService.refreshChatSession(chatSession.getId());
            }
            
            ChatSessionResponse response = convertToResponse(chatSession);
            System.out.println("Réponse préparée avec l'ID: " + response.getId());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Erreur lors de la création de la session: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la création de la session", e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChatSessionResponse> getChatSession(
            @PathVariable String id,
            Authentication authentication) {
        String userId = authentication.getName();
        ChatSession chatSession = chatSessionService.getChatSessionById(id, userId);
        ChatSessionResponse response = convertToResponse(chatSession);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ChatSessionResponse>> getUserChatSessions(Authentication authentication) {
        String userId = authentication.getName();
        List<ChatSession> chatSessions = chatSessionService.getUserChatSessions(userId);
        List<ChatSessionResponse> responses = chatSessions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteChatSession(
            @PathVariable String id,
            Authentication authentication) {
        String userId = authentication.getName();
        chatSessionService.deleteChatSession(id, userId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Session de chat supprimée avec succès");
        return ResponseEntity.ok(response);
    }
    
    /**
     * Convertit un ChatSession en ChatSessionResponse avec les messages et leurs métadonnées
     */
    private ChatSessionResponse convertToResponse(ChatSession session) {
        ChatSessionResponse response = new ChatSessionResponse();
        response.setId(session.getId());
        response.setTitle(session.getTitle());
        response.setCreatedAt(session.getCreatedAt());
        response.setUpdatedAt(session.getUpdatedAt());
        
        // Convertir les messages avec leurs métadonnées
        List<ChatMessageResponse> messageResponses = new ArrayList<>();
        
        // Vérifier si la liste des messages est null
        if (session.getMessages() != null) {
            for (ChatMessage message : session.getMessages()) {
            ChatMessageResponse messageResponse = new ChatMessageResponse();
            messageResponse.setId(message.getId());
            messageResponse.setSessionId(session.getId());
            messageResponse.setContent(message.getContent());
            messageResponse.setRole(message.getRole().name());
            messageResponse.setTimestamp(message.getTimestamp());
            messageResponse.setType(message.getMessageType());
            
            // Désérialiser les métadonnées si elles existent
            if (message.getMetadata() != null && !message.getMetadata().isEmpty()) {
                try {
                    messageResponse.setMetadata(objectMapper.readValue(message.getMetadata(), Object.class));
                } catch (JsonProcessingException e) {
                    // En cas d'erreur, laisser les métadonnées à null
                    System.out.println("Erreur lors de la désérialisation des métadonnées: " + e.getMessage());
                }
            }
            
            messageResponses.add(messageResponse);
        }
        }
        
        response.setMessages(messageResponses);
        return response;
    }
}
