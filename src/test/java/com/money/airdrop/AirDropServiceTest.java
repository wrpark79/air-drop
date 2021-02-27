package com.money.airdrop;

import com.money.airdrop.repository.MemoryReceiverRepository;
import com.money.airdrop.repository.MemorySenderRepository;
import com.money.airdrop.repository.ReceiverRepository;
import com.money.airdrop.repository.SenderRepository;
import com.money.airdrop.service.AirDropService;
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
        // when
        // then
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