package com.money.airdrop.controller;

import com.money.airdrop.service.AirDropService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AirDropController {

  private final AirDropService airDropService;

  public AirDropController(AirDropService airDropService) {
    this.airDropService = airDropService;
  }

  @PostMapping("/airdrops")
  public String send(
      @RequestHeader(name = "X-USER-ID") Long userId,
      @RequestHeader(name = "X-ROOM-ID") String roomId,
      @RequestBody AirDrop airDrop) {
    return airDropService.send(userId, roomId, airDrop);
  }

  @PostMapping("/airdrops/{token}")
  public String receive(
      @RequestHeader(name = "X-USER-ID") Long userId,
      @RequestHeader(name = "X-ROOM-ID") String roomId,
      @PathVariable String token) {
    return airDropService.receive(userId, roomId, token);
  }

  @GetMapping("/airdrops/{token}")
  public String status(@RequestHeader(name = "X-USER-ID") Long userId, @PathVariable String token) {
    return airDropService.status(userId, token);
  }
}