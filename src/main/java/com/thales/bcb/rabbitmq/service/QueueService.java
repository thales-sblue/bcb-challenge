package com.thales.bcb.rabbitmq.service;

import java.util.Map;

public interface QueueService {

    Map<String, Object> getQueueStatus();
}
