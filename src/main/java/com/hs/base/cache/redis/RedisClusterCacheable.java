package com.hs.base.cache.redis;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedisClusterCacheable {
	/**
	 * 缓存组名字，字符串
	 * @return
	 */
	String group();
	
	/**
	 * 操作类型，字符串，包括:
	 * get: 获取缓存key，如果key不存在，执行方法，将方法返回值存入缓存
	 * increase: 自增key
	 * decrease: 自减key
	 * del: 删除key
	 * set: 设置缓存key的值，存在就覆盖
	 * @return
	 */
	String opt();
	
	/**
	 * 缓存key变量的名字，字符串
	 * @return
	 */
	String keyName();
	
	/**
	 * 缓存value变量的名字，字符串
	 * @return
	 */
	String valueName() default "";
	
	/**
	 * key超时时间，单位：毫秒
	 * @return
	 */
	long expire() default 100L;
}
