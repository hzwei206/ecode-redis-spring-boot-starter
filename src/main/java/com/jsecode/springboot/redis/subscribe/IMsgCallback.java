/**
 * 
 */
package com.jsecode.springboot.redis.subscribe;

/**
 * 消息回调接口
 *
 * @author huangzw@jsecode.com
 * @date 2017年3月2日 上午9:32:44
 * @version 1.0
 */
public interface IMsgCallback {
    void callback(String msg);
}
