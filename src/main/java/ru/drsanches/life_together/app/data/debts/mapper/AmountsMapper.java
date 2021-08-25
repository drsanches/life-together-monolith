package ru.drsanches.life_together.app.data.debts.mapper;

import org.springframework.stereotype.Component;
import ru.drsanches.life_together.app.data.debts.dto.AmountDTO;
import ru.drsanches.life_together.app.data.debts.dto.AmountsDTO;
import ru.drsanches.life_together.app.data.debts.model.Transaction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AmountsMapper {

    public AmountsDTO convert(List<Transaction> incomingTransactions, List<Transaction> outgoingTransactions) {
        Map<String, Integer> total = calcTotalDebts(incomingTransactions, outgoingTransactions);
        List<AmountDTO> sent = new ArrayList<>();
        List<AmountDTO> received = new ArrayList<>();
        total.forEach((id, money) -> {
            if (money > 0) {
                sent.add(new AmountDTO(id, money));
            } else if (money < 0) {
                received.add(new AmountDTO(id, Math.abs(money)));
            }
        });
        AmountsDTO amountsDTO = new AmountsDTO();
        amountsDTO.setSent(sent);
        amountsDTO.setReceived(received);
        return amountsDTO;
    }

    private Map<String, Integer> calcTotalDebts(List<Transaction> incomingTransactions, List<Transaction> outgoingTransactions) {
        Map<String, Integer> total = new HashMap<>();
        outgoingTransactions.forEach(transaction -> {
            String toUserId = transaction.getToUserId();
            if (total.containsKey(toUserId)) {
                total.put(toUserId, total.get(toUserId) + transaction.getAmount());
            } else {
                total.put(toUserId, transaction.getAmount());
            }
        });
        incomingTransactions.forEach(transaction -> {
            String fromUserId = transaction.getFromUserId();
            if (total.containsKey(fromUserId)) {
                total.put(fromUserId, total.get(fromUserId) - transaction.getAmount());
            } else {
                total.put(fromUserId, -1 * transaction.getAmount());
            }
        });
        return total;
    }
}