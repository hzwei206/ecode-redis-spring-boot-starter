/**
 * 
 */
package com.jsecode.springboot.redis;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 *
 * @author huangzw
 * @date 2017年6月1日 上午8:46:36
 * @version 1.0
 */
@ConfigurationProperties(prefix = "ecode.redis")
public class RedisProperties {
    private String host;
    private int port = 6379;
    private int timeout = 3000;
    private String password;
    private int maxActive = 10;
    private int maxIdle = 5;
    private int minIdle = 0;
    private long maxWaitMillis = 5000;
    private boolean testOnBorrow = false;
    private boolean testOnReturn = false;
    
    private int dbidx;

    public String getHost(){
        return host;
    }

    public void setHost(String host){
        this.host = host;
    }

    public int getPort(){
        return port;
    }

    public void setPort(int port){
        this.port = port;
    }

    public int getTimeout(){
        return timeout;
    }

    public void setTimeout(int timeout){
        this.timeout = timeout;
    }

    public String getPassword(){
        return password;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public int getMaxActive(){
        return maxActive;
    }

    public void setMaxActive(int maxActive){
        this.maxActive = maxActive;
    }

    public int getMaxIdle(){
        return maxIdle;
    }

    public void setMaxIdle(int maxIdle){
        this.maxIdle = maxIdle;
    }

    public int getMinIdle(){
        return minIdle;
    }

    public void setMinIdle(int minIdle){
        this.minIdle = minIdle;
    }

    public long getMaxWaitMillis(){
        return maxWaitMillis;
    }

    public void setMaxWaitMillis(long maxWaitMillis){
        this.maxWaitMillis = maxWaitMillis;
    }

    public boolean isTestOnBorrow(){
        return testOnBorrow;
    }

    public void setTestOnBorrow(boolean testOnBorrow){
        this.testOnBorrow = testOnBorrow;
    }

    public boolean isTestOnReturn(){
        return testOnReturn;
    }

    public void setTestOnReturn(boolean testOnReturn){
        this.testOnReturn = testOnReturn;
    }

    public int getDbidx() {
        return dbidx;
    }

    public void setDbidx(int dbidx) {
        this.dbidx = dbidx;
    }

}
