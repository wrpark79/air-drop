package com.money.airdrop.domain;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class AirDropSender {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    private Long userId;

    @NonNull
    private String roomId;

    @NonNull
    private String token;

    private int totalAmount;
    private int receiverCount;

    @NonNull
    private Long createdAt;
}