package ru.drsanches.life_together.app.data.debts.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.hibernate.validator.constraints.NotEmpty;
import ru.drsanches.life_together.app.service.validation.annotation.ValidateCollection;
import java.util.List;

public class SendMoneyDTO {

    @NotEmpty
    @ValidateCollection(message = "transactions contains invalid objects")
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