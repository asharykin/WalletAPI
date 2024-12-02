package org.javacode.wallet.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.javacode.wallet.util.WalletOperation;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class RequestWalletDto {

    @NotNull(message = "Wallet ID cannot be null")
    private UUID walletId;

    @NotNull(message = "Operation type cannot be null")
    private WalletOperation operationType;

    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be a positive integer")
    private Integer amount;

}
