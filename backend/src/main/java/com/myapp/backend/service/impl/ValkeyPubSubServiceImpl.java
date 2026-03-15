package com.myapp.backend.service.impl;

import com.myapp.backend.service.PubSubService;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

@Service
public class ValkeyPubSubServiceImpl implements PubSubService {

    private final StringRedisTemplate redisTemplate;
    private final RedisMessageListenerContainer listenerContainer;
    private final List<MessageListener> activeListeners = new CopyOnWriteArrayList<>();

    public ValkeyPubSubServiceImpl(StringRedisTemplate redisTemplate,
                                   RedisMessageListenerContainer listenerContainer) {
        this.redisTemplate = redisTemplate;
        this.listenerContainer = listenerContainer;
    }

    @Override
    public void publish(String channel, String message) {
        redisTemplate.convertAndSend(channel, message);
    }

    @Override
    public void subscribe(String channel, Consumer<String> listener) {
        MessageListener messageListener = (Message message, byte[] pattern) -> {
            String body = new String(message.getBody(), StandardCharsets.UTF_8);
            listener.accept(body);
        };
        activeListeners.add(messageListener);
        listenerContainer.addMessageListener(messageListener, new ChannelTopic(channel));
    }

    @Override
    public void unsubscribeAll() {
        for (MessageListener listener : activeListeners) {
            listenerContainer.removeMessageListener(listener);
        }
        activeListeners.clear();
    }
}
