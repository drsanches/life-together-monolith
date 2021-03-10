package ru.drsanches.life_together.data.debts.dto;

import ru.drsanches.life_together.data.debts.enumeration.TransactionType;
import java.util.GregorianCalendar;

public class TransactionDTO {

    private String id;

    private String userId;

    private TransactionType type;

    private Integer amount;

    private String message;

    private GregorianCalendar timestamp;

    public TransactionDTO() {}

    public TransactionDTO(String id, String userId, TransactionType type, Integer amount, String message, GregorianCalendar timestamp) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.amount = amount;
        this.message = message;
        this.timestamp = timestamp;
    }

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
                ", timestamp=" + timestamp +
                '}';
    }
}