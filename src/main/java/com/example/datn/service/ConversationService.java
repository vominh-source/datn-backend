package com.example.datn.service;

import com.example.datn.dto.request.CreateConversationRequest;
import com.example.datn.dto.response.ConversationResponse;
import com.example.datn.dto.response.UserResponse;
import com.example.datn.entity.Conversation;
import com.example.datn.entity.User;
import com.example.datn.repository.ConversationRepository;
import com.example.datn.repository.MessageRepository;
import com.example.datn.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    @Transactional
    public ConversationResponse createConversation(CreateConversationRequest request) {
        List<User> participants = userRepository.findAllById(request.getParticipantIds());

        if (participants.size() != request.getParticipantIds().size()) {
            throw new RuntimeException("Some participants not found");
        }

        Conversation conversation = Conversation.builder()
                .name(request.getName())
                .type(request.getType())
                .participants(participants)
                .build();

        conversation = conversationRepository.save(conversation);
        return mapToConversationResponse(conversation, null);
    }

    @Transactional(readOnly = true)
    public ConversationResponse getConversationById(Long id, Long currentUserId) {
        Conversation conversation = conversationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conversation not found with id: " + id));

        return mapToConversationResponse(conversation, currentUserId);
    }

    @Transactional(readOnly = true)
    public List<ConversationResponse> getUserConversations(Long userId) {
        List<Conversation> conversations = conversationRepository.findByUserId(userId);

        return conversations.stream()
                .map(conv -> mapToConversationResponse(conv, userId))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ConversationResponse findOrCreateDirectConversation(Long userId1, Long userId2) {
        return conversationRepository.findDirectConversationBetweenUsers(userId1, userId2)
                .map(conv -> mapToConversationResponse(conv, userId1))
                .orElseGet(() -> {
                    CreateConversationRequest request = new CreateConversationRequest();
                    request.setType(Conversation.ConversationType.DIRECT);
                    request.setParticipantIds(List.of(userId1, userId2));
                    return createConversation(request);
                });
    }

    private ConversationResponse mapToConversationResponse(Conversation conversation, Long currentUserId) {
        List<UserResponse> participants = conversation.getParticipants().stream()
                .map(user -> UserResponse.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .fullName(user.getFullName())
                        .email(user.getEmail())
                        .avatarUrl(user.getAvatarUrl())
                        .online(user.getOnline())
                        .lastSeen(user.getLastSeen())
                        .build())
                .collect(Collectors.toList());

        Long unreadCount = currentUserId != null ?
                messageRepository.countUnreadMessages(conversation.getId(), currentUserId) : 0L;

        return ConversationResponse.builder()
                .id(conversation.getId())
                .name(conversation.getName())
                .type(conversation.getType())
                .participants(participants)
                .unreadCount(unreadCount)
                .createdAt(conversation.getCreatedAt())
                .updatedAt(conversation.getUpdatedAt())
                .build();
    }
}