package com.thales.bcb.rabbitmq.listener;

import com.thales.bcb.modules.message.dto.MessageDTO;
import com.thales.bcb.modules.message.enums.Status;
import com.thales.bcb.modules.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import static com.thales.bcb.rabbitmq.config.RabbitMQConfig.NORMAL_QUEUE;
import static com.thales.bcb.rabbitmq.config.RabbitMQConfig.URGENT_QUEUE;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageListener {

    private final MessageService messageService;

    @RabbitListener(queues = URGENT_QUEUE)
    public void listenUrgentQueue(MessageDTO message){
        log.info("[LISTENER][URGENT] Processando mensagem {}", message);

        processMessage(message, "URGENT");
    }

    @RabbitListener(queues = NORMAL_QUEUE)
    public void listenNormalQueue(MessageDTO message){
        log.info("[LISTENER][NORMAL] Processando mensagem {}", message);

        processMessage(message, "NORMAL");
    }

    private void processMessage(MessageDTO message, String priority){
        try{
            log.info("[LISTENER]Iniciado entrega da mensagem [{}] com prioridade {}", message.getId(), message.getPriority());

            Thread.sleep(priority.equalsIgnoreCase("URGENT")? 1000 : 3000);

            messageService.updateStatus(message.getId(), Status.DELIVERED);

            log.info("[LISTENER]Mensagem [{}] entregue com sucesso", message.getId());

        }catch (InterruptedException e){
            messageService.updateStatus(message.getId(), Status.FAILED);
            Thread.currentThread().interrupt();
            log.error("[LISTENER]Erro ao processar mensagem [{}]: {}", message.getId(), e.getMessage());
        }
    }
}
