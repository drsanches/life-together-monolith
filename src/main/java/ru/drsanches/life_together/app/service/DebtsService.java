package ru.drsanches.life_together.app.service;

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
import ru.drsanches.life_together.app.service.domain.UserProfileDomainService;
import ru.drsanches.life_together.exception.application.ApplicationException;
import ru.drsanches.life_together.exception.application.NoUserIdException;
import ru.drsanches.life_together.exception.application.WrongRecipientsException;
import ru.drsanches.life_together.app.data.debts.repository.TransactionRepository;
import ru.drsanches.life_together.app.service.utils.PaginationService;
import ru.drsanches.life_together.app.service.utils.RecipientsValidator;
import ru.drsanches.life_together.integration.token.TokenService;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class DebtsService {

    private final Logger LOG = LoggerFactory.getLogger(DebtsService.class);

    @Autowired
    private TransactionRepository transactionRepository;

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
        Set<String> wrongIds = recipientsValidator.getWrongIds(fromUserId, sendMoneyDTO.getToUserIds());
        if (!wrongIds.isEmpty()) {
            throw new WrongRecipientsException(wrongIds);
        }
        int money = sendMoneyDTO.getMoney() / sendMoneyDTO.getToUserIds().size();
        Set<Transaction> transactions = new HashSet<>();
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
        transactionRepository.saveAll(transactions);
        LOG.info("Transactions has been created: {}", transactions.toString());
    }

    public AmountsDTO getDebts(String token) {
        String userId = tokenService.getUserIdByAccessToken(token);
        List<Transaction> incomingTransactions = transactionRepository.findByToUserId(userId);
        List<Transaction> outgoingTransactions = transactionRepository.findByFromUserId(userId);
        return amountsMapper.convert(incomingTransactions, outgoingTransactions);
    }

    public List<TransactionDTO> getHistory(String token, Integer from, Integer to) {
        String userId = tokenService.getUserIdByAccessToken(token);
        List<Transaction> transactions = transactionRepository.findByFromUserId(userId);
        transactions.addAll(transactionRepository.findByToUserId(userId));
        Stream<Transaction> sorted = transactions.stream()
                .sorted((x, y) -> -x.getTimestamp().compareTo(y.getTimestamp()));
        return paginationService.pagination(sorted, from, to)
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

        List<Transaction> outgoingTransactions = transactionRepository.findByFromUserIdAndToUserId(currentUserId, userId);
        outgoingTransactions.forEach(transaction -> total.updateAndGet(v -> v + transaction.getAmount()));

        List<Transaction> incomingTransactions = transactionRepository.findByFromUserIdAndToUserId(userId, currentUserId);
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
            transactionRepository.save(transaction);
            LOG.info("Transaction for cancel has been created: {}", transaction.toString());
        } else {
            throw new ApplicationException("There is no debts for this user");
        }
    }
}