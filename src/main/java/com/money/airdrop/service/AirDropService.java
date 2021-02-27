package com.money.airdrop.service;

import com.money.airdrop.controller.AirDropRequest;
import com.money.airdrop.controller.AirDropResponse;
import com.money.airdrop.domain.AirDropReceiver;
import com.money.airdrop.domain.AirDropEvent;
import com.money.airdrop.repository.ReceiverRepository;
import com.money.airdrop.repository.EventRepository;
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
    private final ReceiverRepository receiverRepository;

    private final Random random = new Random();

    public AirDropService(EventRepository eventRepository,
        ReceiverRepository receiverRepository) {
        this.eventRepository = eventRepository;
        this.receiverRepository = receiverRepository;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public String send(Long userId, String roomId, AirDropRequest payload) {
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

        AirDropEvent event = AirDropEvent.builder()
            .userId(userId)
            .roomId(roomId)
            .token(RandomStringUtils.randomAlphanumeric(3))
            .totalAmount(totalAmount)
            .createdAt(System.currentTimeMillis())
            .build();

        List<AirDropReceiver> receivers = new ArrayList<>(receiverCount);
        int remainingBonus = totalAmount - MIN_AMOUNT * receiverCount;

        for (int i = 0; i < receiverCount; i++) {
            int bonus =
                (i < receiverCount - 1) ? random.nextInt(remainingBonus + 1) : remainingBonus;
            receivers.add(
                AirDropReceiver.builder()
                    .event(event)
                    .amount(MIN_AMOUNT + bonus)
                    .build());
            remainingBonus -= bonus;
        }
        event.setReceivers(receivers);

        eventRepository.save(event);
        receiverRepository.saveAll(receivers);

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

        if (receiverRepository.findByEventIdAndUserId(event.getId(), userId).isPresent()) {
            throw new IllegalArgumentException("뿌리기는 한번만 받을 수 있습니다");
        }

        Optional<AirDropReceiver> optReceiver =
            receiverRepository.findByEventIdAndUserIdNull(event.getId());
        if (optReceiver.isEmpty()) {
            throw new RuntimeException("뿌리기가 이미 종료되었습니다");
        }

        AirDropReceiver receiver = optReceiver.get();
        receiver.setUserId(userId);
        receiverRepository.save(receiver);

        return receiver.getAmount();
    }

    public AirDropResponse status(Long userId, String roomId, String token) {
        Optional<AirDropEvent> optEvent =
            eventRepository.findByUserIdAndRoomIdAndToken(userId, roomId, token);
        if (optEvent.isEmpty()) {
            throw new IllegalArgumentException("뿌리기를 찾을 수 없습니다");
        }

        AirDropEvent sender = optEvent.get();
        if (System.currentTimeMillis() > sender.getCreatedAt() + 7 * 86400000) {
            throw new RuntimeException("시간이 경과되어 조회할 수 없습니다");
        }

        AirDropResponse info = new AirDropResponse();
        int receivedAmount = 0;

        List<AirDropReceiver> receivers = sender.getReceivers();
        if (receivers != null) {
            for (AirDropReceiver receiver : sender.getReceivers()) {
                if (receiver.getUserId() != null) {
                    receivedAmount += receiver.getAmount();
                    info.addReceiver(receiver.getUserId(), receiver.getAmount());
                }
            }
        }
        info.setTotalAmount(sender.getTotalAmount());
        info.setReceivedAmount(receivedAmount);

        return info;
    }
}