 redis spring boot starter
 
  使用方法  
  1.  maven引入  
  `
    <dependency>
  		<groupId>com.jsecode.springboot.redis</groupId>
  		<artifactId>ecode-redis-spring-boot-starter</artifactId>
  		<version>0.0.1</version>
  	</dependency>
  `
    
  2. application.properties 配置redis参数  
    ecode.redis.host=192.168.1.150  
    ecode.redis.port=6379  
    ecode.redis.timeout=3000  
    ecode.redis.password=Jsecode123  
    ecode.redis.max-active=50  
    ecode.redis.max-idle=5  
    ecode.redis.min-idle=0  
    ecode.redis.max-waitMillis=5000  
    ecode.redis.test-on-borrow=false  
    ecode.redis.test-on-return=false  
    ecode.redis.dbidx=0  
    
  