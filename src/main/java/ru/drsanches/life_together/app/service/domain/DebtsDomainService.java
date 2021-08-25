package ru.drsanches.life_together.app.service.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.drsanches.life_together.app.data.debts.model.Transaction;
import ru.drsanches.life_together.app.data.debts.repository.TransactionRepository;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DebtsDomainService {

    private final Logger LOG = LoggerFactory.getLogger(DebtsDomainService.class);

    @Autowired
    private TransactionRepository transactionRepository;

    public void saveTransaction(Transaction transaction) {
        transactionRepository.save(transaction);
        LOG.info("New transaction has been created: {}", transaction);
    }

    public void saveTransactions(Collection<Transaction> transactions) {
        transactionRepository.saveAll(transactions);
        LOG.info("New transactions have been created: {}", transactions);
    }

    public List<Transaction> getIncomingTransactions(String userId) {
        return transactionRepository.findByToUserId(userId);
    }

    public List<Transaction> getIncomingTransactions(String currentUserId, String userId) {
        return transactionRepository.findByFromUserIdAndToUserId(userId, currentUserId);
    }

    public List<Transaction> getOutgoingTransactions(String userId) {
        return transactionRepository.findByFromUserId(userId);
    }

    public List<Transaction> getOutgoingTransactions(String currentUserId, String userId) {
        return transactionRepository.findByFromUserIdAndToUserId(currentUserId, userId);
    }

    public List<Transaction> getAllTransactions(String userId) {
        List<Transaction> transactions = getIncomingTransactions(userId);
        transactions.addAll(getOutgoingTransactions(userId));
        return transactions.stream()
                .sorted((x, y) -> -x.getTimestamp().compareTo(y.getTimestamp()))
                .collect(Collectors.toList());
    }
}