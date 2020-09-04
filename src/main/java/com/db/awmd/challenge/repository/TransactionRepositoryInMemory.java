package com.db.awmd.challenge.repository;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.Money;
import com.db.awmd.challenge.exception.InvalidAccountException;
import com.db.awmd.challenge.exception.InvalidAmountException;
import com.db.awmd.challenge.service.EmailNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Repository
@Slf4j
public class TransactionRepositoryInMemory implements TransactionRepository{

    private final ReentrantLock lock = new ReentrantLock(true);
    private final Map<String, Account> accounts = new ConcurrentHashMap<>();

    public EmailNotificationService emailNotificationService;
    /** fetching created account records and added into accounts list*/
    @Autowired
    private AccountsRepository accountsRepository;

    @Autowired
    public TransactionRepositoryInMemory(EmailNotificationService emailNotificationService){
        this.emailNotificationService = emailNotificationService;
    }

    @Override
    public void clearAccounts() {
        accounts.clear();
    }

    @Override
    public Account getAccount(String accountId) {
        return accounts.get(accountId);
    }

    @Override
    public void transferMoney(Money money) {

        /** acquiring Reentrant Lock for multi threaded environment*/
        lock.lock();
        /** fetching existing account records*/
        if(accounts.isEmpty()){
            Account accountFrom = accountsRepository.getAccount(money.getAccountFrom());
            Account accountTo = accountsRepository.getAccount(money.getAccountTo());
            if(accountFrom != null && !accountFrom.getAccountId().isEmpty()){
                accounts.put(accountFrom.getAccountId(),accountFrom);
            }
            if(accountTo != null && !accountTo.getAccountId().isEmpty()){
                accounts.put(accountTo.getAccountId(),accountTo);
            }
        }

        try {

            Account accountFrom = accounts.get(money.getAccountFrom());
            Account accountTo = accounts.get(money.getAccountTo());

            if(accountFrom.getBalance().compareTo(money.getAmount()) == -1){
                throw new InvalidAmountException("Insufficient balance for withdraw of accountID: "+accountFrom.getAccountId());
            }
            if (accountTo != null && accountTo.getAccountId() != null) {
                BigDecimal depositAmount = accountTo.getBalance();
                depositAmount = depositAmount.add(money.getAmount());
                accountTo.setBalance(depositAmount);
                accounts.put(accountTo.getAccountId(), accountTo);
                log.debug("Account ID{}: deposit Amount{}:",accountTo.getAccountId(),depositAmount);

                BigDecimal withdrawAmount = accountFrom.getBalance();
                withdrawAmount = withdrawAmount.subtract(money.getAmount());
                accountFrom.setBalance(withdrawAmount);
                accounts.put(accountFrom.getAccountId(), accountFrom);
                log.debug("Account ID{}: withdraw Amount{}:",accountFrom.getAccountId(),withdrawAmount);

                emailNotificationService.notifyAboutTransfer(accounts.get(money.getAccountFrom()), "Amount INR" + money.getAmount() + " has been debited. Current available balance:" + accountFrom.getBalance());
                emailNotificationService.notifyAboutTransfer(accounts.get(money.getAccountTo()), "Amount INR" + money.getAmount() + " has been credited. Current available balance:" + accountTo.getBalance());
            } else {
                throw new InvalidAccountException("AccountTo ID doesn't exist");
            }
        }finally{
            /**release the Reentrant lock*/
            lock.unlock();
        }
    }
}
