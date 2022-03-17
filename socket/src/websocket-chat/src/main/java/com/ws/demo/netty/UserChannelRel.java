package com.ws.demo.netty;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.channel.Channel;

/**
 * @author user
 * @Description: 用户id和channel的关联关系处理，需要手动添加手动移除
 */
public class UserChannelRel {

    private static final Map<String, Channel> MANAGER = new ConcurrentHashMap<>();

    public static void put(String senderId, Channel channel) {
        MANAGER.put(senderId, channel);
    }

    public static Channel get(String senderId) {
        return MANAGER.get(senderId);
    }

    public static void output() {
        for (HashMap.Entry<String, Channel> entry : MANAGER.entrySet()) {
            System.out.println("UserId: " + entry.getKey()
                    + ", ChannelId: " + entry.getValue().id().asLongText());
        }
    }
}
