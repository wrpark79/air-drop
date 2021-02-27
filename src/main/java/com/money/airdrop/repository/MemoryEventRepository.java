package com.money.airdrop.repository;

import com.money.airdrop.domain.AirDropEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class MemoryEventRepository implements EventRepository {

    private static final Map<Long, AirDropEvent> store = new HashMap<>();
    private static long sequence = 0;

    @Override
    public <S extends AirDropEvent> S save(S entity) {
        entity.setId(++sequence);
        store.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public <S extends AirDropEvent> Iterable<S> saveAll(Iterable<S> entities) {
        for (AirDropEvent entity : entities) {
            entity.setId(++sequence);
            store.put(entity.getId(), entity);
        }
        return entities;
    }

    @Override
    public Optional<AirDropEvent> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    @Override
    public Iterable<AirDropEvent> findAll() {
        return null;
    }

    @Override
    public Iterable<AirDropEvent> findAllById(Iterable<Long> longs) {
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
    public void delete(AirDropEvent entity) {
    }

    @Override
    public void deleteAll(Iterable<? extends AirDropEvent> entities) {
    }

    @Override
    public void deleteAll() {
    }

    @Override
    public Optional<AirDropEvent> findByUserIdAndRoomIdAndToken(Long userId, String roomId,
        String token) {
        return store.values().stream()
            .filter(e -> e.getUserId().equals(userId) &&
                e.getRoomId().equals(roomId) &&
                e.getToken().equals(token))
            .findFirst();
    }

    @Override
    public Optional<AirDropEvent> findByRoomIdAndToken(String roomId,
        String token) {
        return store.values().stream()
            .filter(e -> e.getRoomId().equals(roomId) && e.getToken().equals(token))
            .findFirst();
    }

    public void clear() {
        store.clear();
    }
}