package com.example.datn.dto.request;

import com.example.datn.entity.Message.MessageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageRequest {
    private Long conversationId;
    private Long senderId;
    private String content;
    private MessageType type = MessageType.TEXT;
}