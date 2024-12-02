package org.javacode.wallet.service;

import lombok.RequiredArgsConstructor;
import org.javacode.wallet.dto.RequestWalletDto;
import org.javacode.wallet.dto.ResponseWalletDto;
import org.javacode.wallet.entity.Wallet;
import org.javacode.wallet.exception.InsufficientFundsException;
import org.javacode.wallet.exception.WalletNotFoundException;
import org.javacode.wallet.repository.WalletRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;

    public ResponseWalletDto getWallet(UUID walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found"));
        return new ResponseWalletDto(wallet.getId(), wallet.getBalance());
    }

    public ResponseWalletDto updateWallet(RequestWalletDto request) {
        Wallet wallet = walletRepository.findById(request.getWalletId())
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found"));
        switch (request.getOperationType()) {
            case DEPOSIT:
                wallet.setBalance(wallet.getBalance() + request.getAmount());
                break;
            case WITHDRAW:
                if (wallet.getBalance() < request.getAmount()) {
                    throw new InsufficientFundsException("Insufficient funds");
                }
                wallet.setBalance(wallet.getBalance() - request.getAmount());
                break;
        }
        walletRepository.save(wallet);
        return new ResponseWalletDto(wallet.getId(), wallet.getBalance());
    }
}
