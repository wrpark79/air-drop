package com.money.airdrop;

import static org.assertj.core.api.Assertions.assertThat;

import com.money.airdrop.controller.AirDrop;
import com.money.airdrop.domain.AirDropReceiver;
import com.money.airdrop.repository.MemoryReceiverRepository;
import com.money.airdrop.repository.MemorySenderRepository;
import com.money.airdrop.repository.ReceiverRepository;
import com.money.airdrop.repository.SenderRepository;
import com.money.airdrop.service.AirDropService;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AirDropServiceTest {

    AirDropService airDropService;
    SenderRepository senderRepository;
    ReceiverRepository receiverRepository;

    @BeforeEach
    void beforeEach() {
        senderRepository = new MemorySenderRepository();
        receiverRepository = new MemoryReceiverRepository();
        airDropService = new AirDropService(senderRepository, receiverRepository);
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
            assertThat(receiver.getReceiverId()).isNull();
            assertThat(receiver.getAmount()).isGreaterThanOrEqualTo(100);
        }
    }

    @Test
    void receiveMoney() {
        // given
        // when
        // then
    }

    @Test
    void getStatus() {
        // given
        // when
        // then
    }
}