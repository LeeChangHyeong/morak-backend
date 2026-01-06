package org.brokong.morakbackend.global.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    // 저장
    public void setValue(String key, String value, Duration duration) {
        redisTemplate.opsForValue().set(key, value, duration);
    }

    // 조회
    public String getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    // 삭제
    public void deleteValue(String key) {
        redisTemplate.delete(key);
    }

    // 존재 확인
    public boolean isExists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    // Set에 추가
    public void addToSet(String key, String value) {
        redisTemplate.opsForSet().add(key, value);
    }

    // Set에서 제거
    public void removeFromSet(String key, String value) {
        redisTemplate.opsForSet().remove(key, value);
    }

    // Set의 모든 멤버 조회
    public Set<String> getSetMembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    // Set의 크기 조회
    public Long getSetSize(String key) {
        return redisTemplate.opsForSet().size(key);
    }
}
