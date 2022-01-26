package ru.drsanches.life_together.app.data.debts.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.hibernate.validator.constraints.NotEmpty;
import javax.validation.Valid;
import java.util.List;

public class SendMoneyDTO {

    @NotEmpty
    @Valid
    @Schema(required = true)
    private List<SendTransactionDTO> transactions;

    public List<SendTransactionDTO> getTransactions() {
        return transactions;
    }

    @Override
    public String toString() {
        return "SendMoneyDTO{" +
                "transactions=" + transactions +
                '}';
    }
}