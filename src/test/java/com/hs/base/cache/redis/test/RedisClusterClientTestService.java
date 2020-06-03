package com.hs.base.cache.redis.test;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.hs.base.cache.redis.RedisClusterCacheable;

@Component
public class RedisClusterClientTestService {
	@RedisClusterCacheable(group = "test-group" , opt = "get" , keyName = "key")
	public String get(String key) {
		return UUID.randomUUID().toString();
	}
	
	@RedisClusterCacheable(group = "test-group" , opt = "get" , keyName = "key" , valueName = "value" , expire = 10)
	public Boolean set(String key , String value) {
		return true;
	}
	
	@RedisClusterCacheable(group = "test-group" , opt = "del" , keyName = "key")
	public Boolean del(String key) {
		return true;
	}
}
