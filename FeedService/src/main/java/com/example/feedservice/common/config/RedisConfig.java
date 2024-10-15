package com.example.feedservice.common.config;

import com.example.feedservice.feed.dto.redis.CursorDto;
import com.example.feedservice.feed.dto.response.ResponseFeedDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    private final ObjectMapper objectMapper;

    /**
     * 기본 레디스 설정
     * */
    @Bean
    public RedisTemplate<String, Object> defaultRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }

    /**
     * 피드 서비스 레디스 
     * */
    @Bean
    public RedisTemplate<CursorDto, ResponseFeedDto> feedListRedisTemplate(RedisConnectionFactory redisConnectionFactory) {

        // Jackson2JsonRedisSerializer 설정 (최신 생성자 사용)   LocalDateTime 직렬화
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);

        RedisTemplate<CursorDto, ResponseFeedDto> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(serializer);
        template.setHashKeySerializer(serializer);
        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);
        template.afterPropertiesSet();
        return template;
    }
}
