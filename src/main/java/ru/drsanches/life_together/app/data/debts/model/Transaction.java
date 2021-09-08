package ru.drsanches.life_together.app.data.debts.model;

import ru.drsanches.life_together.common.utils.GregorianCalendarConvertor;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.GregorianCalendar;

@Entity
@Table(name="transaction")
public class Transaction {

    @Id
    @Column
    private String id;

    @Column(nullable = false)
    private String fromUserId;

    @Column(nullable = false)
    private String toUserId;

    @Column(nullable = false)
    private Integer amount;

    @Column
    private String message;

    @Column(nullable = false)
    private TransactionType type;

    @Column(nullable = false)
    private GregorianCalendar timestamp;

    public Transaction() {}

    public Transaction(String id, String fromUserId, String toUserId, Integer amount, String message, TransactionType type, GregorianCalendar timestamp) {
        this.id = id;
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.amount = amount;
        this.message = message;
        this.type = type;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public String getToUserId() {
        return toUserId;
    }

    public Integer getAmount() {
        return amount;
    }

    public String getMessage() {
        return message;
    }

    public TransactionType getType() {
        return type;
    }

    public GregorianCalendar getTimestamp() {
        return timestamp;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public void setTimestamp(GregorianCalendar timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + id + '\'' +
                ", fromUserId='" + fromUserId + '\'' +
                ", toUserId='" + toUserId + '\'' +
                ", amount=" + amount +
                ", message='" + message + '\'' +
                ", type=" + type +
                ", timestamp=" + GregorianCalendarConvertor.convert(timestamp) +
                '}';
    }
}