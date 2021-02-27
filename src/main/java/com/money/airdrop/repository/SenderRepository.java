package com.money.airdrop.repository;

import com.money.airdrop.domain.AirDropSender;
import org.springframework.data.repository.CrudRepository;

public interface SenderRepository extends CrudRepository<AirDropSender, Long> {

}