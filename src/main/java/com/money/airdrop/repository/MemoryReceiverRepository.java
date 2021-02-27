package com.money.airdrop.repository;

import com.money.airdrop.domain.AirDropReceiver;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MemoryReceiverRepository implements ReceiverRepository {

    private static final Map<Long, AirDropReceiver> store = new HashMap<>();
    private static long sequence = 0;

    @Override
    public <S extends AirDropReceiver> S save(S entity) {
        entity.setId(++sequence);
        store.put(entity.getId(), entity);
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
        return null;
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
}