package com.thales.bcb.rabbitmq.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {

    public static final String NORMAL_QUEUE = "queue.normal";
    public static final String URGENT_QUEUE = "queue.urgent";
    public static final String EXCHANGE = "exchange.message";
    public static final String READ_QUEUE = "queue.read";

    @Bean
    Queue readQueue(){
        return new Queue(READ_QUEUE, true);
    }

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
    Binding bindingRead(Queue readQueue, DirectExchange exchange){
        return BindingBuilder.bind(readQueue).to(exchange).with("message.read");
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

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory cf, Jackson2JsonMessageConverter converter) {
        RabbitTemplate tpl = new RabbitTemplate(cf);
        tpl.setMessageConverter(converter);
        return tpl;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory cf,
            Jackson2JsonMessageConverter converter) {
        SimpleRabbitListenerContainerFactory fac = new SimpleRabbitListenerContainerFactory();
        fac.setConnectionFactory(cf);
        fac.setMessageConverter(converter);
        return fac;
    }
}
