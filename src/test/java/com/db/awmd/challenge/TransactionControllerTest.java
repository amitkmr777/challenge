package com.db.awmd.challenge;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.service.AccountsService;
import com.db.awmd.challenge.service.TransactionService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class TransactionControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AccountsService accountsService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void prepareMockMvc() {
        this.mockMvc = webAppContextSetup(this.webApplicationContext).build();

        // Reset the existing accounts before each test.
        transactionService.getTransactionRepository().clearAccounts();
    }

    @Test
    public void transferMoneyTest() throws Exception{
        this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountId\":\"Id-123\",\"balance\":1000}")).andExpect(status().isCreated());
        Account account123 = accountsService.getAccount("Id-123");
        assertThat(account123.getAccountId()).isEqualTo("Id-123");

        this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountId\":\"Id-122\",\"balance\":1000}")).andExpect(status().isCreated());
        Account account122 = accountsService.getAccount("Id-122");
        assertThat(account122.getAccountId()).isEqualTo("Id-122");


        this.mockMvc.perform(post("/v1/accounts/transferMoney").contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountFrom\":\"Id-123\",\"accountTo\":\"Id-122\",\"amount\":500}")).
                andExpect(status().isCreated());

        Account accountFrom = transactionService.getAccount("Id-123");
        assertThat(accountFrom.getAccountId()).isEqualTo("Id-123");
        assertThat(accountFrom.getBalance()).isEqualByComparingTo("500");

        Account accountTo = transactionService.getAccount("Id-122");
        assertThat(accountTo.getAccountId()).isEqualTo("Id-122");
        assertThat(accountTo.getBalance()).isEqualByComparingTo("1500");
    }

    @Test
    public void transferMoneyWithZeroBalance() throws Exception{
        this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountId\":\"Id-124\",\"balance\":0}")).andExpect(status().isCreated());
        Account account123 = accountsService.getAccount("Id-124");
        assertThat(account123.getAccountId()).isEqualTo("Id-124");

        this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountId\":\"Id-125\",\"balance\":1000}")).andExpect(status().isCreated());
        Account account122 = accountsService.getAccount("Id-125");
        assertThat(account122.getAccountId()).isEqualTo("Id-125");

        this.mockMvc.perform(post("/v1/accounts/transferMoney").contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountFrom\":\"Id-124\",\"accountTo\":\"Id-125\",\"amount\":500}")).
                andExpect(status().isPaymentRequired());
    }

    @Test
    public void transferMoneyWithNoAmount() throws Exception{
        this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountId\":\"Id-118\",\"balance\":1000}")).andExpect(status().isCreated());
        Account account123 = accountsService.getAccount("Id-118");
        assertThat(account123.getAccountId()).isEqualTo("Id-118");

        this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountId\":\"Id-119\",\"balance\":1000}")).andExpect(status().isCreated());
        Account account122 = accountsService.getAccount("Id-119");
        assertThat(account122.getAccountId()).isEqualTo("Id-119");

        this.mockMvc.perform(post("/v1/accounts/transferMoney").contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountFrom\":\"Id-118\",\"accountTo\":\"Id-119\",\"amount\":0}")).
                andExpect(status().isBadRequest());
    }

    @Test
    public void transferMoneyToInvalidAccount() throws Exception{
        this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountId\":\"Id-120\",\"balance\":1000}")).andExpect(status().isCreated());
        Account account123 = accountsService.getAccount("Id-120");
        assertThat(account123.getAccountId()).isEqualTo("Id-120");

        this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountId\":\"Id-121\",\"balance\":1000}")).andExpect(status().isCreated());
        Account account122 = accountsService.getAccount("Id-121");
        assertThat(account122.getAccountId()).isEqualTo("Id-121");

        this.mockMvc.perform(post("/v1/accounts/transferMoney").contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountFrom\":\"Id-120\",\"accountTo\":\"Id-117\",\"amount\":500}")).
                andExpect(status().isBadRequest());
    }
}
