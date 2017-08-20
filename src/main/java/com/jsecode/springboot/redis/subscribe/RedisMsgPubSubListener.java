package com.jsecode.springboot.redis.subscribe;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import redis.clients.jedis.JedisPubSub;

public class RedisMsgPubSubListener extends JedisPubSub {
    private static Logger logger = LoggerFactory.getLogger(RedisMsgPubSubListener.class);
    
    private Map<String, IMsgCallback> msgCallBackMap = new ConcurrentHashMap<>();
    
    private static class SingletonHolder {
        private static RedisMsgPubSubListener instance = new RedisMsgPubSubListener();
    }
    
    public static RedisMsgPubSubListener getInstance(){
        return SingletonHolder.instance;
    }
    
    /**
     * 添加订阅的主题消息的回调处理器
     * @param channel
     * @param mc
     * 
     */
    public void addChannelMsgCallback(String channel, IMsgCallback mc){
        msgCallBackMap.put(channel, mc);
    }

    /**
     * 取得订阅的消息后的处理
     */
    @Override
    public void onMessage(String channel, String message){
        logger.debug("onMessage: channel[{}], message[{}]", channel, message);
        IMsgCallback mc = msgCallBackMap.get(channel);
        if (mc == null){
            logger.warn("onMessage: No IMsgCallback instance for channel[{}] , message[{}]", channel, message);
        }else{
            mc.callback(message);
        } 
    }

    /**
     * 取得按表达式的方式订阅的消息后的处理
     */
    @Override
    public void onPMessage(String pattern, String channel, String message){
        logger.debug("onPMessage: channel[{}], message[{}]", channel, message);

    }

    /**
     * 初始化订阅时候的处理
     */
    @Override
    public void onSubscribe(String channel, int subscribedChannels){
        logger.debug("onSubscribe: channel[{}], subscribedChannels[{}]", channel, subscribedChannels);

    }

    /**
     * 取消订阅时候的处理
     */
    @Override
    public void onUnsubscribe(String channel, int subscribedChannels){
        logger.debug("onUnsubscribe: channel[{}], subscribedChannels[{}]", channel, subscribedChannels);

    }

    /**
     * 取消按表达式的方式订阅时候的处理
     */
    @Override
    public void onPUnsubscribe(String pattern, int subscribedChannels){
        logger.debug("onPUnsubscribe: pattern[{}], subscribedChannels[{}]", pattern, subscribedChannels);

    }

    /**
     * 初始化按表达式的方式订阅时候的处理
     */
    @Override
    public void onPSubscribe(String pattern, int subscribedChannels){
        logger.debug("onPSubscribe: pattern[{}], subscribedChannels[{}]", pattern, subscribedChannels);

    }
}
