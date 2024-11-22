package com.example.financial.transactions.kafka;

import com.example.financial.transactions.dto.UserDto;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Component
public class KafkaResponseHandler {
    private final CountDownLatch latch = new CountDownLatch(1);
    @Getter
    private UserDto userDto;

    public boolean awaitResponseWithTimeout(long timeout, TimeUnit unit) throws InterruptedException {
        return latch.await(timeout, unit);
    }

    public void setUserDto(UserDto userDto) {
        this.userDto = userDto;
        latch.countDown();
    }

}
