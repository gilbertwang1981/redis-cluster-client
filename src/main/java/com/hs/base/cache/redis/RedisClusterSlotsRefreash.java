package com.hs.base.cache.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Configuration
@EnableScheduling
public class RedisClusterSlotsRefreash {
	@Autowired
	private RedisClusterPipeline redisClusterPipeline;
	
	@Scheduled(cron = "0/15 * * * * ?")
	private void scheduleRefreashSlots() {
		try {
			redisClusterPipeline.refreshCluster();
		} catch (Exception e) {
		}
	}
}
