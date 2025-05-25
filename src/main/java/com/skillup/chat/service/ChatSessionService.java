package com.skillup.chat.service;

import com.skillup.chat.dto.ChatSessionRequest;
import com.skillup.chat.model.ChatSession;
import com.skillup.chat.repository.ChatSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ChatSessionService {

    private final ChatSessionRepository chatSessionRepository;

    @Autowired
    public ChatSessionService(ChatSessionRepository chatSessionRepository) {
        this.chatSessionRepository = chatSessionRepository;
    }

    @Transactional
    public ChatSession createChatSession(ChatSessionRequest request, String userId) {
        // Créer la session de chat
        ChatSession chatSession = ChatSession.builder()
                .title(request.getTitle())
                .userId(userId)
                .messages(new ArrayList<>()) // Initialiser explicitement la liste des messages
                .build();

        // Sauvegarder la session pour obtenir un ID
        return chatSessionRepository.save(chatSession);
    }
    
    /**
     * Recharge une session de chat depuis la base de données
     */
    @Transactional(readOnly = true)
    public ChatSession refreshChatSession(String sessionId) {
        return chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new NoSuchElementException("Session de chat non trouvée avec l'ID: " + sessionId));
    }

    @Transactional(readOnly = true)
    public ChatSession getChatSessionById(String id, String userId) {
        ChatSession chatSession = chatSessionRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Session de chat non trouvée avec l'ID: " + id));

        if (!chatSession.getUserId().equals(userId)) {
            throw new SecurityException("Vous n'êtes pas autorisé à accéder à cette session de chat");
        }

        return chatSession;
    }

    @Transactional(readOnly = true)
    public List<ChatSession> getUserChatSessions(String userId) {
        return chatSessionRepository.findByUserIdOrderByUpdatedAtDesc(userId);
    }

    @Transactional
    public void deleteChatSession(String id, String userId) {
        ChatSession chatSession = getChatSessionById(id, userId);
        chatSessionRepository.delete(chatSession);
    }
}
