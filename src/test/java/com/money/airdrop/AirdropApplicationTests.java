package com.money.airdrop;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.money.airdrop.controller.AirDropRequest;
import com.money.airdrop.controller.AirDropResponse;
import com.money.airdrop.service.AirDropService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class AirdropApplicationTests {

    @Autowired
    AirDropService airDropService;

    @Test
    void singleAirDrop() {
        // given
        int amount = 1000;
        String roomId = "room 0";
        String token = airDropService.send(1L, roomId, new AirDropRequest(amount, 2));

        // when
        // then
        int receivedAmount = airDropService.receive(2L, roomId, token);
        assertThat(receivedAmount).isGreaterThanOrEqualTo(100).isLessThan(amount);

        receivedAmount = airDropService.receive(3L, roomId, token);
        assertThat(receivedAmount).isGreaterThanOrEqualTo(100).isLessThan(amount);

        assertThrows(RuntimeException.class,
            () -> airDropService.receive(4L, roomId, token));

        AirDropResponse status = airDropService.status(1L, roomId, token);
        assertThat(status.getTotalAmount()).isEqualTo(amount);
        assertThat(status.getReceivedAmount()).isEqualTo(amount);
        assertThat(status.getRecipients().size()).isEqualTo(2);
    }

    @Test
    void multipleAirDrops() {
        // given
        int amount1 = 3000;
        String roomId = "room 0";
        String token1 = airDropService.send(1L, roomId, new AirDropRequest(amount1, 2));

        int amount2 = 500000;
        String token2 = airDropService.send(2L, roomId, new AirDropRequest(amount2, 3));

        // when
        // then
        int receivedAmount = airDropService.receive(2L, roomId, token1);
        assertThat(receivedAmount).isGreaterThanOrEqualTo(100).isLessThan(amount1);

        receivedAmount = airDropService.receive(1L, roomId, token2);
        assertThat(receivedAmount).isGreaterThanOrEqualTo(100).isLessThan(amount2);

        receivedAmount = airDropService.receive(3L, roomId, token2);
        assertThat(receivedAmount).isGreaterThanOrEqualTo(100).isLessThan(amount2);

        AirDropResponse status = airDropService.status(1L, roomId, token1);
        assertThat(status.getTotalAmount()).isEqualTo(amount1);
        assertThat(status.getReceivedAmount()).isLessThan(amount1);
        assertThat(status.getRecipients().size()).isEqualTo(1);

        status = airDropService.status(2L, roomId, token2);
        assertThat(status.getTotalAmount()).isEqualTo(amount2);
        assertThat(status.getReceivedAmount()).isLessThan(amount2);
        assertThat(status.getRecipients().size()).isEqualTo(2);
    }
}