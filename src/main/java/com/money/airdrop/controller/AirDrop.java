package com.money.airdrop.controller;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AirDrop {

    private int amount;
    private int receiverCount;
}