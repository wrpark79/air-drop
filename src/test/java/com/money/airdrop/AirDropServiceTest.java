package com.money.airdrop;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.money.airdrop.controller.AirDrop;
import com.money.airdrop.controller.AirDropStatus;
import com.money.airdrop.domain.AirDropReceiver;
import com.money.airdrop.repository.MemoryReceiverRepository;
import com.money.airdrop.repository.MemorySenderRepository;
import com.money.airdrop.service.AirDropService;
import java.util.Collection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AirDropServiceTest {

    AirDropService airDropService;
    MemorySenderRepository senderRepository;
    MemoryReceiverRepository receiverRepository;

    @BeforeEach
    void beforeEach() {
        senderRepository = new MemorySenderRepository();
        receiverRepository = new MemoryReceiverRepository();
        airDropService = new AirDropService(senderRepository, receiverRepository);
    }

    @AfterEach
    void afterEach() {
        senderRepository.clear();
        receiverRepository.clear();
    }

    @Test
    void sendMoney() {
        // given
        long userId = 1;
        String roomId = "room 1";
        int receiverCount = 5;
        AirDrop payload = new AirDrop(1000, receiverCount);

        // when
        airDropService.send(userId, roomId, payload);

        // then
        Collection<AirDropReceiver> receivers =
            (Collection<AirDropReceiver>) receiverRepository.findAll();
        assertThat(receivers.size()).isEqualTo(receiverCount);

        for (AirDropReceiver receiver : receivers) {
            assertThat(receiver.getUserId()).isNull();
            assertThat(receiver.getAmount()).isGreaterThanOrEqualTo(100);
        }
    }

    @Test
    void sendMoneyWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class,
            () -> airDropService.send(1L, "room 1", new AirDrop(200000000, 5)));
        assertThrows(IllegalArgumentException.class,
            () -> airDropService.send(1L, "room 1", new AirDrop(1000, 0)));
        assertThrows(IllegalArgumentException.class,
            () -> airDropService.send(1L, "room 1", new AirDrop(100, 3)));
    }

    @Test
    void receiveMoney() {
        // given
        String roomId = "room 1";
        String token = airDropService.send(1L, roomId, new AirDrop(1000, 3));

        // when
        // then
        int receivedMoney = airDropService.receive(2L, roomId, token);
        assertThat(receivedMoney).isGreaterThanOrEqualTo(100);

        receivedMoney = airDropService.receive(3L, roomId, token);
        assertThat(receivedMoney).isGreaterThanOrEqualTo(100);

        receivedMoney = airDropService.receive(4L, roomId, token);
        assertThat(receivedMoney).isGreaterThanOrEqualTo(100);
    }

    @Test
    void receiveMoneyWithInvalidArgs() {
        // given
        String roomId = "room 1";
        String token = airDropService.send(1L, roomId, new AirDrop(1000, 3));

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
        String token = airDropService.send(1L, roomId, new AirDrop(1000, 2));

        // when
        // then
        AirDropStatus status = airDropService.status(1L, roomId, token);
        assertThat(status.getTotalAmount()).isEqualTo(1000);
        assertThat(status.getReceivedAmount()).isEqualTo(0);
        assertThat(status.getReceivers()).isNull();

        airDropService.receive(2L, roomId, token);
        status = airDropService.status(1L, roomId, token);
        assertThat(status.getTotalAmount()).isEqualTo(1000);
        assertThat(status.getReceivedAmount()).isGreaterThan(0).isLessThan(1000);
        assertThat(status.getReceivers().size()).isEqualTo(1);

        airDropService.receive(3L, roomId, token);
        status = airDropService.status(1L, roomId, token);
        assertThat(status.getTotalAmount()).isEqualTo(1000);
        assertThat(status.getReceivedAmount()).isEqualTo(1000);
        assertThat(status.getReceivers().size()).isEqualTo(2);
    }

    @Test
    void getStatusWithInvalidArgs() {
        // given
        String roomId = "room 1";
        String token = airDropService.send(1L, roomId, new AirDrop(1000, 2));

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