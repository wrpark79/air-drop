package com.money.airdrop.repository;

import com.money.airdrop.domain.AirdropRecipient;
import java.util.Optional;
import javax.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.CrudRepository;

public interface RecipientRepository extends CrudRepository<AirdropRecipient, Long> {

    Optional<AirdropRecipient> findByEventIdAndUserId(Long eventId, Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<AirdropRecipient> findFirstByEventIdAndUserIdNull(Long eventId);
}