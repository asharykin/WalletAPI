package org.javacode.wallet.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name="wallets")
@NoArgsConstructor
@Getter
@Setter
public class Wallet {

    @Id
    private UUID id;

    private Integer balance;
}
