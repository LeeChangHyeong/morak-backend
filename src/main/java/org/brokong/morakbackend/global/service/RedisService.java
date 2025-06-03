package org.brokong.morakbackend.global.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    // 저장
    public void setValues(String key, String value, Duration duration) {
        redisTemplate.opsForValue().set(key, value, duration);
    }

    // 조회
    public String getData(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    // 삭제
    public void deleteData(String key) {
        redisTemplate.delete(key);
    }

    // 존재 확인
    public boolean isExists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}