package com.money.airdrop.controller;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AirDropRequest {

    private int amount;
    private int count;
}