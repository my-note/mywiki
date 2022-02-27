package com.ws.demo.netty;

import lombok.Data;

/**
 * @author user
 */
@Data
public class ChatMsg {


    /**
     * 发送者的用户id
     */
    private String senderId;
    /**
     * 接受者的用户id
     */
    private String receiverId;
    /**
     * 聊天内容
     */
    private String msg;
    /**
     * 用于消息的签收
     * 消息会保存到数据库，数据库的id
     */
    private String msgId;


}
