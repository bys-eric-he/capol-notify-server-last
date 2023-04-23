package com.capol.notify.producer.port.adapter.restapi.controller.message.parameter;

import com.capol.notify.manage.domain.EnumMessageBusinessType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@ApiModel("消息传输对象")
@Data
@NoArgsConstructor
public class MessageRequestParam {

    @ApiModelProperty("消息类型(1.text 2.image 3.file 4.link 5.markdown 6.action_card)")
    private int msgType;

    @ApiModelProperty("消息业务类型")
    private EnumMessageBusinessType businessType;

    @ApiModelProperty("接收者的用户userIds列表，最大列表长度100")
    private List<String> userIds;

    @ApiModelProperty("应用agentId")
    private Long agentId;

    @ApiModelProperty("消息内容")
    private String content;

}
