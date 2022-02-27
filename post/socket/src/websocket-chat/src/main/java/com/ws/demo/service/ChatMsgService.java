package com.ws.demo.service;


import com.ws.demo.entity.Msg;
import com.ws.demo.netty.ChatMsg;
import com.ws.demo.netty.MsgSignFlagEnum;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ChatMsgService {


    private static final Map<String, Msg> MSG_DB = new HashMap<>(16);


    /**
     * 保存消息到db
     */
    public String saveMsg(ChatMsg chatMsg) {

        String msgId = UUID.randomUUID().toString();

        Msg msg = new Msg();

        msg.setId(msgId);
        msg.setCreateTime(System.currentTimeMillis());
        msg.setSignFlag(MsgSignFlagEnum.unsign.type);

        msg.setAcceptUserId(chatMsg.getReceiverId());
        msg.setSendUserId(chatMsg.getSenderId());
        msg.setMsg(chatMsg.getMsg());

        MSG_DB.put(msgId, msg);

        return msgId;
    }


    /**
     * 修改消息签收状态
     */
    public void updateMsgSigned(List<String> msgIdList) {

        for (String msgId : msgIdList) {
            Msg msg = MSG_DB.get(msgId);
            msg.setSignFlag(MsgSignFlagEnum.signed.type);
        }

    }
}
