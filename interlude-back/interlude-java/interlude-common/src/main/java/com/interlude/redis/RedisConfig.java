package com.interlude.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfig<V> {
    // 定义一个名为redisTemplate的Bean，用于操作Redis。它使用RedisConnectionFactory来连接Redis服务器
    @Bean("redisTemplate")
    public RedisTemplate<String, V> redisTemplate(RedisConnectionFactory factory) {
        // 创建RedisTemplate实例，指定key的类型为String，value的类型为泛型V
        RedisTemplate<String, V> template = new RedisTemplate<>();
        // 设置连接工厂r
        template.setConnectionFactory(factory);
        // 设置key的序列化方式  这样key会被以UTF-8字符串的形式处理
        template.setKeySerializer(RedisSerializer.string());
        // 设置value的序列化方式        将对象序列化为JSON字符串存储
        template.setValueSerializer(RedisSerializer.json());
        // 设置hash的key的序列化方式
        template.setHashKeySerializer(RedisSerializer.string());
        // 设置hash的value的序列化方式
        template.setHashValueSerializer(RedisSerializer.json());
        // 初始化模板
        template.afterPropertiesSet();
        return template;
    }

    // 这个Bean用于处理Redis的发布/订阅消息
    @Bean
    public RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        return container;
    }
}