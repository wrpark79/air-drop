package com.money.airdrop.service;

import com.money.airdrop.controller.AirDropRequest;
import com.money.airdrop.controller.AirDropResponse;
import com.money.airdrop.domain.AirDropEvent;
import com.money.airdrop.domain.AirDropRecipient;
import com.money.airdrop.repository.EventRepository;
import com.money.airdrop.repository.RecipientRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AirDropService {

    private static final int MAX_AMOUNT = 100000000;
    private static final int MIN_AMOUNT = 100;

    private final EventRepository eventRepository;
    private final RecipientRepository recipientRepository;

    private final Random random = new Random();

    public AirDropService(EventRepository eventRepository,
        RecipientRepository recipientRepository) {
        this.eventRepository = eventRepository;
        this.recipientRepository = recipientRepository;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public String send(Long userId, String roomId, AirDropRequest payload) {
        int totalAmount = payload.getAmount();
        if (totalAmount > MAX_AMOUNT) {
            throw new IllegalArgumentException("뿌릴 금액은 " + MAX_AMOUNT + "원 이하여야 합니다");
        }

        int totalCount = payload.getCount();
        if (totalCount <= 0) {
            throw new IllegalArgumentException("받을 사람은 최소한 1명 이상이어야 합니다");
        } else if (totalAmount < MIN_AMOUNT * totalCount) {
            throw new IllegalArgumentException("1인당 최소 " + MIN_AMOUNT + "원 이상을 받을 수 있어야 합니다");
        }

        AirDropEvent event = AirDropEvent.builder()
            .userId(userId)
            .roomId(roomId)
            .token(RandomStringUtils.randomAlphanumeric(3))
            .totalAmount(totalAmount)
            .createdAt(System.currentTimeMillis())
            .build();

        List<AirDropRecipient> recipients = new ArrayList<>(totalCount);
        int remainingBonus = totalAmount - MIN_AMOUNT * totalCount;

        for (int i = 0; i < totalCount; i++) {
            int bonus =
                (i < totalCount - 1) ? random.nextInt(remainingBonus + 1) : remainingBonus;
            recipients.add(
                AirDropRecipient.builder()
                    .event(event)
                    .amount(MIN_AMOUNT + bonus)
                    .build());
            remainingBonus -= bonus;
        }
        event.setRecipients(recipients);

        eventRepository.save(event);
        recipientRepository.saveAll(recipients);

        return event.getToken();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public int receive(Long userId, String roomId, String token) {
        Optional<AirDropEvent> optEvent = eventRepository.findByRoomIdAndToken(roomId, token);
        if (optEvent.isEmpty()) {
            throw new IllegalArgumentException("뿌린 기록을 찾을 수 없습니다");
        }

        AirDropEvent event = optEvent.get();
        if (event.getUserId().equals(userId)) {
            throw new IllegalArgumentException("뿌린 사람은 받을 수 없습니다");
        } else if (System.currentTimeMillis() > event.getCreatedAt() + 600000) {
            throw new IllegalArgumentException("받을 수 있는 시간이 초과되었습니다");
        }

        if (recipientRepository.findByEventIdAndUserId(event.getId(), userId).isPresent()) {
            throw new IllegalArgumentException("뿌리기는 한번만 받을 수 있습니다");
        }

        Optional<AirDropRecipient> optRecipient =
            recipientRepository.findByEventIdAndUserIdNull(event.getId());
        if (optRecipient.isEmpty()) {
            throw new RuntimeException("뿌리기가 이미 종료되었습니다");
        }

        AirDropRecipient recipient = optRecipient.get();
        recipient.setUserId(userId);
        recipientRepository.save(recipient);

        return recipient.getAmount();
    }

    public AirDropResponse status(Long userId, String roomId, String token) {
        Optional<AirDropEvent> optEvent =
            eventRepository.findByUserIdAndRoomIdAndToken(userId, roomId, token);
        if (optEvent.isEmpty()) {
            throw new IllegalArgumentException("뿌리기를 찾을 수 없습니다");
        }

        AirDropEvent event = optEvent.get();
        if (System.currentTimeMillis() > event.getCreatedAt() + 7 * 86400000) {
            throw new RuntimeException("시간이 경과되어 조회할 수 없습니다");
        }

        AirDropResponse response = new AirDropResponse();
        int receivedAmount = 0;

        List<AirDropRecipient> recipients = event.getRecipients();
        if (recipients != null) {
            for (AirDropRecipient recipient : event.getRecipients()) {
                if (recipient.getUserId() != null) {
                    receivedAmount += recipient.getAmount();
                    response.addRecipient(recipient.getUserId(), recipient.getAmount());
                }
            }
        }
        response.setTotalAmount(event.getTotalAmount());
        response.setReceivedAmount(receivedAmount);

        return response;
    }
}