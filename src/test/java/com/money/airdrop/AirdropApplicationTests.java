package com.money.airdrop;

import com.money.airdrop.service.AirDropService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional(isolation = Isolation.READ_COMMITTED)
class AirdropApplicationTests {

    @Autowired
    AirDropService airDropService;

    @Test
    void send() {
        // given
        // when
        // then
    }
}