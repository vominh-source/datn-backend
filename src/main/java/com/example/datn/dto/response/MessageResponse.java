package com.example.datn.dto.response;

import com.example.datn.entity.Message.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageResponse {
    private Long id;
    private Long conversationId;
    private UserResponse sender;
    private String content;
    private MessageType type;
    private Boolean isRead;
    private LocalDateTime createdAt;
}