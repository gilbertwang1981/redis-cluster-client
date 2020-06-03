package com.hs.base.cache.redis.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages="com.hs")
public class RedisClusterClientTest {
	public static void main(String [] args) {
		SpringApplication.run(RedisClusterClientTest.class , args);
	}
}
