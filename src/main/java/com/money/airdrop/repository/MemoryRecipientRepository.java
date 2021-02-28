package com.money.airdrop.repository;

import com.money.airdrop.domain.AirdropRecipient;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class MemoryRecipientRepository implements RecipientRepository {

    private static final Map<Long, AirdropRecipient> store = new HashMap<>();
    private static long sequence = 0;

    @Override
    public <S extends AirdropRecipient> S save(S entity) {
        entity.setId(++sequence);
        store.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public <S extends AirdropRecipient> Iterable<S> saveAll(Iterable<S> entities) {
        for (AirdropRecipient entity : entities) {
            entity.setId(++sequence);
            store.put(entity.getId(), entity);
        }
        return entities;
    }

    @Override
    public Optional<AirdropRecipient> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    @Override
    public Iterable<AirdropRecipient> findAll() {
        return store.values();
    }

    @Override
    public Iterable<AirdropRecipient> findAllById(Iterable<Long> longs) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(Long aLong) {
    }

    @Override
    public void delete(AirdropRecipient entity) {
    }

    @Override
    public void deleteAll(Iterable<? extends AirdropRecipient> entities) {
    }

    @Override
    public void deleteAll() {
    }

    @Override
    public Optional<AirdropRecipient> findFirstByEventIdAndUserId(Long eventId, Long userId) {
        for (AirdropRecipient recipient : store.values()) {
            if (recipient.getEvent().getId().equals(eventId) &&
                recipient.getUserId() != null &&
                recipient.getUserId().equals(userId)) {
                return Optional.of(recipient);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<AirdropRecipient> findFirstByEventIdAndUserIdNull(Long eventId) {
        for (AirdropRecipient recipient : store.values()) {
            if (recipient.getEvent().getId().equals(eventId) && recipient.getUserId() == null) {
                return Optional.of(recipient);
            }
        }
        return Optional.empty();
    }

    public void clear() {
        store.clear();
    }
}