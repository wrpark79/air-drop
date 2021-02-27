package com.money.airdrop.repository;

import com.money.airdrop.domain.AirDropSender;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface SenderRepository extends CrudRepository<AirDropSender, Long> {

    Optional<AirDropSender> findByUserIdAndRoomIdAndToken(Long userId, String roomId, String token);

    Optional<AirDropSender> findByRoomIdAndToken(String roomId, String token);
}