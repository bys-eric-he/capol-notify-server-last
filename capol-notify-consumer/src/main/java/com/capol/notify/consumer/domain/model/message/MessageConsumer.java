package com.capol.notify.consumer.domain.model.message;

import com.capol.notify.manage.domain.model.message.MQMessageEdit;
import com.capol.notify.sdk.MessageReceiveConfirmCallback;
import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class MessageConsumer implements Consumer {

    private Channel channel;
    private MessageReceiveConfirmCallback messageReceiveConfirmCallback;

    public MessageConsumer(MessageReceiveConfirmCallback messageReceiveConfirmCallback) {
        this.messageReceiveConfirmCallback = messageReceiveConfirmCallback;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    /**
     * consumerTag 消息者标签，在channel.basicConsume时候可以指定
     * envelope 消息包的内容，可从中获取消息id，消息routingkey，交换机，消息和重传标志(收到消息失败后是否需要重新发送)
     * properties 属性信息
     * body 消息
     */
    @MQMessageEdit(argsIndex = 3)
    @Override
    public void handleDelivery(String consumerTag, Envelope envelope,
                               AMQP.BasicProperties properties, byte[] body) throws IOException {
        String message = new String(body, "UTF-8");
        //路由key
        log.info("路由key为：" + envelope.getRoutingKey());
        //交换机
        log.info("交换机为：" + envelope.getExchange());
        //消息id
        log.info("消息id为：" + envelope.getDeliveryTag());
        //收到的消息
        log.info("消息者： " + consumerTag + ", 收到消息: " + message);
        // 处理消息
        channel.basicAck(envelope.getDeliveryTag(), false); // 确认消息已经消费
        if (messageReceiveConfirmCallback != null) {
            messageReceiveConfirmCallback.receiveConfirmCallback(true);
        }
    }

    @Override
    public void handleConsumeOk(String consumerTag) {
        log.info("消息者： " + consumerTag + " 注册成功 successfully!");
    }


    @Override
    public void handleCancelOk(String consumerTag) {
        log.info("消息者： " + consumerTag + " 取消成功 cancelOk!!");
    }

    @Override
    public void handleCancel(String consumerTag) throws IOException {
        log.info("消息者: " + consumerTag + " 已经取消 canceled!!!");
    }

    @Override
    public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {
        log.info("消息者： " + consumerTag + " 已经注销  shutdown!");
    }

    @Override
    public void handleRecoverOk(String consumerTag) {
        log.info("消息者： " + consumerTag + " 恢复成功 recoverOK!!");
    }

    /**
     * 指定队列监听
     *
     * @param message
     */
    @MQMessageEdit(argsIndex = 0)
    @RabbitListener(queues = "capol_notify_queue_apply_for_leave")
    public void handleMessage(String message) {
        System.out.println("RabbitListener 监听收到消息 Received message: " + message);
    }
}