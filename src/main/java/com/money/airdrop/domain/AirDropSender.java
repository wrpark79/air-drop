package com.money.airdrop.domain;

import java.util.Collection;
import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import lombok.Data;
import lombok.NonNull;

@Data
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

  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "sender_id")
  private Collection<AirDropReceiver> receivers;
}