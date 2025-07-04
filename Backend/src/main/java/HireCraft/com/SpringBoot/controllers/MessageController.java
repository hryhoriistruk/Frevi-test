package HireCraft.com.SpringBoot.controllers;

import HireCraft.com.SpringBoot.dtos.requests.MessageRequest;
import HireCraft.com.SpringBoot.dtos.response.MessageResponse;
import HireCraft.com.SpringBoot.services.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/messages")
//@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    public MessageController(MessageService messageService, SimpMessagingTemplate messagingTemplate) {
        this.messageService = messageService;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Send a new message (encrypted) linked to a booking.
     */
    @PostMapping("/send")
    @PreAuthorize("hasAuthority('SEND_MESSAGE')")
    public ResponseEntity<MessageResponse> sendMessage(@RequestBody MessageRequest request,
                                                       @AuthenticationPrincipal UserDetails userDetails) {
        MessageResponse response = messageService.sendMessage(request, userDetails);
        messagingTemplate.convertAndSend("/topic/messages/" + request.getBookingId(), response);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all messages for a specific booking.
     */
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<List<MessageResponse>> getConversation(@PathVariable Long bookingId) {
        List<MessageResponse> conversation = messageService.getConversation(bookingId);
        return ResponseEntity.ok(conversation);
    }
}

