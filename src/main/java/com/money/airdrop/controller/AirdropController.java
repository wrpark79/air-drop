package com.money.airdrop.controller;

import com.money.airdrop.service.AirdropService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AirdropController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final AirdropService airdropService;

    public AirdropController(AirdropService airdropService) {
        this.airdropService = airdropService;
    }

    @ApiOperation(value = "뿌리기 이벤트 생성")
    @PostMapping("/airdrops")
    public String send(
        @RequestHeader(name = "X-USER-ID") Long userId,
        @RequestHeader(name = "X-ROOM-ID") String roomId,
        @RequestBody AirdropRequest payload) {
        return airdropService.send(userId, roomId, payload);
    }

    @ApiOperation(value = "뿌리기 상태 조회")
    @GetMapping("/airdrops/{token}")
    public AirdropResponse status(
        @RequestHeader(name = "X-USER-ID") Long userId,
        @RequestHeader(name = "X-ROOM-ID") String roomId,
        @PathVariable String token) {
        return airdropService.status(userId, roomId, token);
    }

    @ApiOperation(value = "뿌리기 금액 수령")
    @PutMapping("/airdrops/{token}")
    public int receive(
        @RequestHeader(name = "X-USER-ID") Long userId,
        @RequestHeader(name = "X-ROOM-ID") String roomId,
        @PathVariable String token) {
        return airdropService.receive(userId, roomId, token);
    }
}