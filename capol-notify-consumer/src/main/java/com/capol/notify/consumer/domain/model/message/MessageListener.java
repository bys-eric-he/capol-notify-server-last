package com.capol.notify.consumer.domain.model.message;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.capol.notify.manage.domain.DomainException;
import com.capol.notify.manage.domain.EnumExceptionCode;
import com.capol.notify.manage.domain.model.queue.UserQueueDO;
import com.capol.notify.manage.domain.repository.UserQueueMapper;
import com.capol.notify.sdk.MessageReceiveConfirmCallback;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 统一监听入口
 */
@Slf4j
@Component
@Order(1)
public class MessageListener implements CommandLineRunner {

    private RabbitTemplate rabbitTemplate;
    private final UserQueueMapper userQueueMapper;
    private MessageReceiveConfirmCallback messageReceiveConfirmCallback;

    public MessageListener(RabbitTemplate rabbitTemplate,
                           UserQueueMapper userQueueMapper,
                           MessageReceiveConfirmCallback messageReceiveConfirmCallback) {
        this.rabbitTemplate = rabbitTemplate;
        this.userQueueMapper = userQueueMapper;
        this.messageReceiveConfirmCallback = messageReceiveConfirmCallback;
    }

    @Override
    public void run(String... args) throws Exception {
        LambdaQueryWrapper<UserQueueDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserQueueDO::getDisabled, false);
        queryWrapper.eq(UserQueueDO::getStatus, 1);
        List<UserQueueDO> userQueueDOS = userQueueMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(userQueueDOS)) {
            log.warn("-->当前服务无队列配置信息!!!");
            return;
        }

        //取出所有交换机并去重
        List<String> exchanges = userQueueDOS.stream().map(exchange -> exchange.getExchange()).distinct().collect(Collectors.toList());

        final ConnectionFactory factory = rabbitTemplate.getConnectionFactory();
        final Connection connection = factory.createConnection();

        // 创建频道
        Channel channel = connection.createChannel(false);

        // 保证一次只分发一次 限制发送给同一个消费者 不得超过一条消息
        channel.basicQos(1);

        /**
         * 声明交换机
         * 参数1：交换机名称
         * 参数2：交换机类型，fanout、topic、direct、headers
         */
        exchanges.forEach(exchange -> {
            try {
                channel.exchangeDeclare(exchange, BuiltinExchangeType.DIRECT);
                log.info("-->声明交换机:{} 成功! ", exchange);
            } catch (IOException exception) {
                log.error("声明交换机:{} 发生异常!! 异常详情：{}", exchange, exception);
                throw new DomainException(String.format("声明交换机:%s 发生异常!!", exchange), EnumExceptionCode.InternalServerError);
            }
        });

        /** 声明（创建）队列
         * 参数1：队列名称
         * 参数2：是否定义持久化队列
         * 参数3：是否独占本次连接
         * 参数4：是否在不使用的时候自动删除队列
         * 参数5：队列其它参数
         */
        userQueueDOS.forEach(queue -> {
            try {
                //创建队列
                channel.queueDeclare(queue.getQueue(), true, false, false, null);
                //队列绑定交换机
                channel.queueBind(queue.getQueue(), queue.getExchange(), queue.getRouting());
                log.info("-->创建监听队列:{} 成功! 绑定的交换机:{},路由:{}", queue.getQueue(), queue.getExchange(), queue.getRouting());
            } catch (IOException exception) {
                log.error("-->创建监听队列:" + queue + "异常! 详细内容：" + exception);
                throw new DomainException(String.format("创建监听队列:{} 异常!", queue), EnumExceptionCode.InternalServerError);
            }
        });


        //创建消费者, 并设置消息处理
        MessageConsumer messageConsumer = new MessageConsumer(messageReceiveConfirmCallback);
        messageConsumer.setChannel(channel);
        //监听消息
        for (UserQueueDO queue : userQueueDOS) {
            /**
             * 参数1：队列名称
             * 参数2：是否自动确认，设置为true为表示消息接收到自动向mq回复接收到了，mq接收到回复会删除消息，设置为false则需要手动确认
             * 参数3：消息接收到后回调
             */
            try {
                channel.basicConsume(queue.getQueue(), false, messageConsumer);
                log.info("-->创建监听队列:{},并设置消息接收入口!", queue.getQueue());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
