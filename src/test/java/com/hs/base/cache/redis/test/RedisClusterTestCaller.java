package com.hs.base.cache.redis.test;

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
		logger.info(redisClusterClientTestService.get("t"));
		logger.info(redisClusterClientTestService.set("t", "32323")? "设置缓存成功" : "缓存设置失败");
		logger.info(redisClusterClientTestService.del("t")? "删除缓存成功" : "删除设置失败");
		logger.info(redisClusterClientTestService.get("t"));
	}
}