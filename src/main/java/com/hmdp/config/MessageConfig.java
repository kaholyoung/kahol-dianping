package com.hmdp.config;

import com.hmdp.Constants.QueueConstants;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

@Configuration
public class MessageConfig {

    @Bean
    public DirectExchange orderExchange(){
        return new DirectExchange(QueueConstants.ORDER_EXCHANGE,true,false);
    }

    @Bean
    public Binding orderBinding(Queue orderQueue,DirectExchange orderExchange){
       return BindingBuilder.bind(orderQueue).to(orderExchange).with("order");
    }


    //创建一个队列接收VoucherObj（用户id，优惠券id，订单id）
    @Bean
    public Queue orderQueue() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("x-message-ttl", 1000 * 60 * 15);
        map.put("x-dead-letter-exchange", QueueConstants.DLX_EXCHANGE);
        map.put("x-dead-letter-routing-key", "dlx.any");
        map.put("x-max-length", 200);
        return new Queue(QueueConstants.QUEUE, true, false, false, map);
    }

    @Bean
    public Queue dlxQueue() {
        return new Queue(QueueConstants.DLX_QUEUE, true, false, false);
    }

    @Bean
    public DirectExchange dlxExchange() {
        return new DirectExchange(QueueConstants.DLX_EXCHANGE, true, false);
    }

    @Bean
    public Binding dlxBinding(Queue dlxQueue,DirectExchange dlxExchange) {
        return BindingBuilder.bind(dlxQueue).to(dlxExchange).with("dlx.any");
    }

}
