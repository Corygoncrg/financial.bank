package com.example.financial.transactions.config;

import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Component
public class KafkaResponseHandler {
    private CountDownLatch latch = new CountDownLatch(1);
    private Long userId;

    public void awaitResponse() throws InterruptedException {
        latch.await();
    }

    public boolean awaitResponseWithTimeout(long timeout, TimeUnit unit) throws InterruptedException {
        return latch.await(timeout, unit);
    }

    public void setUserId(Long userId) {
        this.userId = userId;
        latch.countDown(); // Release the latch
    }

    public Long getUserId() {
        return userId;
    }

    public void resetLatch() {
        latch = new CountDownLatch(1); // Reset for reuse
    }
}
