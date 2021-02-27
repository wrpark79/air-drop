package com.money.airdrop.controller;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class AirDropStatus {

    private int totalAmount;
    private int receivedAmount;
    private long createdAt;
    private List<Receiver> receivers;

    public void addReceiver(long userId, int amount) {
        receivers.add(new Receiver(userId, amount));
    }

    @Data
    @AllArgsConstructor
    private static class Receiver {

        private long userId;
        private int amount;
    }

}