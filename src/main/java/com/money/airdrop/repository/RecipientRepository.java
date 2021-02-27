package com.money.airdrop.repository;

import com.money.airdrop.domain.AirDropRecipient;
import java.util.Optional;
import javax.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.CrudRepository;

public interface RecipientRepository extends CrudRepository<AirDropRecipient, Long> {

    Optional<AirDropRecipient> findByEventIdAndUserId(Long eventId, Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<AirDropRecipient> findByEventIdAndUserIdNull(Long eventId);
}