package com.money.airdrop;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.money.airdrop.controller.AirdropRequest;
import com.money.airdrop.controller.AirdropResponse;
import com.money.airdrop.domain.AirdropRecipient;
import com.money.airdrop.repository.MemoryRecipientRepository;
import com.money.airdrop.repository.MemoryEventRepository;
import com.money.airdrop.service.AirdropService;
import java.util.Collection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AirdropServiceTest {

    AirdropService airdropService;
    MemoryEventRepository eventRepository;
    MemoryRecipientRepository recipientRepository;

    @BeforeEach
    void beforeEach() {
        eventRepository = new MemoryEventRepository();
        recipientRepository = new MemoryRecipientRepository();
        airdropService = new AirdropService(eventRepository, recipientRepository);
    }

    @AfterEach
    void afterEach() {
        eventRepository.clear();
        recipientRepository.clear();
    }

    @Test
    void sendMoney() {
        // given
        long userId = 1;
        String roomId = "room 1";
        int recipientCount = 5;
        AirdropRequest payload = new AirdropRequest(1000, recipientCount);

        // when
        airdropService.send(userId, roomId, payload);

        // then
        Collection<AirdropRecipient> recipients =
            (Collection<AirdropRecipient>) recipientRepository.findAll();
        assertThat(recipients.size()).isEqualTo(recipientCount);

        for (AirdropRecipient recipient : recipients) {
            assertThat(recipient.getUserId()).isNull();
            assertThat(recipient.getAmount()).isGreaterThanOrEqualTo(100);
        }
    }

    @Test
    void sendMoneyWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class,
            () -> airdropService.send(1L, "room 1", new AirdropRequest(200000000, 5)));
        assertThrows(IllegalArgumentException.class,
            () -> airdropService.send(1L, "room 1", new AirdropRequest(1000, 0)));
        assertThrows(IllegalArgumentException.class,
            () -> airdropService.send(1L, "room 1", new AirdropRequest(100, 3)));
    }

    @Test
    void receiveMoney() {
        // given
        String roomId = "room 1";
        String token = airdropService.send(1L, roomId, new AirdropRequest(1000, 3));

        // when
        // then
        int receivedAmount = airdropService.receive(2L, roomId, token);
        assertThat(receivedAmount).isGreaterThanOrEqualTo(100).isLessThan(1000);

        receivedAmount = airdropService.receive(3L, roomId, token);
        assertThat(receivedAmount).isGreaterThanOrEqualTo(100).isLessThan(1000);

        receivedAmount = airdropService.receive(4L, roomId, token);
        assertThat(receivedAmount).isGreaterThanOrEqualTo(100).isLessThan(1000);
    }

    @Test
    void receiveMoneyWithInvalidArgs() {
        // given
        String roomId = "room 1";
        String token = airdropService.send(1L, roomId, new AirdropRequest(1000, 3));

        // when
        // then
        assertThrows(IllegalArgumentException.class,
            () -> airdropService.receive(1L, roomId, token));
        assertThrows(IllegalArgumentException.class,
            () -> airdropService.receive(2L, roomId, "invalid"));
        assertThrows(IllegalArgumentException.class,
            () -> airdropService.receive(2L, "invalid", token));

        airdropService.receive(2L, roomId, token);
        assertThrows(IllegalArgumentException.class,
            () -> airdropService.receive(2L, roomId, token));

        airdropService.receive(3L, roomId, token);
        airdropService.receive(4L, roomId, token);
        assertThrows(RuntimeException.class,
            () -> airdropService.receive(5L, roomId, token));
    }

    @Test
    void getStatus() {
        // given
        String roomId = "room 1";
        String token = airdropService.send(1L, roomId, new AirdropRequest(1000, 2));

        // when
        // then
        AirdropResponse status = airdropService.status(1L, roomId, token);
        assertThat(status.getTotalAmount()).isEqualTo(1000);
        assertThat(status.getReceivedAmount()).isEqualTo(0);
        assertThat(status.getRecipients()).isNull();

        airdropService.receive(2L, roomId, token);
        status = airdropService.status(1L, roomId, token);
        assertThat(status.getTotalAmount()).isEqualTo(1000);
        assertThat(status.getReceivedAmount()).isGreaterThan(0).isLessThan(1000);
        assertThat(status.getRecipients().size()).isEqualTo(1);

        airdropService.receive(3L, roomId, token);
        status = airdropService.status(1L, roomId, token);
        assertThat(status.getTotalAmount()).isEqualTo(1000);
        assertThat(status.getReceivedAmount()).isEqualTo(1000);
        assertThat(status.getRecipients().size()).isEqualTo(2);
    }

    @Test
    void getStatusWithInvalidArgs() {
        // given
        String roomId = "room 1";
        String token = airdropService.send(1L, roomId, new AirdropRequest(1000, 2));

        // when
        // then
        assertThrows(RuntimeException.class,
            () -> airdropService.status(2L, roomId, token));
        assertThrows(RuntimeException.class,
            () -> airdropService.status(1L, "invalid", token));
        assertThrows(RuntimeException.class,
            () -> airdropService.status(1L, roomId, "invalid"));
    }
}