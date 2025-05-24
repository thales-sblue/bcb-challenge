package com.thales.bcb.modules.conversation.controller;

import com.thales.bcb.modules.conversation.dto.ConversationResponseDTO;
import com.thales.bcb.modules.conversation.service.ConversationService;
import com.thales.bcb.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/conversation")
@RequiredArgsConstructor
public class ConversationController {

    private final ConversationService conversationService;

    @GetMapping()
    public List<ConversationResponseDTO> listConversations(){
        UUID clientId = SecurityUtil.getClientIdFromToken();
        return conversationService.listAllByClient(clientId);
    }

    @GetMapping("/{id}")
    public ConversationResponseDTO getConversation(@PathVariable UUID id){
        return conversationService.findById(id);
    }
}
