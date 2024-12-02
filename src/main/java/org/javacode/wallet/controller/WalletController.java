package org.javacode.wallet.controller;


import lombok.RequiredArgsConstructor;
import org.javacode.wallet.dto.RequestWalletDto;
import org.javacode.wallet.dto.ResponseWalletDto;
import org.javacode.wallet.service.WalletService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/wallets/{walletId}")
    public ResponseEntity<ResponseWalletDto> getWallet(@PathVariable UUID walletId) {
        ResponseWalletDto response = walletService.getWallet(walletId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/wallet")
    public ResponseEntity<ResponseWalletDto> updateWallet(@Valid @RequestBody RequestWalletDto request) {
        ResponseWalletDto response = walletService.updateWallet(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
