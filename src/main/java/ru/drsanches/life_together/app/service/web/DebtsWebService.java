package ru.drsanches.life_together.app.service.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.drsanches.life_together.app.data.debts.dto.AmountDTO;
import ru.drsanches.life_together.app.data.debts.dto.AmountsDTO;
import ru.drsanches.life_together.app.data.debts.dto.CancelDTO;
import ru.drsanches.life_together.app.data.debts.dto.SendMoneyDTO;
import ru.drsanches.life_together.app.data.debts.dto.TransactionDTO;
import ru.drsanches.life_together.app.data.debts.mapper.TransactionMapper;
import ru.drsanches.life_together.app.data.debts.model.Transaction;
import ru.drsanches.life_together.app.data.debts.model.TransactionType;
import ru.drsanches.life_together.app.service.domain.DebtsDomainService;
import ru.drsanches.life_together.exception.application.ApplicationException;
import ru.drsanches.life_together.app.service.utils.PaginationService;
import ru.drsanches.life_together.common.token.TokenSupplier;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@Validated
public class DebtsWebService {

    private final Logger LOG = LoggerFactory.getLogger(DebtsWebService.class);

    @Autowired
    private DebtsDomainService debtsDomainService;

    @Autowired
    private PaginationService<Transaction> paginationService;

    @Autowired
    private TokenSupplier tokenSupplier;

    @Autowired
    private TransactionMapper transactionMapper;

    public void sendMoney(@Valid SendMoneyDTO sendMoneyDTO) {
        String fromUserId = tokenSupplier.get().getUserId();
        int money = sendMoneyDTO.getMoney() / sendMoneyDTO.getToUserIds().size();
        List<Transaction> transactions = new LinkedList<>();
        sendMoneyDTO.getToUserIds().forEach(toUserId -> {
            if (!fromUserId.equals(toUserId)) {
                transactions.add(new Transaction(
                        UUID.randomUUID().toString(),
                        fromUserId,
                        toUserId,
                        money,
                        sendMoneyDTO.getMessage(),
                        TransactionType.TRANSACTION,
                        new GregorianCalendar()
                ));
            }
        });
        debtsDomainService.saveTransactions(transactions);
        LOG.info("User with id '{}' send {} money to users {}", fromUserId, money, sendMoneyDTO.getToUserIds());
    }

    public AmountsDTO getDebts() {
        String userId = tokenSupplier.get().getUserId();
        List<Transaction> incomingTransactions = debtsDomainService.getIncomingTransactions(userId);
        List<Transaction> outgoingTransactions = debtsDomainService.getOutgoingTransactions(userId);
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
        return new AmountsDTO(sent, received);
    }

    public List<TransactionDTO> getHistory(Integer from, Integer to) {
        String userId = tokenSupplier.get().getUserId();
        List<Transaction> transactions = debtsDomainService.getAllTransactions(userId);
        return paginationService.pagination(transactions.stream(), from, to)
                .map(transaction -> transactionMapper.convert(transaction))
                .collect(Collectors.toList());
    }

    public void cancel(@Valid CancelDTO cancelDTO) {
        String currentUserId = tokenSupplier.get().getUserId();
        int total = calcTotalDebt(currentUserId, cancelDTO.getUserId());
        if (total == 0) {
            throw new ApplicationException("There is no debts for this user");
        }
        Transaction transaction = new Transaction(
                UUID.randomUUID().toString(),
                total > 0 ? cancelDTO.getUserId() : currentUserId,
                total > 0 ? currentUserId : cancelDTO.getUserId(),
                Math.abs(total),
                cancelDTO.getMessage(),
                total > 0 ? TransactionType.CANCELED_BY_RECIPIENT: TransactionType.CANCELED_BY_SENDER,
                new GregorianCalendar()
        );
        debtsDomainService.saveTransaction(transaction);
        LOG.info("User with id '{}' canceled debts for user '{}'", currentUserId, cancelDTO.getUserId());
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

    private int calcTotalDebt(String currentUserId, String userId) {
        AtomicReference<Integer> total = new AtomicReference<>(0);
        List<Transaction> outgoingTransactions = debtsDomainService.getOutgoingTransactions(currentUserId, userId);
        outgoingTransactions.forEach(transaction -> total.updateAndGet(v -> v + transaction.getAmount()));
        List<Transaction> incomingTransactions = debtsDomainService.getIncomingTransactions(currentUserId, userId);
        incomingTransactions.forEach(transaction -> total.updateAndGet(v -> v - transaction.getAmount()));
        return total.get();
    }
}