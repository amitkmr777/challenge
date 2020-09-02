package com.db.awmd.challenge.repository;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.Money;
import com.db.awmd.challenge.exception.InvalidAccountException;
import com.db.awmd.challenge.exception.InvalidAmountException;
import com.db.awmd.challenge.service.EmailNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.Null;
import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class TransactionRepositoryInMemory implements TransactionRepository{

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
        /**Only one instance of this class can modify
         this method at t time in multi threaded environment.*/
        synchronized (AccountsRepositoryInMemory.class) {

            /** to fetching existing account records*/
            if(accounts.isEmpty()){
                Account accountFrom = accountsRepository.getAccount(money.getAccountFrom());
                Account accountTo = accountsRepository.getAccount(money.getAccountTo());
                if(accountFrom != null){
                    accounts.put(accountFrom.getAccountId(),accountFrom);
                }
                if(accountTo != null){
                    accounts.put(accountTo.getAccountId(),accountTo);
                }
            }

            Account accountFrom = accounts.get(money.getAccountFrom());
            Account accountTo = accounts.get(money.getAccountTo());
            if(accountFrom.getBalance().compareTo(new BigDecimal(0)) == 0){
                throw new InvalidAmountException("Insufficient amount to transfer from accountID: "+accountFrom.getAccountId());
            }
            if (accountTo != null && accountTo.getAccountId() != null) {
                BigDecimal creditAmount = new BigDecimal(0);
                creditAmount = accountTo.getBalance();
                creditAmount = creditAmount.add(money.getAmount());
                accountTo.setBalance(creditAmount);
                accounts.put(accountTo.getAccountId(), accountTo);

                BigDecimal debitAmount = new BigDecimal(0);
                debitAmount = accountFrom.getBalance();
                debitAmount = debitAmount.subtract(money.getAmount());
                accountFrom.setBalance(debitAmount);
                accounts.put(accountFrom.getAccountId(), accountFrom);

                emailNotificationService.notifyAboutTransfer(accounts.get(money.getAccountFrom()), "Amount INR" + money.getAmount() + " has been debited. Current available balance:" + accountFrom.getBalance());
                emailNotificationService.notifyAboutTransfer(accounts.get(money.getAccountTo()), "Amount INR" + money.getAmount() + " has been credited. Current available balance:" + accountTo.getBalance());
            } else {
                throw new InvalidAccountException("Account ID doesn't exist");
            }
        }
    }
}
