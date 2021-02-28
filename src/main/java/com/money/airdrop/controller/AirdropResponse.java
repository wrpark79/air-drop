package com.money.airdrop.controller;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class AirdropResponse {

    private int totalAmount;
    private int receivedAmount;
    private long createdAt;
    private List<Recipient> recipients;

    public void addRecipient(long userId, int amount) {
        if (recipients == null) {
            recipients = new ArrayList<>();
        }

        recipients.add(new Recipient(userId, amount));
    }

    @Data
    @AllArgsConstructor
    private static class Recipient {

        private long userId;
        private int amount;
    }

}