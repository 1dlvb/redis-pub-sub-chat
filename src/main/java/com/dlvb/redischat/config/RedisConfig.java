package com.dlvb.redischat.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

/**
 * Configuration class that sets up the Redis connection factory for the application.
 * <ul>
 *     <li>spring.data.redis.host - Redis server host</li>
 *     <li>spring.data.redis.port - Redis server port</li>
 * </ul>
 *
 * @see LettuceConnectionFactory
 * @author Matushkin Anton
 */
@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private String redisPort;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(redisHost, Integer.parseInt(redisPort));
    }

}
