package ru.drsanches.life_together.app.data.debts.dto;

import ru.drsanches.life_together.app.data.debts.enumeration.TransactionType;
import java.time.format.DateTimeFormatter;
import java.util.GregorianCalendar;

public class TransactionDTO {

    private String id;

    private String userId;

    private TransactionType type;

    private Integer amount;

    private String message;

    private GregorianCalendar timestamp;

    public TransactionDTO() {}

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public TransactionType getType() {
        return type;
    }

    public Integer getAmount() {
        return amount;
    }

    public String getMessage() {
        return message;
    }

    public GregorianCalendar getTimestamp() {
        return timestamp;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTimestamp(GregorianCalendar timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "TransactionDTO{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", type=" + type +
                ", amount=" + amount +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp.toZonedDateTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss.SSS z")) +
                '}';
    }
}