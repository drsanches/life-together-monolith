package ru.drsanches.life_together.app.data.debts.dto;

import io.swagger.annotations.ApiModelProperty;

public class AmountDTO {

    @ApiModelProperty(required = true)
    private String userId;

    @ApiModelProperty(required = true)
    private Integer amount;

    public AmountDTO() {}

    public AmountDTO(String userId, Integer amount) {
        this.userId = userId;
        this.amount = amount;
    }

    public String getUserId() {
        return userId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "AmountDTO{" +
                "userId='" + userId + '\'' +
                ", amount=" + amount +
                '}';
    }
}