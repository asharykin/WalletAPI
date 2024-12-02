package org.javacode.wallet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.javacode.wallet.dto.RequestWalletDto;
import org.javacode.wallet.dto.ResponseWalletDto;
import org.javacode.wallet.exception.InsufficientFundsException;
import org.javacode.wallet.exception.WalletNotFoundException;
import org.javacode.wallet.service.WalletService;
import org.javacode.wallet.util.WalletOperation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class WalletControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WalletService walletService;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testGetWallet_Success() throws Exception {
        UUID walletId = UUID.fromString("325342c5-126e-424a-b0f8-b24129a5babe");
        ResponseWalletDto responseWalletDto = new ResponseWalletDto(walletId, 0);

        when(walletService.getWallet(walletId)).thenReturn(responseWalletDto);

        mockMvc.perform(get("/api/v1/wallets/{walletId}", walletId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.walletId").value(walletId.toString()))
                .andExpect(jsonPath("$.balance").value(0));
    }

    @Test
    public void testGetWallet_NotFound() throws Exception {
        UUID walletId = UUID.fromString("ce41a409-0b85-48e8-b24e-11a6cb6fd300");

        when(walletService.getWallet(walletId)).thenThrow(new WalletNotFoundException("Wallet not found"));

        mockMvc.perform(get("/api/v1/wallets/{walletId}", walletId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Wallet not found"));
    }

    @Test
    public void testGetWallet_InvalidUUID() throws Exception {
        String invalidId = "invalid-uuid";

        mockMvc.perform(get("/api/v1/wallets/{walletId}", invalidId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateWallet_Success() throws Exception {
        UUID walletId = UUID.fromString("325342c5-126e-424a-b0f8-b24129a5babe");
        RequestWalletDto request = new RequestWalletDto(walletId, WalletOperation.DEPOSIT, 1000);

        ResponseWalletDto responseWalletDto = new ResponseWalletDto(walletId, 1000);

        when(walletService.updateWallet(any(RequestWalletDto.class))).thenReturn(responseWalletDto);

        mockMvc.perform(post("/api/v1/wallets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.walletId").value(walletId.toString()))
                .andExpect(jsonPath("$.balance").value(1000));
    }

    @Test
    public void testUpdateWallet_NotFound() throws Exception {
        UUID walletId = UUID.fromString("ce41a409-0b85-48e8-b24e-11a6cb6fd300");
        RequestWalletDto request = new RequestWalletDto(walletId, WalletOperation.DEPOSIT, 1000);

        when(walletService.updateWallet(any(RequestWalletDto.class))).thenThrow(new WalletNotFoundException("Wallet not found"));

        mockMvc.perform(post("/api/v1/wallets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Wallet not found"));
    }

    @Test
    public void testUpdateWallet_InsufficientFunds() throws Exception {
        UUID walletId = UUID.fromString("325342c5-126e-424a-b0f8-b24129a5babe");
        RequestWalletDto request = new RequestWalletDto(walletId, WalletOperation.WITHDRAW, 1000);

        when(walletService.updateWallet(any(RequestWalletDto.class))).thenThrow(new InsufficientFundsException("Insufficient funds"));

        mockMvc.perform(post("/api/v1/wallets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Insufficient funds"));
    }

    @Test
    public void testUpdateWallet_InvalidAmount() throws Exception {
        UUID walletId = UUID.fromString("325342c5-126e-424a-b0f8-b24129a5babe");
        RequestWalletDto request = new RequestWalletDto(walletId, WalletOperation.DEPOSIT, -100);

        mockMvc.perform(post("/api/v1/wallets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateWallet_NullWalletId() throws Exception {
        RequestWalletDto request = new RequestWalletDto(null, WalletOperation.DEPOSIT, 1000);

        mockMvc.perform(post("/api/v1/wallets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateWallet_NullOperationType() throws Exception {
        UUID walletId = UUID.fromString("325342c5-126e-424a-b0f8-b24129a5babe");
        RequestWalletDto request = new RequestWalletDto(walletId, null, 1000);

        mockMvc.perform(post("/api/v1/wallets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}

