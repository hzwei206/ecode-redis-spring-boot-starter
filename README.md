 redis spring boot starter
 
  使用方法  
  1.  引入ecode-redis-spring-boot-starter-0.0.2.jar包   
  (1) maven引入：将dist/ecode-redis-spring-boot-starter-0.0.2.jar包安装到您的maven仓库，然后引入依赖：
   <pre>
    &lt;dependency&gt;
  	     &lt;groupId&gt;com.jsecode.springboot.redis&lt;/groupId&gt;
  	     &lt;artifactId&gt;ecode-redis-spring-boot-starter&lt;/artifactId&gt;
  	     &lt;version&gt;0.0.2&lt;/version&gt;
    &lt;/dependency&gt;
    </pre> 
  (2)直接引入dist/ecode-redis-spring-boot-starter-0.0.2.jar到项目，同时引入redis包：  
    <pre>
      &lt;dependency&gt;
      	  &lt;groupId>redis.clients&lt;/groupId&gt;
      	  &lt;artifactId>jedis&lt;/artifactId&gt;
      &lt;/dependency&gt;
    </pre>  
 
  2. application.properties 配置redis参数  
    ecode.redis.host=192.168.1.110  
    ecode.redis.port=6379  
    ecode.redis.timeout=3000  
    ecode.redis.password=redis_password  
    ecode.redis.max-active=50  
    ecode.redis.max-idle=5  
    ecode.redis.min-idle=0  
    ecode.redis.max-waitMillis=5000  
    ecode.redis.test-on-borrow=false  
    ecode.redis.test-on-return=false  
    ecode.redis.dbidx=0  
 
3.项目代码里通过@Autowired注解注入com.jsecode.springboot.redis.RedisClient类：
   <pre>
      @Autowired
      RedisClient redisClient;
   </pre>
   通过RedisClient类操作redis命令
