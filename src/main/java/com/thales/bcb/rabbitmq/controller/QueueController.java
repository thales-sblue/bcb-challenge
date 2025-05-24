package com.thales.bcb.rabbitmq.controller;

import com.thales.bcb.rabbitmq.service.QueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/queue")
@RequiredArgsConstructor
public class QueueController {

    private final QueueService queueService;

    @GetMapping("/status")
    public Map<String, Object> getQueueStatus(){
        return  queueService.getQueueStatus();
    }
}
