package com.money.airdrop.repository;

import com.money.airdrop.domain.AirDropEvent;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface EventRepository extends CrudRepository<AirDropEvent, Long> {

    Optional<AirDropEvent> findByUserIdAndRoomIdAndToken(Long userId, String roomId, String token);

    Optional<AirDropEvent> findByRoomIdAndToken(String roomId, String token);
}