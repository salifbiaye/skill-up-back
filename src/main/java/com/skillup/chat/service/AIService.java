package com.skillup.chat.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillup.chat.config.OpenRouterConfig;
import com.skillup.chat.dto.AIResponseRequest;
import com.skillup.chat.dto.ChatMessageRequest;
import com.skillup.chat.model.ChatMessage;
import com.skillup.chat.model.ChatSession;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AIService {

    private final RestTemplate restTemplate;
    private final OpenRouterConfig openRouterConfig;
    private final ChatMessageService chatMessageService;
    private final ChatSessionService chatSessionService;
    private final ObjectMapper objectMapper;

    @Autowired
    public AIService(RestTemplate restTemplate, OpenRouterConfig openRouterConfig,
                     ChatMessageService chatMessageService, ChatSessionService chatSessionService,
                     ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.openRouterConfig = openRouterConfig;
        this.chatMessageService = chatMessageService;
        this.chatSessionService = chatSessionService;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public ChatMessage generateAIResponse(String sessionId, AIResponseRequest request, String userId) {
        // Vérifier que l'utilisateur a accès à cette session
        ChatSession chatSession = chatSessionService.getChatSessionById(sessionId, userId);
        
        // Récupérer le message de l'utilisateur
        ChatMessage userMessage = chatMessageService.getMessageById(request.getMessageId());
        
        // Vérifier que le message appartient à la session
        if (!userMessage.getChatSession().getId().equals(sessionId)) {
            throw new IllegalArgumentException("Le message n'appartient pas à cette session de chat");
        }
        
        // Récupérer tous les messages de la session pour le contexte
        List<ChatMessage> sessionMessages = chatMessageService.getSessionMessages(sessionId, userId);
        
        // Vérifier si le message contient des métadonnées spéciales
        String messageType = userMessage.getMessageType();
        String metadataJson = userMessage.getMetadata();
        ChatMessageRequest.MessageMetadata metadata = null;
        
        if (metadataJson != null && !metadataJson.isEmpty()) {
            try {
                metadata = objectMapper.readValue(metadataJson, ChatMessageRequest.MessageMetadata.class);
            } catch (JsonProcessingException e) {
                System.out.println("Erreur lors de la lecture des métadonnées: " + e.getMessage());
            }
        }
        
        // Préparer le contexte pour l'IA (résumé des messages précédents pour économiser des tokens)
        List<Message> context = prepareContext(sessionMessages, messageType, metadata);
        
        // Appeler l'API OpenRouter
        String aiResponse = callOpenRouterAPI(context);
        
        // Créer et sauvegarder le message de l'IA
        ChatMessage aiMessage = ChatMessage.builder()
                .chatSession(chatSession)
                .content(aiResponse)
                .role(ChatMessage.MessageRole.assistant)
                .build();
        System.out.println("AI Response: " + aiResponse);
        return chatMessageService.saveMessage(aiMessage);
    }
    
    private List<Message> prepareContext(List<ChatMessage> messages, String messageType, ChatMessageRequest.MessageMetadata metadata) {
        List<Message> context = new ArrayList<>();
        
        // Ajouter des instructions spécifiques en fonction du type de message et des métadonnées
        if (messageType != null && metadata != null) {
            StringBuilder systemPrompt = new StringBuilder();
            
            switch (messageType) {
                case "note":
                    if ("summarize".equals(metadata.getAction()) && metadata.getNoteContent() != null) {
                        systemPrompt.append("Tu es un assistant qui aide à résumer des notes. ");
                        systemPrompt.append("Voici le contenu de la note intitulée '").append(metadata.getNoteTitle())
                                   .append("' que tu dois résumer: \n\n").append(metadata.getNoteContent());
                        systemPrompt.append("\n\nRésume cette note de manière concise et claire.");
                        context.add(new Message("system", systemPrompt.toString()));
                        return context; // Retourner directement ce contexte spécifique
                    } else if ("review".equals(metadata.getAction()) && metadata.getNoteContent() != null) {
                        systemPrompt.append("Tu es un assistant qui aide à réviser et améliorer des notes. ");
                        systemPrompt.append("Voici le contenu de la note intitulée '").append(metadata.getNoteTitle())
                                   .append("' que tu dois réviser: \n\n").append(metadata.getNoteContent());
                        systemPrompt.append("\n\nDonne des suggestions pour améliorer cette note.");
                        context.add(new Message("system", systemPrompt.toString()));
                        return context; // Retourner directement ce contexte spécifique
                    }else if ("quiz".equals(metadata.getAction()) && metadata.getNoteContent() != null) {
                        systemPrompt.append("Tu es un assistant qui crée des quiz basés sur des notes. ");
                        systemPrompt.append("Voici le contenu de la note intitulée '").append(metadata.getNoteTitle())
                                   .append("' que tu dois utiliser pour créer un quiz: \n\n").append(metadata.getNoteContent());
                        systemPrompt.append("\n\nCrée un quiz avec des questions à choix multiples.");
                        context.add(new Message("system", systemPrompt.toString()));
                        return context; // Retourner directement ce contexte spécifique
                    }
                    break;
                    

            }
        }
        
        // Si la conversation est longue, résumer les anciens messages pour économiser des tokens
        if (messages.size() > 10) {
            // Ajouter un résumé des messages précédents
            StringBuilder summary = new StringBuilder("Résumé de la conversation précédente: ");
            for (int i = 0; i < messages.size() - 10; i++) {
                ChatMessage msg = messages.get(i);
                summary.append(msg.getRole().name()).append(": ")
                       .append(msg.getContent().length() > 50 ? 
                               msg.getContent().substring(0, 50) + "..." : 
                               msg.getContent())
                       .append(". ");
            }
            
            // Ajouter le résumé comme message du système si ce n'est pas déjà fait
            if (context.isEmpty()) {
                context.add(new Message("system", summary.toString()));
            }
            
            // Ajouter les 10 derniers messages complets
            for (int i = messages.size() - 10; i < messages.size(); i++) {
                ChatMessage msg = messages.get(i);
                context.add(new Message(msg.getRole().name(), msg.getContent()));
            }
        } else {
            // Si la conversation est courte, ajouter tous les messages
            for (ChatMessage msg : messages) {
                context.add(new Message(msg.getRole().name(), msg.getContent()));
            }
        }
        
        return context;
    }
    
    private String callOpenRouterAPI(List<Message> messages) {
        String url = openRouterConfig.getApiUrl() + "/chat/completions";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + openRouterConfig.getApiKey());
        
        OpenRouterRequest request = new OpenRouterRequest();
        request.setModel(openRouterConfig.getModel());
        request.setMessages(messages);
        
        HttpEntity<OpenRouterRequest> entity = new HttpEntity<>(request, headers);
        
        try {
            OpenRouterResponse response = restTemplate.postForObject(url, entity, OpenRouterResponse.class);
            if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
                return response.getChoices().get(0).getMessage().getContent();
            }
        } catch (Exception e) {
            // En cas d'erreur, retourner un message d'erreur
            return "Désolé, je n'ai pas pu générer une réponse. Erreur: " + e.getMessage();
        }
        
        return "Désolé, je n'ai pas pu générer une réponse.";
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class Message {
        private String role;
        private String content;
    }
    
    @Data
    private static class OpenRouterRequest {
        private String model;
        private List<Message> messages;
    }
    
    @Data
    private static class OpenRouterResponse {
        private List<Choice> choices;
    }
    
    @Data
    private static class Choice {
        private Message message;
        private int index;
        
        @JsonProperty("finish_reason")
        private String finishReason;
    }
}
