package com.money.airdrop.controller;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AirdropRequest {

    private int amount;
    private int count;
}