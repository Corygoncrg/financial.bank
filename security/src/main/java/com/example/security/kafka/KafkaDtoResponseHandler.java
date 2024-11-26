package com.example.security.kafka;

import com.example.security.dto.UserDto;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Component
public class KafkaDtoResponseHandler {
    private final CountDownLatch latch = new CountDownLatch(1);
    @Getter
    private UserDto userDto;

    public boolean awaitResponseWithTimeout(long timeout, TimeUnit unit) throws InterruptedException {
        return latch.await(timeout, unit);
    }

    public void setUserDto(com.example.security.dto.UserDto userDto) {
        this.userDto = userDto;
        latch.countDown();
    }

}