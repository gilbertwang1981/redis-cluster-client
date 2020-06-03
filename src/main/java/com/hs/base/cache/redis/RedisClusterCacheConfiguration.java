package com.hs.base.cache.redis;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

@Configuration
public class RedisClusterCacheConfiguration extends CachingConfigurerSupport {
    @Value("${jedis-cluster.nodes:127.0.0.1:6379}")
    private String nodes;
    @Value("${jedis-cluster.connect-timeout:500}")
    private int connectionTimeout;
    @Value("${jedis-cluster.so-timeout:500}")
    private int soTimeout;
    @Value("${jedis-cluster.max-attempts:10}")
    private int maxAttempts;
    @Value("${jedis-cluster.pool.maxTotal:800}")
    private int maxTotal;
    @Value("${jedis-cluster.pool.minIdle:50}")
    private int minIdle;
    @Value("${jedis-cluster.pool.maxIdle:200}")
    private int maxIdle;
    @Value("${jedis-cluster.pool.maxWait:3000}")
    private int maxWaitMillis;
    
    private Set<HostAndPort> buildHostAndPort(String nodesList) {
    	Set<HostAndPort> nodes = new HashSet<HostAndPort>();
    	StringTokenizer st = new StringTokenizer(nodesList , ";");
    	while (st.hasMoreElements()) {
    		String node = st.nextToken();
    		if (node != null) {
    			StringTokenizer stn = new StringTokenizer(node , ":");
    			if (stn != null) {
    				nodes.add(new HostAndPort(stn.nextToken() , Integer.parseInt(stn.nextToken())));
    			}
    		}
    	}
    	
    	return nodes;
    }

    @Bean
    public JedisCluster jedisCluster() {
        JedisCluster jedisCluster = null;
        if (!nodes.isEmpty()){
            GenericObjectPoolConfig pool = new GenericObjectPoolConfig();
            pool.setMaxTotal(maxTotal);
            pool.setMinIdle(minIdle);
            pool.setMaxIdle(maxIdle);
            pool.setMaxWaitMillis(maxWaitMillis);
            jedisCluster = new JedisCluster(buildHostAndPort(nodes) , connectionTimeout, soTimeout, maxAttempts, null, pool);
        }
        
        return jedisCluster;
    }
    
    @Bean
    public RedisClusterPipeline jedisPipeline() {
    	RedisClusterPipeline pipeline = null;
    	if (!nodes.isEmpty()){
            GenericObjectPoolConfig pool = new GenericObjectPoolConfig();
            pool.setMaxTotal(maxTotal);
            pool.setMinIdle(minIdle);
            pool.setMaxIdle(maxIdle);
            pool.setMaxWaitMillis(maxWaitMillis);
            pipeline = new RedisClusterPipeline(buildHostAndPort(nodes) , connectionTimeout, soTimeout, maxAttempts, null, pool);
        }
    	
    	return pipeline;
    }
}
