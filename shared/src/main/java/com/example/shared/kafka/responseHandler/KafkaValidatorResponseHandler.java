package com.example.shared.kafka.responseHandler;

import com.example.shared.dto.UserValidatorDto;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Component
public class KafkaValidatorResponseHandler {
    private final CountDownLatch latch = new CountDownLatch(1);
    @Getter
    private UserValidatorDto userValidatorDto;

    public boolean awaitResponseWithTimeout(long timeout, TimeUnit unit) throws InterruptedException {
        return latch.await(timeout, unit);
    }

    public void setValidatorDto(UserValidatorDto userValidatorDto) {
        this.userValidatorDto = userValidatorDto;
        latch.countDown();
    }
}
