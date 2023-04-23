package com.capol.notify.producer.application.message;

import com.capol.notify.manage.domain.model.IdGenerator;
import com.capol.notify.manage.domain.model.permission.CurrentUserService;
import com.capol.notify.producer.application.message.command.DingDingMsgCommand;
import com.capol.notify.producer.domain.model.message.MessageProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ApplyForOTMessageService {

    private MessageProducer messageProducer;
    private CurrentUserService currentUserService;

    public ApplyForOTMessageService(MessageProducer messageProducer, CurrentUserService currentUserService) {
        this.messageProducer = messageProducer;
        this.currentUserService = currentUserService;
    }

    public void sentApplyNoticeMsg(DingDingMsgCommand otMsgCommand) {
        if (otMsgCommand.getUserId() == null) {
            Long userId = currentUserService.getCurrentUserId() != null ? Long.valueOf(currentUserService.getCurrentUserId()) : null;
            otMsgCommand.setUserId(userId);
        }
        if (otMsgCommand.getMessageId() == null) {
            otMsgCommand.setMessageId(IdGenerator.generateId());
        }
        log.info("-->发送消息ID:{}", otMsgCommand.getMessageId());
        messageProducer.sentApplyNoticeMsg(otMsgCommand);
    }
}
