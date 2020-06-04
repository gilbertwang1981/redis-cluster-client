package com.hs.base.cache.redis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import redis.clients.util.JedisClusterCRC16;

@Component
public class RedisClusterRepository {
	private static Logger logger = LoggerFactory.getLogger(RedisClusterRepository.class);
	
	@Autowired
	private JedisCluster jedisCluster;
	
	@Autowired
	private RedisClusterPipeline redisClusterPipeline;
	
	public Boolean del(String key) {
		try {
			jedisCluster.del(key);
			
			return Boolean.TRUE;
		} catch (Exception e) {
			return Boolean.FALSE;
		}
	}
	
	public Boolean set(String key , String value , Long exp) {
		try {
			jedisCluster.psetex(key, exp, value);
			
			return Boolean.TRUE;
		} catch (Exception e) {
			logger.error("set失败, {} {} {} {}" , e.getMessage() , key , value , exp);
			
			return Boolean.FALSE;
		}
	}
	
	public Boolean increase(String key , Long exp) {
		try {
			jedisCluster.incr(key);
			
			if (exp != null) {
				jedisCluster.expire(key, exp.intValue());
			}
			
			return Boolean.TRUE;
		} catch (Exception e) {
			logger.error("incr失败, {} {} {}" , e.getMessage() , key , exp);
			
			return Boolean.FALSE;
		}
	}
	
	public Boolean decrease(String key , Long exp) {
		try {
			jedisCluster.decr(key);
			
			if (exp != null) {
				jedisCluster.expire(key, exp.intValue());
			}
			
			return Boolean.TRUE;
		} catch (Exception e) {
			logger.error("decr失败, {} {} {}" , e.getMessage() , key , exp);
			
			return Boolean.FALSE;
		}
	}
	
	public Object get(String key) {
		try {
			return jedisCluster.get(key);
		} catch (Exception e) {
			logger.error("get失败, {} {}" , e.getMessage() , key );
			
			return null;
		}
	}
	
	private Map<JedisPool , List<String>> buildKeys4Pipeline(List<String> keys) {
		Map<JedisPool , List<String>> aggregate = new HashMap<>();
		
		RedisSlotAdvancedConnectionHandler jedisSlotAdvancedConnectionHandler = redisClusterPipeline.getConnectionHandler();
		for (String key : keys) {
			JedisPool jedisPool = jedisSlotAdvancedConnectionHandler.getJedisPoolFromSlot(JedisClusterCRC16.getSlot(key));
			if (jedisPool != null) {
				List<String> aggregateKeys = aggregate.get(jedisPool);
				if (aggregateKeys == null) {
					aggregateKeys = new ArrayList<>();
					aggregateKeys.add(key);
					aggregate.put(jedisPool,  aggregateKeys);
				} else {
					aggregateKeys.add(key);
				}
			}
		}
		
		return aggregate;
	}
	
	public Map<String, Object> mget(List<String> keys) {
		try {
			Map<String, Object> resp = new HashMap<>();
			Map<JedisPool , List<String>> aggregate = buildKeys4Pipeline(keys);
			for (Map.Entry<JedisPool , List<String>> entry : aggregate.entrySet()) {
				Jedis jedis = entry.getKey().getResource();
				if (jedis == null) {
					return Collections.emptyMap();
				}
				
				Pipeline pl = jedis.pipelined();
				if (pl == null) {
					return Collections.emptyMap();
				}
				
				for (String key : entry.getValue()) {					
					pl.get(key);					
				}
				
				List<Object> results = pl.syncAndReturnAll();
				for (int index = 0; index < results.size(); index ++) {
					Object result = results.get(index);
					if (result != null) {
						resp.put(entry.getValue().get(index) , result);
					}
				}
				
				jedis.close();
			}
			
			return resp;
		} catch (Exception e) {
			return Collections.emptyMap();
		}
	}
	
	public Map<String, Object> mset(List<String> keys) {
		return null;
	}
	
	public Map<String, Object> mdel(List<String> keys) {
		return null;
	}
}
