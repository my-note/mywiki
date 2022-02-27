package com.ws.demo.entity;


import lombok.Data;

@Data
public class Msg {

    private String id;


    private String sendUserId;


    private String acceptUserId;

    private String msg;

    /**
     * 消息是否签收状态
     *  1：签收
     *  0：未签收
     */
    private Integer signFlag;


    private Long createTime;


}