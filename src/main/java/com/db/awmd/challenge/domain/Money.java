package com.db.awmd.challenge.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class Money {

    @NotNull
    @NotEmpty
    private final String accountFrom;

    @NotNull
    @NotEmpty
    private final String accountTo;

    @NotNull
    @Min(value = 1, message = "amount must be greater than zero.")
    private BigDecimal amount;

    @JsonCreator
    public Money(@JsonProperty("accountFrom") String accountFrom,
                 @JsonProperty("accountTo") String accountTo,
                   @JsonProperty("amount") BigDecimal amount) {
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
        this.amount = amount;
    }
}
