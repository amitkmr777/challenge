package com.db.awmd.challenge;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.Money;
import com.db.awmd.challenge.exception.InvalidAccountException;
import com.db.awmd.challenge.exception.InvalidAmountException;
import com.db.awmd.challenge.service.AccountsService;
import com.db.awmd.challenge.service.TransactionService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TransactionServiceTest {
    @Autowired
    private AccountsService accountsService;

    @Autowired
    private TransactionService transactionService;

    @Before
    public void init() {
        // Reset the existing accounts before each test.
        transactionService.getTransactionRepository().clearAccounts();
    }
    @Test
    public void transferMoneyServiceTest() throws Exception{
        Account accountFrom = new Account("Id-120");
        accountFrom.setBalance(new BigDecimal(1000));
        accountsService.createAccount(accountFrom);
        assertThat(this.accountsService.getAccount("Id-120")).isEqualTo(accountFrom);

        Account accountTo = new Account("Id-121");
        accountTo.setBalance(new BigDecimal(1000));
        accountsService.createAccount(accountTo);
        assertThat(this.accountsService.getAccount("Id-121")).isEqualTo(accountTo);

        Money money = new Money("Id-120","Id-121",new BigDecimal(500));
        transactionService.transferMoney(money);
        Account accountFrom1 = transactionService.getAccount("Id-120");
        assertThat(accountFrom1.getAccountId()).isEqualTo("Id-120");
        assertThat(accountFrom1.getBalance()).isEqualByComparingTo("500");

        Account accountTo1 = transactionService.getAccount("Id-121");
        assertThat(accountTo1.getAccountId()).isEqualTo("Id-121");
        assertThat(accountTo1.getBalance()).isEqualByComparingTo("1500");
    }

    @Test
    public void transferMoneyWithZeroBalanceServiceTest() throws Exception{
        Account accountFrom = new Account("Id-124");
        accountFrom.setBalance(new BigDecimal(0));
        this.accountsService.createAccount(accountFrom);
        assertThat(this.accountsService.getAccount("Id-124")).isEqualTo(accountFrom);

        Account accountTo = new Account("Id-125");
        accountTo.setBalance(new BigDecimal(1000));
        this.accountsService.createAccount(accountTo);
        assertThat(this.accountsService.getAccount("Id-125")).isEqualTo(accountTo);

        try {
            Money money = new Money("Id-124","Id-125",new BigDecimal(500));
            this.transactionService.transferMoney(money);
            fail("Should have failed when transfer money with zero balance account");
        } catch (InvalidAmountException ex) {
            assertThat(ex.getMessage()).isEqualTo("Insufficient balance for withdraw of accountID: Id-124");
        }
    }
    @Test
    public void transferMoneyWithNoAmountServiceTest() throws Exception{
        Account accountFrom = new Account("Id-126");
        accountFrom.setBalance(new BigDecimal(1000));
        this.accountsService.createAccount(accountFrom);
        assertThat(this.accountsService.getAccount("Id-126")).isEqualTo(accountFrom);

        Account accountTo = new Account("Id-127");
        accountTo.setBalance(new BigDecimal(1000));
        this.accountsService.createAccount(accountTo);
        assertThat(this.accountsService.getAccount("Id-127")).isEqualTo(accountTo);

        Money money = new Money("Id-126","Id-127",new BigDecimal(0));
        this.transactionService.transferMoney(money);

        Account accountFrom1 = this.transactionService.getAccount("Id-126");
        assertThat(accountFrom1.getAccountId()).isEqualTo("Id-126");
        assertThat(accountFrom1.getBalance()).isEqualByComparingTo("1000");

        Account accountTo1 = transactionService.getAccount("Id-127");
        assertThat(accountTo1.getAccountId()).isEqualTo("Id-127");
        assertThat(accountTo1.getBalance()).isEqualByComparingTo("1000");

    }
    @Test
    public void transferMoneyToInvalidAccountServiceTest() throws Exception{
        Account accountFrom = new Account("Id-118");
        accountFrom.setBalance(new BigDecimal(1000));
        this.accountsService.createAccount(accountFrom);
        assertThat(this.accountsService.getAccount("Id-118")).isEqualTo(accountFrom);

        try {
            Money money = new Money("Id-118","Id-119",new BigDecimal(500));
            this.transactionService.transferMoney(money);
            fail("Should have failed when transfer money to invalid accountID");
        } catch (InvalidAccountException ex) {
            assertThat(ex.getMessage()).isEqualTo("AccountTo ID doesn't exist");
        }
    }
}
