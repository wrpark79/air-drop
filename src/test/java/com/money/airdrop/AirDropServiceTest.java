package com.money.airdrop;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.money.airdrop.controller.AirDropRequest;
import com.money.airdrop.controller.AirDropResponse;
import com.money.airdrop.domain.AirDropRecipient;
import com.money.airdrop.repository.MemoryRecipientRepository;
import com.money.airdrop.repository.MemoryEventRepository;
import com.money.airdrop.service.AirDropService;
import java.util.Collection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AirDropServiceTest {

    AirDropService airDropService;
    MemoryEventRepository eventRepository;
    MemoryRecipientRepository recipientRepository;

    @BeforeEach
    void beforeEach() {
        eventRepository = new MemoryEventRepository();
        recipientRepository = new MemoryRecipientRepository();
        airDropService = new AirDropService(eventRepository, recipientRepository);
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
        AirDropRequest payload = new AirDropRequest(1000, recipientCount);

        // when
        airDropService.send(userId, roomId, payload);

        // then
        Collection<AirDropRecipient> recipients =
            (Collection<AirDropRecipient>) recipientRepository.findAll();
        assertThat(recipients.size()).isEqualTo(recipientCount);

        for (AirDropRecipient recipient : recipients) {
            assertThat(recipient.getUserId()).isNull();
            assertThat(recipient.getAmount()).isGreaterThanOrEqualTo(100);
        }
    }

    @Test
    void sendMoneyWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class,
            () -> airDropService.send(1L, "room 1", new AirDropRequest(200000000, 5)));
        assertThrows(IllegalArgumentException.class,
            () -> airDropService.send(1L, "room 1", new AirDropRequest(1000, 0)));
        assertThrows(IllegalArgumentException.class,
            () -> airDropService.send(1L, "room 1", new AirDropRequest(100, 3)));
    }

    @Test
    void receiveMoney() {
        // given
        String roomId = "room 1";
        String token = airDropService.send(1L, roomId, new AirDropRequest(1000, 3));

        // when
        // then
        int receivedAmount = airDropService.receive(2L, roomId, token);
        assertThat(receivedAmount).isGreaterThanOrEqualTo(100);

        receivedAmount = airDropService.receive(3L, roomId, token);
        assertThat(receivedAmount).isGreaterThanOrEqualTo(100);

        receivedAmount = airDropService.receive(4L, roomId, token);
        assertThat(receivedAmount).isGreaterThanOrEqualTo(100);
    }

    @Test
    void receiveMoneyWithInvalidArgs() {
        // given
        String roomId = "room 1";
        String token = airDropService.send(1L, roomId, new AirDropRequest(1000, 3));

        // when
        // then
        assertThrows(IllegalArgumentException.class,
            () -> airDropService.receive(1L, roomId, token));
        assertThrows(IllegalArgumentException.class,
            () -> airDropService.receive(2L, roomId, "invalid"));
        assertThrows(IllegalArgumentException.class,
            () -> airDropService.receive(2L, "invalid", token));

        airDropService.receive(2L, roomId, token);
        assertThrows(IllegalArgumentException.class,
            () -> airDropService.receive(2L, roomId, token));

        airDropService.receive(3L, roomId, token);
        airDropService.receive(4L, roomId, token);
        assertThrows(RuntimeException.class,
            () -> airDropService.receive(5L, roomId, token));
    }

    @Test
    void getStatus() {
        // given
        String roomId = "room 1";
        String token = airDropService.send(1L, roomId, new AirDropRequest(1000, 2));

        // when
        // then
        AirDropResponse status = airDropService.status(1L, roomId, token);
        assertThat(status.getTotalAmount()).isEqualTo(1000);
        assertThat(status.getReceivedAmount()).isEqualTo(0);
        assertThat(status.getRecipients()).isNull();

        airDropService.receive(2L, roomId, token);
        status = airDropService.status(1L, roomId, token);
        assertThat(status.getTotalAmount()).isEqualTo(1000);
        assertThat(status.getReceivedAmount()).isGreaterThan(0).isLessThan(1000);
        assertThat(status.getRecipients().size()).isEqualTo(1);

        airDropService.receive(3L, roomId, token);
        status = airDropService.status(1L, roomId, token);
        assertThat(status.getTotalAmount()).isEqualTo(1000);
        assertThat(status.getReceivedAmount()).isEqualTo(1000);
        assertThat(status.getRecipients().size()).isEqualTo(2);
    }

    @Test
    void getStatusWithInvalidArgs() {
        // given
        String roomId = "room 1";
        String token = airDropService.send(1L, roomId, new AirDropRequest(1000, 2));

        // when
        // then
        assertThrows(RuntimeException.class,
            () -> airDropService.status(2L, roomId, token));
        assertThrows(RuntimeException.class,
            () -> airDropService.status(1L, "invalid", token));
        assertThrows(RuntimeException.class,
            () -> airDropService.status(1L, roomId, "invalid"));
    }
}