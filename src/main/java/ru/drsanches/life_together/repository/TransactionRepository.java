package ru.drsanches.life_together.repository;

import org.springframework.data.repository.CrudRepository;
import ru.drsanches.life_together.data.debts.model.Transaction;
import java.util.Set;

public interface TransactionRepository extends CrudRepository<Transaction, String> {

    Set<Transaction> findByFromUserId(String fromUserId);

    Set<Transaction> findByToUserId(String toUserId);

    Set<Transaction> findByFromUserIdAndToUserId(String fromUserId, String toUserId);
}