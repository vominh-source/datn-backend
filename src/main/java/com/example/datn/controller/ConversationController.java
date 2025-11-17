package com.example.datn.controller;

import com.example.datn.dto.request.CreateConversationRequest;
import com.example.datn.dto.response.ConversationResponse;
import com.example.datn.service.ConversationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Conversation Management", description = "APIs for managing conversations")
public class ConversationController {

    private final ConversationService conversationService;

    @Operation(
            summary = "Create a new conversation",
            description = "Creates a new conversation (direct or group chat)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Conversation created successfully",
                    content = @Content(schema = @Schema(implementation = ConversationResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<ConversationResponse> createConversation(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Conversation information",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CreateConversationRequest.class))
            )
            @RequestBody CreateConversationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(conversationService.createConversation(request));
    }

    @Operation(
            summary = "Get conversation by ID",
            description = "Retrieves a conversation with its participants and metadata"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Conversation found",
                    content = @Content(schema = @Schema(implementation = ConversationResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Conversation not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ConversationResponse> getConversationById(
            @Parameter(description = "Conversation ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "Current user ID", required = true)
            @RequestParam Long currentUserId) {
        return ResponseEntity.ok(conversationService.getConversationById(id, currentUserId));
    }

    @Operation(
            summary = "Get user's conversations",
            description = "Retrieves all conversations for a specific user"
    )
    @ApiResponse(
            responseCode = "200",
            description = "List of conversations retrieved successfully"
    )
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ConversationResponse>> getUserConversations(
            @Parameter(description = "User ID", required = true)
            @PathVariable Long userId) {
        return ResponseEntity.ok(conversationService.getUserConversations(userId));
    }

    @Operation(
            summary = "Find or create direct conversation",
            description = "Finds an existing direct conversation between two users or creates a new one"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Conversation found or created successfully",
            content = @Content(schema = @Schema(implementation = ConversationResponse.class))
    )
    @GetMapping("/direct")
    public ResponseEntity<ConversationResponse> findOrCreateDirectConversation(
            @Parameter(description = "First user ID", required = true)
            @RequestParam Long userId1,
            @Parameter(description = "Second user ID", required = true)
            @RequestParam Long userId2) {
        return ResponseEntity.ok(
                conversationService.findOrCreateDirectConversation(userId1, userId2));
    }
}