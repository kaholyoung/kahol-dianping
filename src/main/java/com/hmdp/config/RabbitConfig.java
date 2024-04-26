package com.hmdp.config;


import com.hmdp.Constants.QueueConstants;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){


        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);

        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean b, String s) {
                if (b){
                    System.out.println("消息发送成功");
                }else {
                    Message returnedMessage = correlationData.getReturnedMessage();
                    String s1 = new String(returnedMessage.getBody());
                    rabbitTemplate.convertAndSend(QueueConstants.DLX_QUEUE,s1);
                }
            }
        });


        rabbitTemplate.setMandatory(true);

        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int i, String s, String s1, String s2) {
                System.out.println("Returned Message: " + message);
                System.out.println("Reply Code: " + i);
                System.out.println("Reply Text: " + s);
                System.out.println("Exchange: " + s1);
                System.out.println("Routing Key: " + s2);
                String s3 = new String(message.getBody());
                rabbitTemplate.convertAndSend(QueueConstants.DLX_QUEUE,s3);
            }
        });

        return rabbitTemplate;
    }
}
