package com.backend.abhishek.uber.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisConfigSetup implements InitializingBean {

    private final RedisConnectionFactory connectionFactory;

    @Override
    public void afterPropertiesSet() {
        var connection = connectionFactory.getConnection();
        connection.setConfig("notify-keyspace-events", "Ex");
    }
}

