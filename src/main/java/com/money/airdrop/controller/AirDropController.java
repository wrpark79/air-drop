package com.money.airdrop.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

public class AirDropController {

  @PostMapping("/airdrops")
  public String send(
      @RequestHeader(name = "X-USER-ID") Long userId,
      @RequestHeader(name = "X-ROOM-ID") String roomId) {
    return "";
  }

  @PostMapping("/airdrops/{token}")
  public String receive(
      @RequestHeader(name = "X-USER-ID") Long userId,
      @RequestHeader(name = "X-ROOM-ID") String roomId,
      @PathVariable String token) {
    return "";
  }

  @GetMapping("/airdrops/{token}")
  public String status(@RequestHeader(name = "X-USER-ID") Long userId, @PathVariable String token) {
    return "";
  }
}