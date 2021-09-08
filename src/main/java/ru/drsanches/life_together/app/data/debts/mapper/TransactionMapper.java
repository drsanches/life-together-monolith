package ru.drsanches.life_together.app.data.debts.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.drsanches.life_together.app.data.debts.dto.TransactionDTO;
import ru.drsanches.life_together.app.data.debts.dto.TransactionDTOType;
import ru.drsanches.life_together.app.data.debts.model.Transaction;
import ru.drsanches.life_together.common.token.TokenSupplier;
import ru.drsanches.life_together.common.utils.GregorianCalendarConvertor;

@Component
public class TransactionMapper {

    @Autowired
    private TokenSupplier tokenSupplier;

    public TransactionDTO convert(Transaction transaction) {
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setId(transaction.getId());
        transactionDTO.setUserId(transaction.getFromUserId().equals(tokenSupplier.get().getUserId()) ?
                transaction.getToUserId() : transaction.getFromUserId());
        transactionDTO.setType(convertTransactionType(transaction));
        transactionDTO.setAmount(transaction.getAmount());
        transactionDTO.setMessage(transaction.getMessage());
        transactionDTO.setTimestamp(GregorianCalendarConvertor.convert(transaction.getTimestamp()));
        return transactionDTO;
    }

    private TransactionDTOType convertTransactionType(Transaction transaction) {
        switch (transaction.getType()) {
            case CANCELED_BY_SENDER:
                return transaction.getFromUserId().equals(tokenSupplier.get().getUserId()) ?
                        TransactionDTOType.CANCELED_BY_CURRENT :
                        TransactionDTOType.CANCELED_BY_OTHER;
            case CANCELED_BY_RECIPIENT:
                return transaction.getToUserId().equals(tokenSupplier.get().getUserId()) ?
                        TransactionDTOType.CANCELED_BY_CURRENT :
                        TransactionDTOType.CANCELED_BY_OTHER;
            default:
                return transaction.getFromUserId().equals(tokenSupplier.get().getUserId()) ?
                        TransactionDTOType.OUTGOING : TransactionDTOType.INCOMING;
        }
    }
}