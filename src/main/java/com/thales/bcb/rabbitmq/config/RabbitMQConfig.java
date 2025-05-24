package com.thales.bcb.rabbitmq.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {

    public static final String NORMAL_QUEUE = "queue.normal";
    public static final String URGENT_QUEUE = "queue.urgent";
    public static final String EXCHANGE = "exchange.message";

    @Bean
    Queue normalQueue(){
        return new Queue(NORMAL_QUEUE, true);
    }

    @Bean
    Queue urgentQueue(){
        return new Queue(URGENT_QUEUE, true);
    }

    @Bean
    DirectExchange exchange(){
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    Binding bindingNormal(Queue normalQueue, DirectExchange exchange){
        return BindingBuilder.bind(normalQueue).to(exchange).with("message.normal");
    }

    @Bean
    Binding bindingUrgent(Queue urgentQueue, DirectExchange exchange){
        return BindingBuilder.bind(urgentQueue).to(exchange).with("message.urgent");
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory){
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }

    @Bean
    public Object initializeRabbit(RabbitAdmin rabbitAdmin){
        return new Object();
    }
}
