package com.thales.bcb.rabbitmq.service;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.QueueInformation;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.thales.bcb.rabbitmq.config.RabbitMQConfig.NORMAL_QUEUE;
import static com.thales.bcb.rabbitmq.config.RabbitMQConfig.URGENT_QUEUE;

@Service
@RequiredArgsConstructor
public class QueueService {

    private final RabbitAdmin rabbitAdmin;
    private final RabbitTemplate rabbitTemplate;

    public Map<String, Object> getQueueStatus(){
        Map<String, Object> status = new HashMap<>();

        QueueInformation normalQueue = rabbitAdmin.getQueueInfo(NORMAL_QUEUE);
        QueueInformation urgentQueue = rabbitAdmin.getQueueInfo(URGENT_QUEUE);

        status.put("normal", buildQueueInfo(normalQueue));
        status.put("urgent", buildQueueInfo(urgentQueue));

        return status;
    }

    private Map<String, Object> buildQueueInfo(QueueInformation queueInfo){
        if(queueInfo == null){
            return Map.of("messageCount", 0,
                    "consumerCount", 0
            );
        }
        return Map.of("messageCount", queueInfo.getMessageCount(),
                "consumerCount", queueInfo.getConsumerCount()
        );
    }
}
