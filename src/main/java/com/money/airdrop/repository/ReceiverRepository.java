package com.money.airdrop.repository;

import com.money.airdrop.domain.AirDropReceiver;
import org.springframework.data.repository.CrudRepository;

public interface ReceiverRepository extends CrudRepository<AirDropReceiver, Long> {

}