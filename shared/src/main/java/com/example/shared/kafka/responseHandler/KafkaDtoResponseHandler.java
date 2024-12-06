package com.example.shared.kafka.responseHandler;

import com.example.shared.dto.UserDto;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Service
public class KafkaDtoResponseHandler {
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