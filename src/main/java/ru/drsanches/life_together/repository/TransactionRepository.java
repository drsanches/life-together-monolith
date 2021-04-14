package ru.drsanches.life_together.repository;

import org.springframework.data.repository.CrudRepository;
import ru.drsanches.life_together.data.debts.model.Transaction;
import java.util.List;

public interface TransactionRepository extends CrudRepository<Transaction, String> {

    List<Transaction> findByFromUserId(String fromUserId);

    List<Transaction> findByToUserId(String toUserId);

    List<Transaction> findByFromUserIdAndToUserId(String fromUserId, String toUserId);
}