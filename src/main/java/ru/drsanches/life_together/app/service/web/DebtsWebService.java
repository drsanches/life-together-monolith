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
import ru.drsanches.life_together.app.service.domain.DebtsDomainService;
import ru.drsanches.life_together.app.service.domain.UserProfileDomainService;
import ru.drsanches.life_together.exception.application.ApplicationException;
import ru.drsanches.life_together.exception.application.NoUserIdException;
import ru.drsanches.life_together.exception.application.WrongRecipientsException;
import ru.drsanches.life_together.app.service.utils.PaginationService;
import ru.drsanches.life_together.app.service.utils.RecipientsValidator;
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
    private UserProfileDomainService userProfileDomainService;

    @Autowired
    private RecipientsValidator recipientsValidator;

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
        if (sendMoneyDTO.getMoney() == null || sendMoneyDTO.getMoney() <= 0) {
            throw new ApplicationException("Money must be positive: money=" + sendMoneyDTO.getMoney());
        }
        if (sendMoneyDTO.getToUserIds() == null || sendMoneyDTO.getToUserIds().isEmpty()) {
            throw new ApplicationException("User id list is empty");
        }
        List<String> wrongIds = recipientsValidator.getWrongIds(fromUserId, sendMoneyDTO.getToUserIds());
        if (!wrongIds.isEmpty()) {
            throw new WrongRecipientsException(wrongIds);
        }
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
        LOG.info("Transactions has been created: {}", transactions.toString());
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
        if (!userProfileDomainService.anyExistsById(userId)) {
            throw new NoUserIdException(userId);
        }
        if (currentUserId.equals(userId)) {
            throw new ApplicationException("You can't cancel debt for yourself");
        }

        AtomicReference<Integer> total = new AtomicReference<>(0);

        List<Transaction> outgoingTransactions = debtsDomainService.getOutgoingTransactions(currentUserId, userId);
        outgoingTransactions.forEach(transaction -> total.updateAndGet(v -> v + transaction.getAmount()));

        List<Transaction> incomingTransactions = debtsDomainService.getIncomingTransactions(currentUserId, userId);
        incomingTransactions.forEach(transaction -> total.updateAndGet(v -> v - transaction.getAmount()));

        if (total.get() != 0) {
            Transaction transaction = new Transaction();
            transaction.setId(UUID.randomUUID().toString());
            transaction.setAmount(Math.abs(total.get()));
            transaction.setSystem(true);
            transaction.setMessage("Debt has been canceled by user with id '" + currentUserId + "'");
            transaction.setTimestamp(new GregorianCalendar());
            if (total.get() > 0) {
                transaction.setFromUserId(userId);
                transaction.setToUserId(currentUserId);
            } else {
                transaction.setFromUserId(currentUserId);
                transaction.setToUserId(userId);
            }
            debtsDomainService.saveTransaction(transaction);
            LOG.info("Transaction for cancel has been created: {}", transaction.toString());
        } else {
            throw new ApplicationException("There is no debts for this user");
        }
    }
}