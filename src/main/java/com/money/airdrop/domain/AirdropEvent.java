package com.money.airdrop.domain;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
@Entity
public class AirdropEvent {

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

    @NonNull
    private Long createdAt;

    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<AirdropRecipient> recipients;
}