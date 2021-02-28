package com.money.airdrop;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.money.airdrop.controller.AirdropRequest;
import com.money.airdrop.controller.AirdropResponse;
import com.money.airdrop.service.AirdropService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class AirdropApplicationTests {

    @Autowired
    AirdropService airdropService;

    @Test
    void singleAirdrop() {
        // given
        int amount = 1000;
        String roomId = "room 0";
        String token = airdropService.send(1L, roomId, new AirdropRequest(amount, 2));

        // when
        // then
        int receivedAmount = airdropService.receive(2L, roomId, token);
        assertThat(receivedAmount).isGreaterThanOrEqualTo(100).isLessThan(amount);

        receivedAmount = airdropService.receive(3L, roomId, token);
        assertThat(receivedAmount).isGreaterThanOrEqualTo(100).isLessThan(amount);

        assertThrows(RuntimeException.class,
            () -> airdropService.receive(4L, roomId, token));

        AirdropResponse status = airdropService.status(1L, roomId, token);
        assertThat(status.getTotalAmount()).isEqualTo(amount);
        assertThat(status.getReceivedAmount()).isEqualTo(amount);
        assertThat(status.getRecipients().size()).isEqualTo(2);
    }

    @Test
    void multipleAirdrops() {
        // given
        int amount1 = 3000;
        String roomId = "room 0";
        String token1 = airdropService.send(1L, roomId, new AirdropRequest(amount1, 2));

        int amount2 = 500000;
        String token2 = airdropService.send(2L, roomId, new AirdropRequest(amount2, 3));

        // when
        // then
        int receivedAmount = airdropService.receive(2L, roomId, token1);
        assertThat(receivedAmount).isGreaterThanOrEqualTo(100).isLessThan(amount1);

        receivedAmount = airdropService.receive(1L, roomId, token2);
        assertThat(receivedAmount).isGreaterThanOrEqualTo(100).isLessThan(amount2);

        receivedAmount = airdropService.receive(3L, roomId, token2);
        assertThat(receivedAmount).isGreaterThanOrEqualTo(100).isLessThan(amount2);

        AirdropResponse status = airdropService.status(1L, roomId, token1);
        assertThat(status.getTotalAmount()).isEqualTo(amount1);
        assertThat(status.getReceivedAmount()).isLessThan(amount1);
        assertThat(status.getRecipients().size()).isEqualTo(1);

        status = airdropService.status(2L, roomId, token2);
        assertThat(status.getTotalAmount()).isEqualTo(amount2);
        assertThat(status.getReceivedAmount()).isLessThan(amount2);
        assertThat(status.getRecipients().size()).isEqualTo(2);
    }
}