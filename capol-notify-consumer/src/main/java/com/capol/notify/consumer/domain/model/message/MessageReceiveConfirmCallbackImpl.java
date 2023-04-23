package com.capol.notify.consumer.domain.model.message;

import com.capol.notify.sdk.MessageReceiveConfirmCallback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MessageReceiveConfirmCallbackImpl extends MessageReceiveConfirmCallback {
    /**
     * 消息消费成功回调方法
     *
     * @param ack 是否消费成功
     */
    @Override
    public void receiveConfirmCallback(boolean ack) {
        log.info("-->消息消费成功回调方法!!ACK：{}", ack);
    }
}
