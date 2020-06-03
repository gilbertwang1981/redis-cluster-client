package com.hs.base.cache.redis;

import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

public class RedisClusterPipeline  extends JedisCluster {
    public RedisClusterPipeline(Set<HostAndPort> jedisClusterNode, int connectionTimeout, int soTimeout, int maxAttempts, String password, final GenericObjectPoolConfig poolConfig) {
        super(jedisClusterNode,connectionTimeout, soTimeout, maxAttempts, password, poolConfig);
        super.connectionHandler = new RedisSlotAdvancedConnectionHandler(jedisClusterNode, poolConfig,
                connectionTimeout, soTimeout ,password);
    }

    public RedisSlotAdvancedConnectionHandler getConnectionHandler() {
        return (RedisSlotAdvancedConnectionHandler)this.connectionHandler;
    }

    public void refreshCluster() {
        connectionHandler.renewSlotCache();
    }
}
