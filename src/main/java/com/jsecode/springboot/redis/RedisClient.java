package com.jsecode.springboot.redis;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

public class RedisClient {
    private JedisPool pool;

    public void setPool(JedisPool pool){
        this.pool = pool;
    }
    
    public RedisClient(){}
    public RedisClient(JedisPool pool){ this.pool = pool;}
    
    public Jedis getJedis() {
        return pool.getResource();
    }
    
    public void release(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }
    
    public Set<String> getAllKeys(String pattern){
        Jedis jedis = null;  
        try {  
            jedis = getJedis();  
            return jedis.keys(pattern);
        } catch (Exception e) {
            String err = "从Redis里获取所有匹配("+pattern+")的KEY值发生异常";
            //LOG.error(err, e);
            throw new RuntimeException(err, e);
        }finally{
            release(jedis);
        }
    }
    
    /**
     * 以秒为单位返回 key的剩余过期时间。<br><br>
     * 注：当 key不存在时，返回 -2 。 当 key存在但没有设置剩余生存时间时，返回 -1 。 否则，以秒为单位，返回 key的剩余生存时间。<br>
     *    在 Redis 2.8 以前，当 key 不存在，或者 key 没有设置剩余生存时间时，命令都返回 -1 
     * @param key
     * @return 
     * 
     */
    public Long ttl(String key) { 
        Jedis jedis = null;  
        try {  
            jedis = getJedis();  
            return jedis.ttl(key);
        } catch (Exception e) {
            String err = "从Redis里获取key="+key+"的剩余过期时长发生异常";
            //LOG.error(err, e);
            throw new RuntimeException(err, e);
        }finally{
            release(jedis);
        }
    }
    
    public void set(String key, String value) {  
        if (value == null){
            return;
        }
        
        Jedis jedis = null;  
        try {  
            jedis = getJedis();  
            jedis.set(key, value);
        } catch (Exception e) {
            //LOG.error("往Redis里set[key={}, value={}]的值发生异常", key, value, e);
            throw new RuntimeException("往Redis里设置key="+key+"的值发生异常", e);
        }finally{
            release(jedis);
        }
    }
    
    public void setex(String key, String value, int expireSecond) { 
        if (value == null){
            return;
        }
        
        Jedis jedis = null;  
        try {  
            jedis = getJedis();  
            jedis.setex(key, expireSecond, value);
        } catch (Exception e) { 
            //LOG.error("往Redis里setex[key={}, value={}, expireSecond={}]的值发生异常", key, value, expireSecond, e);
            throw new RuntimeException("往Redis里设置key="+key+"的值发生异常", e);
        }finally{
            release(jedis);
        }
    }
    
    public String get(String key) {  
        Jedis jedis = null;  
        try {  
            jedis = getJedis();  
            return jedis.get(key);
        } catch (Exception e) {  
            String err = "从Redis里获取key="+key+"的值发生异常";
            //LOG.error(err, e);
            throw new RuntimeException(err, e);
        }finally{
            release(jedis);
        }
    }
    
    public void hset(String key, String field, String value) {
        if (value == null){
            value = "";
        }
        
        Jedis jedis = null;  
        try {  
            jedis = getJedis();
            jedis.hset(key, field, value);
        }catch (Exception e) {  
            //LOG.error("往Redis里hset[key={}][field={}, value={}]的值发生异常", key, field, value, e);
            throw new RuntimeException("往Redis里设置key="+key+", field="+field+"的值发生异常", e);
        }finally{
            release(jedis);
        }
    }
    
    public String hget(String key, String field){
        Jedis jedis = null;
        try{
            jedis = getJedis();
            String v = jedis.hget(key, field);
            return v;
        }catch(Exception e){
            String err = "从Redis里获取key="+key+", field="+field+"的值发生异常";
            //LOG.error(err, e);
            throw new RuntimeException(err, e);
        }finally{
            release(jedis);
        }
    }
    
    public Long hdel(String key, String field){
        Jedis jedis =null;
        try{
            jedis = getJedis();
            return jedis.hdel(key, field);
        }catch (Exception e) {  
            String err = "删除Redis里[key="+key+"]的Hash集合中指定元素时发生异常";
            //LOG.error(err, e);
            throw new RuntimeException(err, e);
        }finally{
            release(jedis);
        }
    }
    
    /** 
     * 将多个field - value(域-值)对设置到哈希表key中。 
     */  
    public void hmset(String key, Map<String, String> map) {  
        Jedis jedis = null;  
        try {  
            //查找值为null的field
            List<String> fieldList = map.entrySet().stream()
                                                 .filter(item -> item.getValue() == null)
                                                 .map(item -> item.getKey())
                                                 .collect(Collectors.toList());
            if (fieldList != null){
                fieldList.forEach(item -> map.remove(item));
            }
            
            if (map.isEmpty()){
                return;
            }
            
             jedis = getJedis();
            jedis.hmset(key, map);  
        } catch (Exception e) {  
            //String err = "往Redis里hmset[key="+key+", map="+map+"]的值发生异常";
            //LOG.error(err, e);
            throw new RuntimeException("往Redis里设置key="+key+"的映射值发生异常", e);
        }finally{
            release(jedis);
        }
    }
     
    /** 
     * 返回哈希表key中，所有的域和值 
     */  
    public Map<String, String> hgetAll(String key) {  
        Jedis jedis = null;  
        try {  
            jedis = getJedis();  
            return jedis.hgetAll(key);  
        } catch (Exception e) {  
            String err = "从Redis里hgetAll[key="+key+"]值发生异常";
            //LOG.error(err, e);
            throw new RuntimeException(err, e);
        }finally{
            release(jedis);
        }
    }
    
    /**
     * 批量获取多个key中某个字段的值
     * @param keyList
     * @param field
     * @return Map&lt;key, field的值&gt;
     * 
     */
    public Map<String, String> batchGetFieldValue(List<String> keyList, String field){
        return batchGetFieldValue(keyList.toArray(new String[keyList.size()]), field);
    }
    
    /**
     * 批量获取多个key中某个字段的值
     * @param keyList
     * @param field
     * @return Map&lt;key, field的值&gt;
     * 
     */
    public Map<String, String> batchGetFieldValue(Set<String> keyList, String field){
         return batchGetFieldValue(keyList.toArray(new String[keyList.size()]), field);
    }
    
    /**
     * 批量获取多个key中某个字段的值
     * @param keyList
     * @param field
     * @return Map&lt;key, field的值&gt;
     * 
     */
    public Map<String, String> batchGetFieldValue(String[] keyList, String field){
        Jedis jedis = null; 
        try {
            Map<String, Response<String>> responses = new LinkedHashMap<>(keyList.length);
            jedis = getJedis();
            Pipeline pipeline = jedis.pipelined();
            Arrays.stream(keyList).forEach(key -> responses.put(key, pipeline.hget(key, field)));
            pipeline.sync();
            
            Map<String, String> result = new HashMap<>();
            responses.forEach((k,v) -> {
                String str = v.get();
                if (str != null){
                    result.put(k, str);
                }
            });
            return result;  
        } catch (Exception e) {  
            String err = "从Redis里批量hgetAll[key="+keyList + ", field="+field+"]值发生异常";
            //LOG.error(err, e);
            throw new RuntimeException(err, e);
        }finally{
            release(jedis);
        }
    }
    
    /**
     * 批量获取多个key中某些字段的值
     * @param keyList
     * @param field
     * @return Map&lt;key, Map&lt;field, field的值&gt&gt;
     * 
     */
    public Map<String, Map<String, String>> batchGetFieldsValue(List<String> keyList, String... fields){
        if (fields == null || fields.length <= 0){
            return batchGetAll(keyList);
        }
        
        Jedis jedis = null; 
        try {
            Map<String, Map<String, Response<String>>> responses = new HashMap<>(keyList.size());
            jedis = getJedis();
            Pipeline pipeline = jedis.pipelined();
            keyList.forEach(key -> {
                Map<String, Response<String>> keyMap = new HashMap<>(fields.length);
                responses.put(key, keyMap);
                Arrays.stream(fields).forEach(field -> keyMap.put(field, pipeline.hget(key, field)));
            });
            pipeline.sync();
            
            Map<String, Map<String, String>> result = new HashMap<>();
            responses.forEach((k,v) -> {
                Map<String, String> keyMap = new HashMap<>(fields.length);
                result.put(k, keyMap);
                
                Arrays.stream(fields).forEach(field -> {
                    String str = v.get(field).get();
                    if (str != null){
                        keyMap.put(field, str);
                    }
                });
            });
            return result;  
        } catch (Exception e) {  
            String err = "从Redis里批量hgetAll[key="+keyList + ", fields="+Arrays.toString(fields)+"]值发生异常";
            //LOG.error(err, e);
            throw new RuntimeException(err, e);
        }finally{
            release(jedis);
        }
    }
    

    /** 
     * 返回哈希表key中，所有的域和值 
     */  
    public Map<String, Map<String,String>> batchGetAll(List<String> keyList) {  
        return batchGetAll(keyList.toArray(new String[keyList.size()]));
    }
    
    /** 
     * 返回哈希表key中，所有的域和值 
     */  
    public Map<String, Map<String,String>> batchGetAll(String[] keyList) {  
        Jedis jedis = null; 
        try {
            Map<String, Response<Map<String,String>>> responses = new HashMap<>(keyList.length);
            jedis = getJedis();
            Pipeline pipeline = jedis.pipelined();
            Arrays.stream(keyList).forEach(key -> responses.put(key, pipeline.hgetAll(key)));
            pipeline.sync();
            
            Map<String, Map<String,String>> result = new HashMap<>();
            responses.forEach((k,v) -> {
                Map<String,String> map = v.get();
                if (map != null && !map.isEmpty()){
                    result.put(k, map);
                }
            });
            return result;  
        } catch (Exception e) {  
            String err = "从Redis里批量hgetAll[key="+keyList+"]值发生异常";
            //LOG.error(err, e);
            throw new RuntimeException(err, e);
        }finally{
            release(jedis);
        }
    }
    
    public boolean exists(String key) {  
        Jedis jedis = null;  
        try {  
            jedis = getJedis(); 
            return jedis.exists(key);
        } catch (Exception e) {  
            String err = "判断Redis里是否存在key="+key+"发生异常";
            //LOG.error(err, e);
            throw new RuntimeException(err, e);
        }finally{
            release(jedis);
        }
    }
    
    public boolean hexists(String key, String field){
        Jedis jedis = null;
        try{
            jedis = getJedis();
            return jedis.hexists(key, field);
        }catch(Exception e){
            String err = "判断Redis是否存在key="+key+", field="+field+"的元素发生异常";
            //LOG.error(err, e);
            throw new RuntimeException(err, e);
        }finally{
            release(jedis);
        }
    }
    
    public void expire(String key, int seconds){
        Jedis jedis = null;
        try{
            jedis = getJedis();
            jedis.expire(key, seconds);
        }catch(Exception e){
            String err = "Redis设置key="+key+"的失效时长发生异常";
            //LOG.error(err, e);
            throw new RuntimeException(err, e);
        }finally{
            release(jedis);
        }
    }
    

    /** 
     * redis的List集合 ，向key这个list添加元素 
     */  
    public long rpush(String key, String... listItems) {  
        Jedis jedis = null;
        try{
            jedis = getJedis();
            return jedis.rpush(key, listItems);
        }catch(Exception e){
            //String err = "往Redis里rpush[key="+key+"][value="+listItems+"]的值发生异常";
            //LOG.error(err, e);
            throw new RuntimeException("往Redis里添加key="+key+"的List集合元素发生异常", e);
        }finally{
            release(jedis);
        }
    }
    
    /**
     * 获取redis里list集合的长度
     * @param key
     * @return
     * 
     */
    public long listLen(String key) {  
        Jedis jedis = null;  
        try {  
            jedis = getJedis();  
            return jedis.llen(key);
        } catch (Exception e) {  
            String err = "获取Redis里key="+key+"的list集合的长度发生异常";
            //LOG.error(err, e);
            throw new RuntimeException(err, e);
        }finally{
            release(jedis);
        } 
    }
    
    /** 
     * 获取key这个List，从第几个元素到第几个元素
     * LRANGE key start end 返回列表key中指定区间内的元素，区间以偏移量start和end指定。 
     * 下标(index)参数start和stop都以0为底，也就是说，以0表示列表的第一个元素，以1表示列表的第二个元素，以此类推。 
     * 也可以使用负数下标，以-1表示列表的最后一个元素，-2表示列表的倒数第二个元素，以此类推。 
     */  
    public List<String> lrange(String key, long start, long end) {  
        Jedis jedis = null;  
        try {  
            jedis = getJedis();  
            return jedis.lrange(key, start, end);
        } catch (Exception e) {  
            String err = "从Redis里获取key="+key+"的List集合的start="+start+", end="+end+"的值发生异常";
            //LOG.error(err, e);
            throw new RuntimeException(err, e);
        }finally{
            release(jedis);
        } 
    }
    

    /** 
     * 返回key值的类型 none(key不存在), string(字符串), list(列表), set(集合), zset(有序集), hash(哈希表) 
     */  
    public String type(String key) {  
        Jedis jedis = null;  
        try {  
            jedis = getJedis();
            return jedis.type(key);
        } catch (Exception e) {  
            String err = "返回Redis里[key="+key+"]值的type发生异常";
            //LOG.error(err, e);
            throw new RuntimeException(err, e);
        }finally{
            release(jedis);
        }
    }  
    

    /**
     * 返回set集合中所有值
     * @param key
     * @return
     * 
     */
    public Set<String> smembers(String key) {  
        Jedis jedis = null;  
        try {  
            jedis = getJedis();
            return jedis.smembers(key);  
        } catch (Exception e) {  
            String err = "从Redis里获取key="+key+"的Set集合发生异常";
            //LOG.error(err, e);
            throw new RuntimeException(err, e);
        }finally{
            release(jedis);
        }
    }
    
    /**
     * 批量返回集合中的值
     * @param keyList
     * @return Map&lt;key, Set&lt;key对应的set集合&gt;&gt;
     * 
     */
    public Map<String, Set<String>> batchSmembers(List<String> keyList) {  
        Jedis jedis = null;  
        try {  
            jedis = getJedis();
            Pipeline pipeline = jedis.pipelined();
            
            Map<String, Response<Set<String>>> map = new HashMap<>();
            keyList.forEach(key -> map.put(key, pipeline.smembers(key)));
            pipeline.sync();
            
            Map<String, Set<String>> result = new HashMap<>();
            map.forEach((k, v) -> {
                Set<String> tmp = v.get();
                if (tmp != null && !tmp.isEmpty()){
                    result.put(k, tmp);
                }
            });
            return result;
        } catch (Exception e) {  
            String err = "批量从Redis里获取key="+keyList+"的Set集合发生异常";
            //LOG.error(err, e);
            throw new RuntimeException(err, e);
        }finally{
            release(jedis);
        }
    }
    
    /** 
     * 往set集合中添加member元素 
     */  
    public void sadd(String key, String... members) {  
        Jedis jedis = null;  
        try {  
            jedis = getJedis();  
            jedis.sadd(key, members);
        } catch (Exception e) {  
            String err = "从Redis里sadd[key="+key+"]的元素时发生异常";
            //LOG.error(err, e);
            throw new RuntimeException(err, e);
        }finally{
            release(jedis);
        }
    }
    
    /** 
     * 移除set集合中的member元素 
     */  
    public void srem(String key, String... members) {  
        Jedis jedis = null;  
        try {  
            jedis = getJedis();  
            jedis.srem(key, members);
        } catch (Exception e) {  
            String err = "从Redis里srem[key="+key+"]的元素时发生异常";
            //LOG.error(err, e);
            throw new RuntimeException(err, e);
        }finally{
            release(jedis);
        }
    }
     
    public void del(String... key) {  
        Jedis jedis = null;  
       try {  
           jedis = getJedis();  
           jedis.del(key);
       } catch (Exception e) {  
           String err = "从Redis里移除[key="+key+"]的元素时发生异常";
           //LOG.error(err, e);
           throw new RuntimeException(err, e);
       }finally{
           release(jedis);
       }
   }
 
    /** 
     * 判断member元素是否是集合key的成员。是（true），否则（false） 
     */  
    public boolean contain(String key, String member) {  
        Jedis jedis = null;  
        try {
            jedis = getJedis();
             return jedis.sismember(key, member);  
        } catch (Exception e) {  
            String err = "判断Redis里[key="+key+"]的Set集合中是否包含指定元素时发生异常";
            //LOG.error(err, e);
            throw new RuntimeException(err, e);
        }finally{
            release(jedis);
        }
    }
    
    public Long incrby(String key, Integer inval){
        Jedis jedis =null;
        try{
            jedis = getJedis();
            return jedis.incrBy(key, inval);
        }catch (Exception e) {  
            String err = "对Redis里incrby[key="+key+"]的指定元素进行增量操作时发生异常";
            //LOG.error(err, e);
            throw new RuntimeException(err, e);
        }finally{
            release(jedis);
        }
    }
    
    public Long hincrby(String key, String field, Integer inval){
        Jedis jedis =null;
        try{
            jedis = getJedis();
            return jedis.hincrBy(key, field, inval);
        }catch (Exception e) {  
            String err = "对Redis里hincrby[key="+key+"]的Set集合中指定元素进行增量操作发生异常";
            //LOG.error(err, e);
            throw new RuntimeException(err, e);
        }finally{
            release(jedis);
        }
    }
    

    /**
     * 发布/订阅模式，发布消息
     * @param channel
     * @param message
     * 
     */
    public void publishMsg(String channel, String message){
        Jedis jedis = null;
        try{
            jedis = getJedis();
            jedis.publish(channel, message);
        }catch(Exception e){
            String err = "publishMsg exception: channel=" + channel + ", message=" + message;
            //LOG.error(err, e);
            throw new RuntimeException(err, e);
        }finally{
            release(jedis);
        }
    }
    
    /**
     * 发布/订阅模式，发布消息
     * @param channel
     * @param message
     */
    public void publishMsg(byte[] channel, byte[] message){
        Jedis jedis = null;
        try{
            jedis = getJedis();
            jedis.publish(channel, message);
        }catch(Exception e){
            String err = "publishMsg exception: channel=" + channel + ", message=" + message;
            //LOG.error(err, e);
            throw new RuntimeException(err, e);
        }finally{
            release(jedis);
        }
    }
    
    /**
     * 订阅消息主题（注意：订阅操作是阻塞模式）
     * @param jedisPubSub
     * @param channels
     * 
     */
    public void subscribeMsg(JedisPubSub jedisPubSub, String... channels){
        Jedis jedis = null;
        try{
            jedis = getJedis();
            jedis.subscribe(jedisPubSub, channels);
        }catch(Exception e){
            //LOG.error("subscribeMsg {} = {}", jedisPubSub, channels, e);
        }finally{
            release(jedis);
        }
    }
    
    public Object eval(String script){
        Jedis jedis =null;
        try{
            jedis = getJedis();
            return jedis.eval(script);
        }catch (Exception e) {  
            String err = "Redis执行脚本时发生异常。脚本：" + script;
            //LOG.error(err, e);
            throw new RuntimeException(err, e);
        }finally{
            release(jedis);
        }
    }
    
    public Object eval(String script, List<String> keys, List<String> args){
        Jedis jedis =null;
        try{
            jedis = getJedis();
            return jedis.eval(script, keys, args);
        }catch (Exception e) {  
            String err = "Redis执行脚本时发生异常。脚本：" + script;
            //LOG.error(err, e);
            throw new RuntimeException(err, e);
        }finally{
            release(jedis);
        }
    }
    
    /**
     * 检查并锁定KEY
     * @param key  
     * @return 当前请求线程锁定KEY，返回1，当前线程未锁定KEY(已有其他线程锁定)，返回0
     * 
     */
    public String checkAndLockKey(String key){
        return checkAndLockKey(key, -1);
    }
    
    public String checkAndLockKey(String key, int expireSeconds){ 
        return checkAndLockKey(key, null, expireSeconds);
    }
    
    /**
     * 检查并锁定KEY
     * @param key 
     * @param data 锁定KEY时需要写入的数据  
     * @return 当前请求线程锁定KEY，返回1，当前线程未锁定KEY(已有其他线程锁定)，返回0
     * 
     */
    public String checkAndLockKey(String key, Map<String, String> data){
        return checkAndLockKey(key, data, -1);
    }
    
    private String getCurSysTime(){
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
  
    public String checkAndLockKey(String key, Map<String, String> data, int expireSeconds){
        if (data == null || data.isEmpty()){
            data = new HashMap<>();
            data.put("lockTime", getCurSysTime());
        }
        
        List<String> params = new ArrayList<>();
        StringBuilder luaScript = new StringBuilder();
        luaScript.append("local caseInHandle = redis.call('exists',KEYS[1]); ");
        luaScript.append("if caseInHandle == 1 then ");
        luaScript.append(" return 0  ");
        luaScript.append("end;");
        luaScript.append("redis.call('hmset', KEYS[1]");
        int i = 1; 
        for (Entry<String, String> entry: data.entrySet()){
            luaScript.append(", ARGV[").append(i++).append("], ARGV[").append(i++).append("]");
            params.add(entry.getKey());
            params.add(entry.getValue());
        }
        luaScript.append("); ");
         
        if (expireSeconds > 0){
            luaScript.append("redis.call('expire', KEYS[1], ARGV[").append(i++).append("]);");
            params.add(String.valueOf(expireSeconds));
        }
        luaScript.append("return 1;");
        
        return eval(luaScript.toString(), Arrays.asList(key), params).toString();
    }
 
}
