package com.money.airdrop.repository;

import com.money.airdrop.domain.AirDropReceiver;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class MemoryReceiverRepository implements ReceiverRepository {

    private static final Map<Long, AirDropReceiver> store = new HashMap<>();
    private static long sequence = 0;

    @Override
    public <S extends AirDropReceiver> S save(S entity) {
        assert entity.getId() != null;
        return entity;
    }

    @Override
    public <S extends AirDropReceiver> Iterable<S> saveAll(Iterable<S> entities) {
        for (AirDropReceiver entity : entities) {
            entity.setId(++sequence);
            store.put(entity.getId(), entity);
        }
        return entities;
    }

    @Override
    public Optional<AirDropReceiver> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    @Override
    public Iterable<AirDropReceiver> findAll() {
        return store.values();
    }

    @Override
    public Iterable<AirDropReceiver> findAllById(Iterable<Long> longs) {
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
    public void delete(AirDropReceiver entity) {
    }

    @Override
    public void deleteAll(Iterable<? extends AirDropReceiver> entities) {
    }

    @Override
    public void deleteAll() {
    }

    @Override
    public Optional<AirDropReceiver> findByEventIdAndUserId(Long id, Long userId) {
        for (AirDropReceiver receiver : store.values()) {
            if (receiver.getEvent().getId().equals(id) &&
                receiver.getUserId() != null &&
                receiver.getUserId().equals(userId)) {
                return Optional.of(receiver);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<AirDropReceiver> findByEventIdAndUserIdNull(Long eventId) {
        for (AirDropReceiver receiver : store.values()) {
            if (receiver.getEvent().getId().equals(eventId) && receiver.getUserId() == null) {
                return Optional.of(receiver);
            }
        }
        return Optional.empty();
    }

    public void clear() {
        store.clear();
    }
}