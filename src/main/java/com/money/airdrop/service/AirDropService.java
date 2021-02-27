package com.money.airdrop.service;

import com.money.airdrop.repository.ReceiverRepository;
import com.money.airdrop.repository.SenderRepository;
import org.springframework.stereotype.Service;

@Service
public class AirDropService {
  private final SenderRepository senderRepository;
  private final ReceiverRepository receiverRepository;

  public AirDropService(SenderRepository senderRepository, ReceiverRepository receiverRepository) {
    this.senderRepository = senderRepository;
    this.receiverRepository = receiverRepository;
  }

  public String send(Long userId, String roomId) {
    return "";
  }

  public String receive(Long userId, String roomId, String token) {
    return "";
  }

  public String status(Long userId, String token) {
    return "";
  }
}