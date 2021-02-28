package com.money.airdrop.repository;

import com.money.airdrop.domain.AirdropEvent;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface EventRepository extends CrudRepository<AirdropEvent, Long> {

    Optional<AirdropEvent> findByUserIdAndRoomIdAndToken(Long userId, String roomId, String token);

    Optional<AirdropEvent> findByRoomIdAndToken(String roomId, String token);
}