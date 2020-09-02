package com.db.awmd.challenge.service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.Money;
import com.db.awmd.challenge.exception.InvalidAccountException;
import com.db.awmd.challenge.exception.InvalidAmountException;
import com.db.awmd.challenge.repository.TransactionRepository;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    @Getter
    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public void transferMoney(Money money) throws InvalidAccountException, InvalidAmountException {
        this.transactionRepository.transferMoney(money);
    }

    public Account getAccount(String accountId) {
        return this.transactionRepository.getAccount(accountId);
    }
}
