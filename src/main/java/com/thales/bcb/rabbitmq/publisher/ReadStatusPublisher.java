package com.thales.bcb.rabbitmq.publisher;

import com.thales.bcb.modules.message.dto.ReadStatusPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.thales.bcb.rabbitmq.config.RabbitMQConfig.EXCHANGE;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReadStatusPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void sendReadStatus(ReadStatusPayload readStatusPayload){
        rabbitTemplate.convertAndSend(
                EXCHANGE,
                "message.read",
                readStatusPayload
        );

        log.info("[PUBLISHER][READ] Atualização de status enviada para fila -> message.read | Payload: {}", readStatusPayload);
    }

}
