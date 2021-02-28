package com.money.airdrop.service;

import com.money.airdrop.controller.AirdropRequest;
import com.money.airdrop.controller.AirdropResponse;
import com.money.airdrop.domain.AirdropEvent;
import com.money.airdrop.domain.AirdropRecipient;
import com.money.airdrop.repository.EventRepository;
import com.money.airdrop.repository.RecipientRepository;
import java.lang.management.MonitorInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AirdropService {

    private static final int MAX_AMOUNT = 100000000;
    private static final int MIN_AMOUNT = 100;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final EventRepository eventRepository;
    private final RecipientRepository recipientRepository;

    private final Random random = new Random();

    public AirdropService(EventRepository eventRepository,
        RecipientRepository recipientRepository) {
        this.eventRepository = eventRepository;
        this.recipientRepository = recipientRepository;
    }

    /**
     * <p>주어진 파라미터를 사용하여 에어드랍 이벤트를 생성한다.</p>
     *
     * @param userId  사용자 아이디
     * @param roomId  현재 속한 채팅방 아이디
     * @param payload 뿌릴 금액과 인원
     * @return 이벤트를 식별하기 위한 토큰
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public String send(Long userId, String roomId, AirdropRequest payload) {
        int totalAmount = payload.getAmount();
        if (totalAmount > MAX_AMOUNT) {
            throw new IllegalArgumentException("뿌릴 금액은 " + MAX_AMOUNT + "원 이하여야 합니다");
        }

        int totalCount = payload.getCount();
        if (totalCount <= 0) {
            throw new IllegalArgumentException("받을 사람은 최소한 1명 이상이어야 합니다");
        } else if (totalAmount < MIN_AMOUNT * totalCount) {
            // 작은 금액으로 반복 시도하는 것을 방지하기 위해 최소 금액 제한을 둔다.
            throw new IllegalArgumentException("1인당 최소 " + MIN_AMOUNT + "원 이상을 받을 수 있어야 합니다");
        }

        AirdropEvent event = AirdropEvent.builder()
            .userId(userId)
            .roomId(roomId)
            .token(RandomStringUtils.randomAlphanumeric(3))
            .totalAmount(totalAmount)
            .createdAt(System.currentTimeMillis())
            .recipients(new ArrayList<>(totalCount))
            .build();

        logger.info("airdrop event created: " + event.toString());
        eventRepository.save(event);

        int remainingBonus = totalAmount - MIN_AMOUNT * totalCount;
        for (int i = 0; i < totalCount; i++) {
            // 남은 금액에서 랜덤값으로 결정하여 최소 금액에 더한다.
            int bonus = (i < totalCount - 1) ? getBonus(remainingBonus) : remainingBonus;

            // 아직 받을 사람이 결정되지 않았으므로 userId는 null이다.
            AirdropRecipient recipient =
                AirdropRecipient.builder()
                    .event(event)
                    .amount(MIN_AMOUNT + bonus)
                    .build();

            logger.info("airdrop recipient created: " + recipient.toString());
            recipient.getEvent().getRecipients().add(recipient);
            recipientRepository.save(recipient);
            remainingBonus -= bonus;
        }

        return event.getToken();
    }

    /**
     * <p>주어진 에어드랍 이벤트에서 랜덤 금액을 수령한다.</p>
     *
     * @param userId 사용자 아이디
     * @param roomId 현재 속한 채팅방 아이디
     * @param token  이벤트 토큰
     * @return 수령한 금액
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public int receive(Long userId, String roomId, String token) {
        // 이벤트 기본 정보 확인
        AirdropEvent event = eventRepository.findByRoomIdAndToken(roomId, token)
            .map(e -> {
                if (e.getUserId().equals(userId)) {
                    throw new IllegalArgumentException("뿌린 사람은 받을 수 없습니다");
                } else if (System.currentTimeMillis() > e.getCreatedAt() + 600000) {
                    throw new IllegalArgumentException("받을 수 있는 시간이 초과되었습니다");
                }
                return e;
            })
            .orElseThrow(() -> {
                throw new IllegalArgumentException("뿌린 기록을 찾을 수 없습니다");
            });

        if (recipientRepository.findFirstByEventIdAndUserId(event.getId(), userId).isPresent()) {
            throw new IllegalArgumentException("뿌리기는 한번만 받을 수 있습니다");
        }

        // SELECT...FOR UPDATE 구문을 사용하여 정합성을 해결한다. 단, Oracle을 제외한 다른 데이터베이스는 시퀀스나
        // SKIP_LOCKED 기능을 지원하지 않으므로 결과를 확인하여 다시 시도한다.
        return recipientRepository.findFirstByEventIdAndUserIdNull(event.getId())
            .map(recipient -> {
                logger.info("airdrop received: " + recipient.toString());
                // row lock이 풀린 후에 predicate을 적용하므로 반드시 아무도 가져가지 않은 recipient가 반환된다.
                assert recipient.getUserId() == null;
                // userId를 설정하여 이미 받아갔음을 표시한다.
                recipient.setUserId(userId);
                recipientRepository.save(recipient);
                return recipient.getAmount();
            })
            .orElseThrow(() -> {
                throw new RuntimeException("뿌리기가 이미 종료되었습니다");
            });
    }

    /**
     * <p>에어드랍 이벤트의 상태를 조회한다.</p>
     *
     * @param userId 사용자 아이디
     * @param roomId 현재 속한 채팅방 아이디
     * @param token  이벤트 토큰
     * @return 상태 정보
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public AirdropResponse status(Long userId, String roomId, String token) {
        AirdropEvent event = eventRepository.findByUserIdAndRoomIdAndToken(userId, roomId, token)
            .map(e -> {
                if (System.currentTimeMillis() > e.getCreatedAt() + 7 * 86400000) {
                    throw new RuntimeException("시간이 경과되어 조회할 수 없습니다");
                }
                return e;
            })
            .orElseThrow(() -> {
                throw new IllegalArgumentException("뿌리기 기록을 찾을 수 없습니다");
            });

        AirdropResponse response = new AirdropResponse();
        int receivedAmount = 0;

        List<AirdropRecipient> recipients = event.getRecipients();
        if (recipients != null) {
            for (AirdropRecipient recipient : event.getRecipients()) {
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

    private int getBonus(int remainingBonus) {
        return (random.nextInt(remainingBonus + 1) / MIN_AMOUNT) * MIN_AMOUNT;
    }
}