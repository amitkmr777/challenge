package com.db.awmd.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.Money;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.exception.InvalidAccountException;
import com.db.awmd.challenge.exception.InvalidAmountException;
import com.db.awmd.challenge.service.AccountsService;
import java.math.BigDecimal;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountsServiceTest {

  @Autowired
  private AccountsService accountsService;

  @Test
  public void addAccount() throws Exception {
    Account account = new Account("Id-123");
    account.setBalance(new BigDecimal(1000));
    this.accountsService.createAccount(account);

    assertThat(this.accountsService.getAccount("Id-123")).isEqualTo(account);
  }

  @Test
  public void addAccount_failsOnDuplicateId() throws Exception {
    String uniqueId = "Id-" + System.currentTimeMillis();
    Account account = new Account(uniqueId);
    this.accountsService.createAccount(account);

    try {
      this.accountsService.createAccount(account);
      fail("Should have failed when adding duplicate account");
    } catch (DuplicateAccountIdException ex) {
      assertThat(ex.getMessage()).isEqualTo("Account id " + uniqueId + " already exists!");
    }
  }

  /*@Test
  public void transferMoneyTest() throws Exception{
    Account accountFrom = new Account("Id-120");
    accountFrom.setBalance(new BigDecimal(1000));
    this.accountsService.createAccount(accountFrom);
    assertThat(this.accountsService.getAccount("Id-120")).isEqualTo(accountFrom);

    Account accountTo = new Account("Id-121");
    accountTo.setBalance(new BigDecimal(1000));
    this.accountsService.createAccount(accountTo);
    assertThat(this.accountsService.getAccount("Id-121")).isEqualTo(accountTo);

    Money money = new Money("Id-120","Id-121",new BigDecimal(500));
    this.accountsService.transferMoney(money);
    Account accountFrom1 = this.accountsService.getAccount("Id-120");
    assertThat(accountFrom1.getAccountId()).isEqualTo("Id-120");
    assertThat(accountFrom1.getBalance()).isEqualByComparingTo("500");

    Account accountTo1 = accountsService.getAccount("Id-121");
    assertThat(accountTo1.getAccountId()).isEqualTo("Id-121");
    assertThat(accountTo1.getBalance()).isEqualByComparingTo("1500");
  }
  @Test
  public void transferMoneyWithZeroBalanceTest() throws Exception{
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
      this.accountsService.transferMoney(money);
      fail("Should have failed when transfer money with zero balance account");
    } catch (InvalidAmountException ex) {
      assertThat(ex.getMessage()).isEqualTo("Insufficient amount to transfer from accountID: Id-124");
    }
  }
  @Test
  public void transferMoneyWithNoAmountTest() throws Exception{
    Account accountFrom = new Account("Id-126");
    accountFrom.setBalance(new BigDecimal(1000));
    this.accountsService.createAccount(accountFrom);
    assertThat(this.accountsService.getAccount("Id-126")).isEqualTo(accountFrom);

    Account accountTo = new Account("Id-127");
    accountTo.setBalance(new BigDecimal(1000));
    this.accountsService.createAccount(accountTo);
    assertThat(this.accountsService.getAccount("Id-127")).isEqualTo(accountTo);

      Money money = new Money("Id-126","Id-127",new BigDecimal(0));
      this.accountsService.transferMoney(money);
      Account accountFrom1 = this.accountsService.getAccount("Id-126");
      assertThat(accountFrom1.getAccountId()).isEqualTo("Id-126");
      assertThat(accountFrom1.getBalance()).isEqualByComparingTo("1000");

      Account accountTo1 = accountsService.getAccount("Id-127");
      assertThat(accountTo1.getAccountId()).isEqualTo("Id-127");
      assertThat(accountTo1.getBalance()).isEqualByComparingTo("1000");

  }
  @Test
  public void transferMoneyToInvalidAccountTest() throws Exception{
    Account accountFrom = new Account("Id-118");
    accountFrom.setBalance(new BigDecimal(1000));
    this.accountsService.createAccount(accountFrom);
    assertThat(this.accountsService.getAccount("Id-118")).isEqualTo(accountFrom);

    try {
      Money money = new Money("Id-118","Id-119",new BigDecimal(500));
      this.accountsService.transferMoney(money);
      fail("Should have failed when transfer money to invalid accountID");
    } catch (InvalidAccountException ex) {
      assertThat(ex.getMessage()).isEqualTo("Account ID doesn't exist");
    }
  }*/

}
