package ru.drsanches.life_together.app.service.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.drsanches.life_together.app.data.debts.dto.AmountsDTO;
import ru.drsanches.life_together.app.data.debts.dto.SendMoneyDTO;
import ru.drsanches.life_together.app.data.debts.dto.TransactionDTO;
import ru.drsanches.life_together.app.data.debts.mapper.AmountsMapper;
import ru.drsanches.life_together.app.data.debts.mapper.TransactionMapper;
import ru.drsanches.life_together.app.data.debts.model.Transaction;
import ru.drsanches.life_together.app.service.validator.CancelUserIdValidator;
import ru.drsanches.life_together.app.service.validator.SendMoneyDtoValidator;
import ru.drsanches.life_together.app.service.domain.DebtsDomainService;
import ru.drsanches.life_together.exception.application.ApplicationException;
import ru.drsanches.life_together.app.service.utils.PaginationService;
import ru.drsanches.life_together.integration.token.TokenService;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class DebtsWebService {

    private final Logger LOG = LoggerFactory.getLogger(DebtsWebService.class);

    @Autowired
    private DebtsDomainService debtsDomainService;

    @Autowired
    private SendMoneyDtoValidator sendMoneyDtoValidator;

    @Autowired
    private CancelUserIdValidator cancelUserIdValidator;

    @Autowired
    private PaginationService<Transaction> paginationService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AmountsMapper amountsMapper;

    @Autowired
    private TransactionMapper transactionMapper;

    public void sendMoney(String token, SendMoneyDTO sendMoneyDTO) {
        String fromUserId = tokenService.getUserIdByAccessToken(token);
        sendMoneyDtoValidator.validate(fromUserId, sendMoneyDTO);
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
                        false,
                        new GregorianCalendar()
                ));
            }
        });
        debtsDomainService.saveTransactions(transactions);
    }

    public AmountsDTO getDebts(String token) {
        String userId = tokenService.getUserIdByAccessToken(token);
        List<Transaction> incomingTransactions = debtsDomainService.getIncomingTransactions(userId);
        List<Transaction> outgoingTransactions = debtsDomainService.getOutgoingTransactions(userId);
        return amountsMapper.convert(incomingTransactions, outgoingTransactions);
    }

    public List<TransactionDTO> getHistory(String token, Integer from, Integer to) {
        String userId = tokenService.getUserIdByAccessToken(token);
        List<Transaction> transactions = debtsDomainService.getAllTransactions(userId);
        return paginationService.pagination(transactions.stream(), from, to)
                .map(transaction -> transactionMapper.convert(transaction, userId))
                .collect(Collectors.toList());
    }

    public void cancel(String token, String userId) {
        String currentUserId = tokenService.getUserIdByAccessToken(token);
        cancelUserIdValidator.validate(currentUserId, userId);
        int total = calcTotalDebt(currentUserId, userId);
        if (total == 0) {
            throw new ApplicationException("There is no debts for this user");
        }
        Transaction transaction = new Transaction(
                UUID.randomUUID().toString(),
                total > 0 ? userId : currentUserId,
                total > 0 ? currentUserId : userId,
                Math.abs(total),
                "Debt has been canceled by user with id '" + currentUserId + "'",
                true,
                new GregorianCalendar()
        );
        debtsDomainService.saveTransaction(transaction);
        LOG.info("Transaction for cancel has been created: {}", transaction.toString());
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