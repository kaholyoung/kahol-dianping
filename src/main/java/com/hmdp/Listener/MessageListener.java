package com.hmdp.Listener;


import com.alibaba.fastjson.JSON;
import com.hmdp.Constants.QueueConstants;
import com.hmdp.entity.VoucherOrder;
import com.hmdp.exception.MyException;
import com.hmdp.service.impl.VoucherOrderServiceImpl;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Component
@Slf4j
public class MessageListener {

    @Autowired
    private VoucherOrderServiceImpl service;

    @RabbitListener(queues = QueueConstants.QUEUE)
    @Transactional(rollbackFor = Exception.class)
    public void handleOrderQueueMessage(Channel channel, Message message) throws IOException, MyException {

        long deliveryTag = message.getMessageProperties().getDeliveryTag();

        try {
            String order = new String(message.getBody());
            VoucherOrder voucherOrder = JSON.parseObject(order, VoucherOrder.class);
            service.createVoucherOrder(voucherOrder);
            channel.basicAck(deliveryTag,false);
        } catch (Exception e) {
            log.error("出现异常：{}",e.getMessage());
            channel.basicNack(deliveryTag,false,false);
            throw new MyException("出现异常，回滚订单");
        }


    }

    @RabbitListener(queues = QueueConstants.DLX_QUEUE)
    @Transactional(rollbackFor = Exception.class)
    public void handleDlxMessage(Channel channel,Message message) throws Exception {

        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            String order = new String(message.getBody());
            VoucherOrder voucherOrder = JSON.parseObject(order, VoucherOrder.class);
            service.createVoucherOrder(voucherOrder);
            channel.basicAck(deliveryTag,false);
        } catch (Exception e) {
            Thread.sleep(60 * 1000);
            log.error("出现异常：{}",e.getMessage());
            channel.basicNack(deliveryTag,false,true);
        }
    }

}
