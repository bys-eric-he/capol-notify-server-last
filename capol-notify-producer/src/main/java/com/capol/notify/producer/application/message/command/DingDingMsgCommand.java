package com.capol.notify.producer.application.message.command;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
public class DingDingMsgCommand extends BaseMsgCommand {

    /**
     * 消息类型(1.text 2.image 3.file 4.link 5.markdown 6.action_card)
     */
    private int msgType;

    /**
     * 接收者的用户userIds列表，最大列表长度100
     */
    private List<String> userIds;
    /**
     * 应用agentId
     */
    private Long agentId;

    /**
     * 消息内容
     */
    private String content;
}
