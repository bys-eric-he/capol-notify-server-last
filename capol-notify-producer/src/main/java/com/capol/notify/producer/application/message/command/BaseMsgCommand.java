package com.capol.notify.producer.application.message.command;

import com.capol.notify.manage.domain.EnumMessageBusinessType;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@SuperBuilder
public class BaseMsgCommand implements Serializable {

    private static final long serialVersionUID = -6643212454457854652L;

    /**
     * 消息ID
     */
    private Long messageId;

    /**
     * 当前发送消息的身份ID(通过该ID，获取用户队列信息)
     */
    private Long userId;

    /**
     * 业务类型(根据业务类型确定要发送的消息队列)
     */
    private EnumMessageBusinessType businessType;

    /**
     * 消息类型（标识该消息是业务发送方发送，还是定时任务重试发送）
     */
    private Integer sendType;
}
