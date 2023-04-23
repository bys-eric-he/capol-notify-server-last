package com.capol.notify.sdk;

/**
 * 消息消费回调
 */
public abstract class MessageReceiveConfirmCallback {
    /**
     * 消息消费成功回调方法
     *
     * @param ack 是否消费成功
     */
    public abstract void receiveConfirmCallback(boolean ack);
}
