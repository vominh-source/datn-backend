package com.example.datn.controller;

import com.example.datn.dto.request.SendMessageRequest;
import com.example.datn.dto.response.MessageResponse;
import com.example.datn.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Tag(name = "Message Management")
@Slf4j
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<MessageResponse> sendMessage(@RequestBody SendMessageRequest request) {
        log.info("=== SEND MESSAGE REQUEST ===");
        log.info("Request: {}", request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(messageService.sendMessage(request));
    }

    @GetMapping("/conversation/{conversationId}")
    public ResponseEntity<List<MessageResponse>> getConversationMessages(
            @PathVariable Long conversationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(
                messageService.getConversationMessages(conversationId, page, size));
    }

    @PutMapping("/{messageId}/read")
    public ResponseEntity<Void> markMessageAsRead(@PathVariable Long messageId) {
        messageService.markMessageAsRead(messageId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/conversation/{conversationId}/read")
    public ResponseEntity<Void> markConversationMessagesAsRead(
            @PathVariable Long conversationId,
            @RequestParam Long userId) {
        messageService.markConversationMessagesAsRead(conversationId, userId);
        return ResponseEntity.ok().build();
    }

    // SSE Endpoint
    @GetMapping(value = "/subscribe/{userId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable Long userId) {
        log.info("=== CONTROLLER: SSE Subscribe request for user {} ===", userId);
        SseEmitter emitter = messageService.subscribe(userId);
        log.info("=== CONTROLLER: Returning emitter for user {} ===", userId);
        return emitter;
    }
}
