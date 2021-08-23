package ru.drsanches.life_together.app.data.debts.mapper;

import org.springframework.stereotype.Component;
import ru.drsanches.life_together.app.data.debts.dto.TransactionDTO;
import ru.drsanches.life_together.app.data.debts.enumeration.TransactionType;
import ru.drsanches.life_together.app.data.debts.model.Transaction;

@Component
public class TransactionMapper {

    public TransactionDTO convert(Transaction transaction, String currentUserId) {
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setId(transaction.getId());
        transactionDTO.setUserId(transaction.getFromUserId().equals(currentUserId) ?
                transaction.getToUserId() : transaction.getFromUserId());
        transactionDTO.setType(transaction.isSystem() ?
                TransactionType.SYSTEM :
                transaction.getFromUserId().equals(currentUserId) ?
                        TransactionType.OUTGOING : TransactionType.INCOMING);
        transactionDTO.setAmount(transaction.getAmount());
        transactionDTO.setMessage(transaction.getMessage());
        transactionDTO.setTimestamp(transaction.getTimestamp());
        return transactionDTO;
    }
}