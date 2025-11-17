package com.example.datn.dto.request;

import com.example.datn.entity.Conversation.ConversationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateConversationRequest {
    private String name;
    private ConversationType type;
    private List<Long> participantIds;
}