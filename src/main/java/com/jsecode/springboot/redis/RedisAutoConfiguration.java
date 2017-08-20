package com.jsecode.springboot.redis;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
@EnableConfigurationProperties(RedisProperties.class)
@ConditionalOnClass({ JedisPoolConfig.class, JedisPool.class, Jedis.class })
public class RedisAutoConfiguration {
    @Autowired
    private RedisProperties properties;
    
    private JedisPool jedisPool;
    
    @PostConstruct
    public void initJedisPool() {
        System.out.print("------初始化JedisPool.....host=" + properties.getHost());
        boolean hostExist = properties.getHost() != null && properties.getHost().trim().length() > 0;
        Assert.isTrue(hostExist, "Redis主机地址不能为空，请配置[ecode.redis.host]");
        
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(properties.getMaxActive());
        config.setMaxIdle(properties.getMaxIdle());
        config.setMinIdle(properties.getMinIdle());
        config.setMaxWaitMillis(properties.getMaxWaitMillis());
        config.setTestOnBorrow(properties.isTestOnBorrow());
        config.setTestOnReturn(properties.isTestOnReturn()); 
        
        if (properties.getPassword() == null || properties.getPassword().trim().length() <= 0){
            jedisPool = new JedisPool(config, properties.getHost(), properties.getPort(), 
                                        properties.getTimeout(), null, properties.getDbidx());
        }else{
            jedisPool = new JedisPool(config, properties.getHost(), properties.getPort(), properties.getTimeout(), 
                                        properties.getPassword(), properties.getDbidx());
        }
        
        System.out.println(".....ok!");
    }
     
    @Bean
    @ConditionalOnMissingBean
    public RedisClient redisClient(){
        Assert.isTrue(jedisPool != null, "JedisPool尚未初始化，无法初始化RedisClient");
        return new RedisClient(jedisPool);
    }
}
