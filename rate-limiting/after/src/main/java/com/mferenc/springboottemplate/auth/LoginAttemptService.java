package com.mferenc.springboottemplate.auth;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS_PER_IP = 5;
    private static final int MAX_ATTEMPTS_PER_USERNAME = 10;
    private static final long LOCK_TIME_MS = TimeUnit.MINUTES.toMillis(15);

    private final ConcurrentHashMap<String, AttemptsInfo> ipAttempts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AttemptsInfo> usernameAttempts = new ConcurrentHashMap<>();

    private record AttemptsInfo(AtomicLong count, AtomicLong lastReset) {
    }

    public void loginFailed(String ip, String username) {
        long now = System.currentTimeMillis();

        ipAttempts.compute(ip, (k, bucket) -> {
            if (bucket == null) return new AttemptsInfo(new AtomicLong(1), new AtomicLong(now));
            resetIfExpired(bucket, now);
            bucket.count().incrementAndGet();
            return bucket;
        });

        usernameAttempts.compute(username, (k, bucket) -> {
            if (bucket == null) return new AttemptsInfo(new AtomicLong(1), new AtomicLong(now));
            resetIfExpired(bucket, now);
            bucket.count().incrementAndGet();
            return bucket;
        });
    }

    public void loginSucceeded(String ip, String username) {
        ipAttempts.remove(ip);
        usernameAttempts.remove(username);
    }

    public boolean isIpBlocked(String ip) {
        return isBlocked(ipAttempts.get(ip), MAX_ATTEMPTS_PER_IP);
    }

    public boolean isUsernameBlocked(String username) {
        return isBlocked(usernameAttempts.get(username), MAX_ATTEMPTS_PER_USERNAME);
    }

    private void resetIfExpired(AttemptsInfo bucket, long currentTime) {
        long lastReset = bucket.lastReset().get();
        if (currentTime - lastReset > LOCK_TIME_MS) {
            if (bucket.lastReset().compareAndSet(lastReset, currentTime)) {
                bucket.count().set(0);
            }
        }
    }

    private boolean isBlocked(AttemptsInfo bucket, int maxAttempts) {
        if (bucket == null) return false;

        long now = System.currentTimeMillis();
        resetIfExpired(bucket, now);
        return bucket.count().get() >= maxAttempts;
    }
}