package com.capol.notify.manage.application.message;

import com.capol.notify.manage.domain.model.message.UserQueueMessageDO;
import com.capol.notify.manage.domain.repository.UserQueueMessageMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 消息处理应用层服务
 *
 * @author heyong
 * @since 2023-04-21 16:52:11
 */
@Slf4j
@Service
public class MessageService {

    private UserQueueMessageMapper userQueueMessageMapper;

    public MessageService(UserQueueMessageMapper userQueueMessageMapper) {
        this.userQueueMessageMapper = userQueueMessageMapper;
    }

    /**
     * 获取指定MessageID的消息
     *
     * @param id
     * @return
     */
    public UserQueueMessageDO getMessageById(Long id) {
        return userQueueMessageMapper.findByMessageId(id);
    }

    /**
     * 保存或更新消息
     *
     * @param messageDO
     * @return
     */
    public Long saveOrUpdateMessage(UserQueueMessageDO messageDO) {
        UserQueueMessageDO userQueueMessageDO = userQueueMessageMapper.findByMessageId(messageDO.getId());
        if (userQueueMessageDO == null) {
            int rows = userQueueMessageMapper.insert(messageDO);
            if (rows > 0) {
                log.info("-->保存消息成功!");
            } else {
                log.error("-->保存消息失败, 消息ID:{}", messageDO.getId());
            }
        } else {
            messageDO.buildBaseInfo();
            int rows = userQueueMessageMapper.updateById(messageDO);
            if (rows > 0) {
                log.info("-->更新消息成功!");
            } else {
                log.error("-->更新消息失败, 消息ID:{}", messageDO.getId());
            }
        }

        return messageDO.getId();
    }
}
