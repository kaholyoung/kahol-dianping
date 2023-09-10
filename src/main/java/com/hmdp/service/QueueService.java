package com.hmdp.service;


import com.hmdp.Constants.QueueConstants;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class QueueService {

    @Autowired
    public RabbitTemplate rabbitTemplate;

    public int getMessageCount(String queueName){
        RabbitAdmin rabbitAdmin = new RabbitAdmin(rabbitTemplate.getConnectionFactory());
        int messageCount = rabbitAdmin.getQueueInfo(queueName).getMessageCount();
        return messageCount;
    }

    public boolean isEmpty(String queueName){
        return getMessageCount(queueName) == 0;
    }
}
