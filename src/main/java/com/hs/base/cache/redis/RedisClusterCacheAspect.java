package com.hs.base.cache.redis;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RedisClusterCacheAspect {
	@Autowired
	private RedisClusterRepository redisClusterRepository;
	
	private final static String EMPTY_STRING = "";
	private final static String SEPARATOR = ":";
	
	@Pointcut("@annotation(com.hs.base.cache.redis.RedisClusterCacheable)")
    public void cachePointCut() {
    }
	
	private Object execute(ProceedingJoinPoint point , RedisClusterCacheable cacheable , 
			String keyPrefix , String valuePrefix) throws Throwable {
        if (!EMPTY_STRING.equalsIgnoreCase(keyPrefix) && RedisClusterCacheCommand.CACHE_INCR.getCmd().equalsIgnoreCase(cacheable.opt())) {
        	redisClusterRepository.increase(cacheable.group() + SEPARATOR + keyPrefix , cacheable.expire());
        	
        	return point.proceed();
        } else if (!EMPTY_STRING.equalsIgnoreCase(keyPrefix) && RedisClusterCacheCommand.CACHE_DECR.getCmd().equalsIgnoreCase(cacheable.opt())) {
        	redisClusterRepository.decrease(cacheable.group() + SEPARATOR + keyPrefix , cacheable.expire());
        	
        	return point.proceed();
        }  else if (!EMPTY_STRING.equalsIgnoreCase(keyPrefix) && RedisClusterCacheCommand.CACHE_DEL.getCmd().equalsIgnoreCase(cacheable.opt())) {
        	redisClusterRepository.del(cacheable.group() + SEPARATOR + keyPrefix);
        	
        	return point.proceed();
        } else if (!EMPTY_STRING.equalsIgnoreCase(keyPrefix) && RedisClusterCacheCommand.CACHE_GET.getCmd().equalsIgnoreCase(cacheable.opt())) {
        	Object cacheableData = redisClusterRepository.get(cacheable.group() + SEPARATOR + keyPrefix);
        	if (cacheableData != null) {
        		return cacheableData;
        	} else {
        		Object returnValue = point.proceed();
        		if (returnValue != null) {
        			redisClusterRepository.set(cacheable.group() + SEPARATOR + keyPrefix , returnValue.toString() , cacheable.expire());
        		}
        		
        		return returnValue;
        	}
        } else if (!EMPTY_STRING.equalsIgnoreCase(keyPrefix) && RedisClusterCacheCommand.CACHE_SET.getCmd().equalsIgnoreCase(cacheable.opt())) {
        	redisClusterRepository.set(cacheable.group() + SEPARATOR + keyPrefix , valuePrefix , cacheable.expire());
        	
        	return point.proceed();
        } else {
        	return point.proceed();
        }		
	}
	
	@Around("cachePointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
		MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();       
        RedisClusterCacheable cacheable = method.getAnnotation(RedisClusterCacheable.class);
        
        try {        	
        	int index = 0;
        	String keyPrefix = "";
        	String valuePrefix = null;
        	for (String parameterName : signature.getParameterNames()) {
	        	if (parameterName.equalsIgnoreCase(cacheable.keyName())) {
	        		keyPrefix = point.getArgs()[index].toString();
	        	} else if (parameterName.equalsIgnoreCase(cacheable.valueName())) {
	        		valuePrefix = point.getArgs()[index].toString();
	        	}
	        	
	        	index ++;
        	}

        	return execute(point , cacheable , keyPrefix , valuePrefix);
        } catch (Exception e) {
        	throw e;
        }
	}
}
