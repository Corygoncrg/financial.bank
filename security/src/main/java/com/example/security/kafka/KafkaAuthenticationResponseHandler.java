package com.example.security.kafka;

import com.example.shared.dto.UserAuthenticationDto;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
@Component
public class KafkaAuthenticationResponseHandler {
    private final CountDownLatch latch = new CountDownLatch(1);
    @Getter
    private UserAuthenticationDto authenticationDto;
    public boolean awaitResponseWithTimeout(long timeout, TimeUnit unit) throws InterruptedException {
        return latch.await(timeout, unit);
    }

    public void setAuthDto(UserAuthenticationDto authenticationDto) {
        this.authenticationDto = authenticationDto;
        latch.countDown();
    }

}
