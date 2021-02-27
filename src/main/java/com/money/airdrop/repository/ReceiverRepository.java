package com.money.airdrop.repository;

import com.money.airdrop.domain.AirDropReceiver;
import java.util.Optional;
import javax.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.CrudRepository;

public interface ReceiverRepository extends CrudRepository<AirDropReceiver, Long> {

    Optional<AirDropReceiver> findByIdAndUserId(Long id, Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<AirDropReceiver> findByIdAndUserIdNull(Long id);
}