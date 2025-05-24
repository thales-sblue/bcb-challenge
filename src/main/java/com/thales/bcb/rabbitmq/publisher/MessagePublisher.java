package com.thales.bcb.rabbitmq.publisher;

import com.thales.bcb.modules.message.dto.MessageDTO;
import com.thales.bcb.modules.message.enums.Priority;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

import static com.thales.bcb.rabbitmq.config.RabbitMQConfig.EXCHANGE;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessagePublisher {

    private final RabbitTemplate rabbitTemplate;
    private final AtomicInteger urgentCount = new AtomicInteger(0);

    public void sendMessage(MessageDTO message){
        String routingKey = getRoutingKey(message);


        rabbitTemplate.convertAndSend(
                EXCHANGE,
                routingKey,
                message
        );

        log.info("[PUBLISHER] Mensagem enviada para a fila -> {}", routingKey);
    }

    private String getRoutingKey(MessageDTO message) {
        boolean isUrgent = Priority.URGENT.equals(message.getPriority());

        if(!isUrgent){
            urgentCount.set(0);
            return "message.normal";
        }

        int count = urgentCount.incrementAndGet();
        log.info("Contador de mensagens urgentes: {}", count);

        if(count >= 3){
            urgentCount.set(0);
            log.info("Balanceamento - Forçando envio dee mensagem normal anti-starvation");
            return "message.normal";
        }

        return "message.urgent";
    }
}
