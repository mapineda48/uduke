package com.myapp.backend.service.impl;

import com.myapp.backend.service.CacheService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ValkeyCacheServiceImpl implements CacheService {

    private final StringRedisTemplate redisTemplate;

    public ValkeyCacheServiceImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public void hashPut(String key, String hashKey, String value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    @Override
    public String hashGet(String key, String hashKey) {
        Object val = redisTemplate.opsForHash().get(key, hashKey);
        return val != null ? val.toString() : null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, String> hashGetAll(String key) {
        return (Map<String, String>) (Map<?, ?>) redisTemplate.opsForHash().entries(key);
    }

    @Override
    public void hashDelete(String key, String hashKey) {
        redisTemplate.opsForHash().delete(key, hashKey);
    }

    @Override
    public void listRightPush(String key, String value) {
        redisTemplate.opsForList().rightPush(key, value);
    }

    @Override
    public List<String> listRange(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    @Override
    public void listTrim(String key, long start, long end) {
        redisTemplate.opsForList().trim(key, start, end);
    }
}
