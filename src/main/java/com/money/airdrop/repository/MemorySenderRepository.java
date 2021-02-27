package com.money.airdrop.repository;

import com.money.airdrop.domain.AirDropSender;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MemorySenderRepository implements SenderRepository {

    private static final Map<Long, AirDropSender> store = new HashMap<>();
    private static long sequence = 0;

    @Override
    public <S extends AirDropSender> S save(S entity) {
        entity.setId(++sequence);
        store.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public <S extends AirDropSender> Iterable<S> saveAll(Iterable<S> entities) {
        for (AirDropSender entity : entities) {
            entity.setId(++sequence);
            store.put(entity.getId(), entity);
        }
        return entities;
    }

    @Override
    public Optional<AirDropSender> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    @Override
    public Iterable<AirDropSender> findAll() {
        return null;
    }

    @Override
    public Iterable<AirDropSender> findAllById(Iterable<Long> longs) {
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
    public void delete(AirDropSender entity) {
    }

    @Override
    public void deleteAll(Iterable<? extends AirDropSender> entities) {
    }

    @Override
    public void deleteAll() {
    }

    @Override
    public Optional<AirDropSender> findByRoomIdAndToken(String roomId,
        String token) {
        return store.values().stream()
            .filter(sender -> sender.getRoomId().equals(roomId) && sender.getToken().equals(token))
            .findFirst();
    }

    public void clear() {
        store.clear();
    }
}