package ru.drsanches.life_together.app.data.debts.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.drsanches.life_together.common.utils.GregorianCalendarConvertor;

public class TransactionDTO {

    @Schema(required = true)
    private String id;

    @Schema(required = true)
    private String userId;

    @Schema(required = true)
    private TransactionDTOType type;

    @Schema(required = true)
    private Integer amount;

    @Schema
    private String message;

    @Schema(required = true, description = GregorianCalendarConvertor.PATTERN)
    private String timestamp;

    public TransactionDTO() {}

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public TransactionDTOType getType() {
        return type;
    }

    public Integer getAmount() {
        return amount;
    }

    public String getMessage() {
        return message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setType(TransactionDTOType type) {
        this.type = type;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTimestamp(String timestamp) {
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