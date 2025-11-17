package com.example.datn.service;

import com.example.datn.dto.request.SendMessageRequest;
import com.example.datn.dto.response.MessageResponse;
import com.example.datn.dto.response.UserResponse;
import com.example.datn.entity.Conversation;
import com.example.datn.entity.Message;
import com.example.datn.entity.User;
import com.example.datn.repository.ConversationRepository;
import com.example.datn.repository.MessageRepository;
import com.example.datn.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;

    // Store SSE emitters for each user
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    @Transactional
    public MessageResponse sendMessage(SendMessageRequest request) {
        log.info("Processing message from user {} to conversation {}",
                request.getSenderId(), request.getConversationId());

        Conversation conversation = conversationRepository.findById(request.getConversationId())
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        User sender = userRepository.findById(request.getSenderId())
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        Message message = Message.builder()
                .conversation(conversation)
                .sender(sender)
                .content(request.getContent())
                .type(request.getType())
                .isRead(false)
                .build();

        message = messageRepository.save(message);
        log.info("Message saved with ID: {}", message.getId());

        MessageResponse response = mapToMessageResponse(message);

        // Send message to all participants via SSE
        notifyParticipants(conversation, response);

        return response;
    }

    @Transactional(readOnly = true)
    public List<MessageResponse> getConversationMessages(Long conversationId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Message> messages = messageRepository.findByConversationId(conversationId, pageable);

        return messages.getContent().stream()
                .map(this::mapToMessageResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void markMessageAsRead(Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        message.setIsRead(true);
        messageRepository.save(message);
    }

    @Transactional
    public void markConversationMessagesAsRead(Long conversationId, Long userId) {
        List<Message> messages = messageRepository.findByConversationIdOrderByCreatedAtDesc(conversationId);

        messages.stream()
                .filter(msg -> !msg.getSender().getId().equals(userId) && !msg.getIsRead())
                .forEach(msg -> {
                    msg.setIsRead(true);
                    messageRepository.save(msg);
                });
    }

    // SSE Methods
    public SseEmitter subscribe(Long userId) {
        log.info("=== SSE SUBSCRIBE START for user {} ===", userId);

        // Remove old emitter if exists
        SseEmitter oldEmitter = emitters.remove(userId);
        if (oldEmitter != null) {
            log.info("Removed old emitter for user {}", userId);
            try {
                oldEmitter.complete();
            } catch (Exception e) {
                log.warn("Error completing old emitter: {}", e.getMessage());
            }
        }

        // Create new emitter with long timeout (30 minutes)
        SseEmitter emitter = new SseEmitter(30 * 60 * 1000L); // 30 minutes

        emitter.onCompletion(() -> {
            log.info("SSE connection COMPLETED for user {}", userId);
            emitters.remove(userId);
        });

        emitter.onTimeout(() -> {
            log.warn("SSE connection TIMEOUT for user {}", userId);
            emitters.remove(userId);
            emitter.complete();
        });

        emitter.onError(e -> {
            log.error("SSE connection ERROR for user {}: {}", userId, e.getMessage());
            emitters.remove(userId);
        });

        // Store emitter BEFORE sending initial event
        emitters.put(userId, emitter);
        log.info("Emitter stored for user {}. Total connections: {}", userId, emitters.size());

        // Send initial "connected" event
        try {
            emitter.send(SseEmitter.event()
                    .name("connected")
                    .data("User " + userId + " connected to SSE"));
            log.info("=== SENT 'connected' event to user {} ===", userId);
        } catch (IOException e) {
            log.error("Failed to send initial event to user {}: {}", userId, e.getMessage());
            emitters.remove(userId);
            throw new RuntimeException("Failed to establish SSE connection", e);
        }

        log.info("=== SSE SUBSCRIBE SUCCESS for user {} ===", userId);
        return emitter;
    }

    private void notifyParticipants(Conversation conversation, MessageResponse message) {
        log.info("=== NOTIFY PARTICIPANTS START ===");
        log.info("Conversation ID: {}, Total participants: {}",
                conversation.getId(), conversation.getParticipants().size());
        log.info("Active emitters: {}", emitters.keySet());

        conversation.getParticipants().forEach(participant -> {
            Long participantId = participant.getId();
            SseEmitter emitter = emitters.get(participantId);

            log.info("Participant {}: emitter={}", participantId, emitter != null ? "EXISTS" : "NULL");

            if (emitter != null) {
                try {
                    emitter.send(SseEmitter.event()
                            .name("new-message")
                            .data(message));
                    log.info("✅ Message sent successfully to user {}", participantId);
                } catch (IOException e) {
                    log.error("❌ Failed to send message to user {}: {}", participantId, e.getMessage());
                    emitters.remove(participantId);
                }
            } else {
                log.warn("⚠️ No active SSE connection for user {}", participantId);
            }
        });

        log.info("=== NOTIFY PARTICIPANTS END ===");
    }

    private MessageResponse mapToMessageResponse(Message message) {
        return MessageResponse.builder()
                .id(message.getId())
                .conversationId(message.getConversation().getId())
                .sender(UserResponse.builder()
                        .id(message.getSender().getId())
                        .username(message.getSender().getUsername())
                        .fullName(message.getSender().getFullName())
                        .avatarUrl(message.getSender().getAvatarUrl())
                        .online(message.getSender().getOnline())
                        .build())
                .content(message.getContent())
                .type(message.getType())
                .isRead(message.getIsRead())
                .createdAt(message.getCreatedAt())
                .build();
    }
}
