package com.db.awmd.challenge.repository;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.Money;

public interface TransactionRepository {

    public void clearAccounts();
    Account getAccount(String accountId);
    void transferMoney(Money money);
}
