package com.ws.demo.netty;

import lombok.Data;

import java.io.Serializable;


@Data
public class DataContent {


    /**
     * 动作类型
     */
    private Integer action;
    /**
     * 用户的聊天内容entity
     */
    private ChatMsg chatMsg;
    /**
     * 扩展字段
     */
    private String extand;


}
