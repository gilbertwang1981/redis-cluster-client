package com.hs.base.cache.redis.test;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RedisClusterTestCaller {
	
	private static Logger logger = LoggerFactory.getLogger(RedisClusterTestCaller.class);

	@Autowired
	private RedisClusterClientTestService redisClusterClientTestService;
	
	@PostConstruct
	private void init() {
		new Timer().scheduleAtFixedRate(new TimerTask() {
			public void run() {
				call();
			}
		} , 5000 , 5000); 
	}
	
	private void call() {
		logger.info(redisClusterClientTestService.del("t")? "删除缓存成功" : "删除设置失败");
		
		redisClusterClientTestService.set("t", "32323");
		
		logger.info(redisClusterClientTestService.get("t"));
		logger.info(redisClusterClientTestService.del("t")? "删除缓存成功" : "删除设置失败");
		logger.info(redisClusterClientTestService.get("t"));
		
		for (int i = 0;i < 20 ; i ++) {
			redisClusterClientTestService.set("t-" + i , "t" + i);
		}
		
		Map<String , Object> kvs = new HashMap<>();
		for (int i = 0;i < 20 ; i ++) {
			kvs.put("t-" + i , "");
		}
		
		Map<String , Object> ret = redisClusterClientTestService.mget(kvs);
		for (Map.Entry<String, Object> entry : ret.entrySet()) {
			logger.info("mget返回值：{}/{}" , entry.getKey() , entry.getValue());
		}
	}
}