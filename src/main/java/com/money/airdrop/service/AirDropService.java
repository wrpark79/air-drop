package com.money.airdrop.service;

import com.money.airdrop.controller.AirDrop;
import com.money.airdrop.domain.AirDropReceiver;
import com.money.airdrop.domain.AirDropSender;
import com.money.airdrop.repository.ReceiverRepository;
import com.money.airdrop.repository.SenderRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

@Service
public class AirDropService {

    private static final int MAX_AMOUNT = 100000000;
    private static final int MIN_AMOUNT = 100;

    private final SenderRepository senderRepository;
    private final ReceiverRepository receiverRepository;

    private final Random random = new Random();

    public AirDropService(SenderRepository senderRepository,
        ReceiverRepository receiverRepository) {
        this.senderRepository = senderRepository;
        this.receiverRepository = receiverRepository;
    }

    public String send(Long userId, String roomId, AirDrop payload) {
        int totalAmount = payload.getAmount();
        if (totalAmount > MAX_AMOUNT) {
            throw new IllegalArgumentException("뿌릴 금액은 " + MAX_AMOUNT + "원 이하여야 합니다");
        }

        int receiverCount = payload.getReceiverCount();
        if (receiverCount <= 0) {
            throw new IllegalArgumentException("받을 사람은 최소한 1명 이상이어야 합니다");
        } else if (totalAmount < MIN_AMOUNT * receiverCount) {
            throw new IllegalArgumentException("1인당 최소 " + MIN_AMOUNT + "원 이상을 받을 수 있어야 합니다");
        }

        AirDropSender sender = AirDropSender.builder()
            .userId(userId)
            .roomId(roomId)
            .token(RandomStringUtils.randomAlphanumeric(3))
            .totalAmount(totalAmount)
            .receiverCount(receiverCount)
            .createdAt(System.currentTimeMillis())
            .build();
        senderRepository.save(sender);

        List<AirDropReceiver> receivers = new ArrayList<>(receiverCount);
        int remainingBonus = totalAmount - MIN_AMOUNT * receiverCount;

        for (int i = 0; i < receiverCount; i++) {
            int bonus =
                (i < receiverCount - 1) ? random.nextInt(remainingBonus + 1) : remainingBonus;
            receivers.add(
                AirDropReceiver.builder()
                    .sender(sender)
                    .amount(MIN_AMOUNT + bonus)
                    .build());
            remainingBonus -= bonus;
        }
        receiverRepository.saveAll(receivers);

        return sender.getToken();
    }

    public String receive(Long userId, String roomId, String token) {
        return "";
    }

    public String status(Long userId, String token) {
        return "";
    }
}