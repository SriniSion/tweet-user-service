package com.tweetapp.config;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.data.redis.config.ConfigureRedisAction;

@Configuration
public class CacheConfig extends CachingConfigurerSupport {

	@Value("${spring.redis.host}")
	private String redisHostName;

	@Value("${spring.redis.port}")
	private int redisPort;
	@Value("${spring.cache.redis.time-to-live}")
	private int redisDataTTL;

	@Bean(name = "redisConnectionFactory")
	public JedisConnectionFactory redisConnectionFactory() {

		RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisHostName, redisPort);
	//	config.setPassword(RedisPassword.of(password));
		JedisClientConfiguration jedisClientConfiguration = null;
//		if (REDIS_USE_SSL) {
//			jedisClientConfiguration = JedisClientConfiguration.builder().usePooling().and().useSsl().build();
//		} else {
			jedisClientConfiguration = JedisClientConfiguration.builder().usePooling().build();
	//	}
		JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(config, jedisClientConfiguration);

		return jedisConnectionFactory;
	}

	@Bean(name = "redisTemplate")
	public RedisTemplate<Object, Object> redisTemplate(
			@Qualifier("redisConnectionFactory") JedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<Object, Object>();
		redisTemplate.setConnectionFactory(redisConnectionFactory);
		redisTemplate.afterPropertiesSet();
		return redisTemplate;
	}

	@Bean(name = "redisCacheManager")
	public RedisCacheManager redisCacheManager(
			@Qualifier("redisConnectionFactory") JedisConnectionFactory redisConnectionFactory) {

		RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
				.disableCachingNullValues().entryTtl(Duration.ofSeconds(redisDataTTL)).serializeValuesWith(
						RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.string()));

		redisCacheConfiguration.usePrefix();

		return RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(redisConnectionFactory).withCacheConfiguration("emailCache", RedisCacheConfiguration.defaultCacheConfig()
				.disableCachingNullValues().entryTtl(Duration.ofSeconds(3600)).serializeValuesWith(
						RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.string())))
				.cacheDefaults(redisCacheConfiguration).build();
	}
	

	@Override
	public CacheErrorHandler errorHandler() {
		return new RedisCacheErrorHandler();
	}

	@Bean
	public static ConfigureRedisAction configureRedisAction() {
		return ConfigureRedisAction.NO_OP;
	}

	public static class RedisCacheErrorHandler implements CacheErrorHandler {
		Logger log = LoggerFactory.getLogger(this.getClass());

		@Override
		public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
			log.info("Unable to get from cache " + cache.getName() + " : " + exception.getMessage());
		}

		@Override
		public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
			log.info("Unable to put into cache " + cache.getName() + " : " + exception.getMessage());
		}

		@Override
		public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
			log.info("Unable to evict from cache " + cache.getName() + " : " + exception.getMessage());
		}

		@Override
		public void handleCacheClearError(RuntimeException exception, Cache cache) {
			log.info("Unable to clean cache " + cache.getName() + " : " + exception.getMessage());
		}
	}

}
