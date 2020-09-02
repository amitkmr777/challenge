package com.db.awmd.challenge.web;

import com.db.awmd.challenge.domain.Money;
import com.db.awmd.challenge.exception.InvalidAccountException;
import com.db.awmd.challenge.exception.InvalidAmountException;
import com.db.awmd.challenge.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;

@RestController
@RequestMapping("/v1/accounts")
@Slf4j
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService){
        this.transactionService = transactionService;
    }

    @PostMapping(value= "/transferMoney",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> transferMoney(@RequestBody @Valid Money money) {
        log.info("transaction money {}", money);

        try {
            this.transactionService.transferMoney(money);

        } catch (InvalidAccountException inve) {
            return new ResponseEntity<>(inve.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (InvalidAmountException invamoe){
            return new ResponseEntity<>(invamoe.getMessage(), HttpStatus.PAYMENT_REQUIRED);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
