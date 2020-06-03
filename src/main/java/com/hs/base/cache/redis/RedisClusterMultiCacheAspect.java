package com.hs.base.cache.redis;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class RedisClusterMultiCacheAspect {
	
	private static final String SEPARATOR = ":";
	
	@Autowired
	private RedisClusterRepository redisClusterRepository;
	
	@Pointcut("@annotation(com.hs.base.cache.redis.RedisClusterMultiCacheable)")
    public void cacheMultiPointCut() {
    }
	
	private List<String> buildKeys(Map<String , String> kvs , RedisClusterMultiCacheable cacheable) {
		List<String> keys = new ArrayList<>();
		for (Map.Entry<String, String> entry : kvs.entrySet()) {
			keys.add(cacheable.group() + SEPARATOR + entry.getKey());
		}
		
		return keys;
	}
	
	private Map<String, Object> multiExecute(Map<String , String> kvs , RedisClusterMultiCacheable cacheable) {
		if (RedisClusterCacheCommand.CACHE_MGET.getCmd().equals(cacheable.opt())) {
			return redisClusterRepository.mget(buildKeys(kvs , cacheable));
		}
		
		return Collections.emptyMap();
	}
	
	@SuppressWarnings("unchecked")
	@Around("cacheMultiPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
		MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        RedisClusterMultiCacheable cacheable = method.getAnnotation(RedisClusterMultiCacheable.class);
        
        try {
        	Map<String , String> kvs = null;
        	int index = 0;
        	for (String parameterName : signature.getParameterNames()) {
	        	if (parameterName.equalsIgnoreCase(cacheable.kvMap())) {
	        		kvs = (Map<String, String>) point.getArgs()[index];
	        		break;
	        	}
	        	index ++;
        	}
            
    		return multiExecute(kvs , cacheable);
        } catch (Exception e) {
        	throw e;
        }       
	}
}
