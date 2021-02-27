package com.money.airdrop.repository;

import com.money.airdrop.domain.AirDropRecipient;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class MemoryRecipientRepository implements RecipientRepository {

    private static final Map<Long, AirDropRecipient> store = new HashMap<>();
    private static long sequence = 0;

    @Override
    public <S extends AirDropRecipient> S save(S entity) {
        entity.setId(++sequence);
        store.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public <S extends AirDropRecipient> Iterable<S> saveAll(Iterable<S> entities) {
        for (AirDropRecipient entity : entities) {
            entity.setId(++sequence);
            store.put(entity.getId(), entity);
        }
        return entities;
    }

    @Override
    public Optional<AirDropRecipient> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    @Override
    public Iterable<AirDropRecipient> findAll() {
        return store.values();
    }

    @Override
    public Iterable<AirDropRecipient> findAllById(Iterable<Long> longs) {
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
    public void delete(AirDropRecipient entity) {
    }

    @Override
    public void deleteAll(Iterable<? extends AirDropRecipient> entities) {
    }

    @Override
    public void deleteAll() {
    }

    @Override
    public Optional<AirDropRecipient> findByEventIdAndUserId(Long eventId, Long userId) {
        for (AirDropRecipient recipient : store.values()) {
            if (recipient.getEvent().getId().equals(eventId) &&
                recipient.getUserId() != null &&
                recipient.getUserId().equals(userId)) {
                return Optional.of(recipient);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<AirDropRecipient> findFirstByEventIdAndUserIdNull(Long eventId) {
        for (AirDropRecipient recipient : store.values()) {
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