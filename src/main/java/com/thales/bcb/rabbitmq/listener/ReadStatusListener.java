package com.thales.bcb.rabbitmq.listener;

import com.thales.bcb.modules.conversation.service.ConversationService;
import com.thales.bcb.modules.message.dto.ReadStatusPayload;
import com.thales.bcb.modules.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.thales.bcb.rabbitmq.config.RabbitMQConfig.READ_QUEUE;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReadStatusListener {

    private final ConversationService conversationService;
    private final MessageService messageService;

    @RabbitListener(queues =  READ_QUEUE)
    public void listenReadQueue(ReadStatusPayload readStatusPayload){
        log.info("[LISTENER][READ] Processando leitura da conversa {} pelo usuário {}", readStatusPayload.getConversationId(), readStatusPayload.getReaderId());

        List<UUID> failedMessages = messageService.markMessagesAsRead(readStatusPayload);

        int totalMessages = readStatusPayload.getMessageIds().size();
        int failedCount = failedMessages.size();
        int successCount = totalMessages - failedCount;

        conversationService.updateUnreadCount(readStatusPayload.getConversationId(), successCount);


        log.info("[LISTENER][READ] Processo de leitura finalizado.");
    }
}