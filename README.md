 redis spring boot starter
 
  使用方法  
  1.  引入ecode-redis-spring-boot-starter-0.0.2.jar包   
  (1) maven引入：将dist/ecode-redis-spring-boot-starter-0.0.2.jar包安装到您的maven仓库，然后引入依赖：  
    <pre>
      <dependency>
  		     <groupId>com.jsecode.springboot.redis</groupId>
  		     <artifactId>ecode-redis-spring-boot-starter</artifactId>
  		     <version>0.0.1</version>
  	   </dependency>
    </pre> 
  (2)直接引入dist/ecode-redis-spring-boot-starter-0.0.2.jar到项目，同时引入redis包：  
     <pre>
      <dependency>
      	  <groupId>redis.clients</groupId>
      	  <artifactId>jedis</artifactId>
      </dependency>
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
    
  
