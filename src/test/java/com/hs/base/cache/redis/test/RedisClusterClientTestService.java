package com.hs.base.cache.redis.test;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.hs.base.cache.redis.RedisClusterCacheable;
import com.hs.base.cache.redis.RedisClusterMultiCacheable;

@Component
public class RedisClusterClientTestService {
	@RedisClusterCacheable(group = "test-group" , opt = "get" , keyName = "key" , expire = 10000)
	public String get(String key) {
		return UUID.randomUUID().toString();
	}
	
	@RedisClusterCacheable(group = "test-group" , opt = "set" , keyName = "key" , valueName = "value" , expire = 10000)
	public String set(String key , String value) {
		return UUID.randomUUID().toString();
	}
	
	@RedisClusterCacheable(group = "test-group" , opt = "del" , keyName = "key")
	public Boolean del(String key) {
		return true;
	}
	
	@RedisClusterMultiCacheable(group = "test-group" , opt = "mget" , kvMap = "kvs")
	public Map<String , Object> mget(Map<String , Object> kvs) {
		return Collections.emptyMap();
	}
}
