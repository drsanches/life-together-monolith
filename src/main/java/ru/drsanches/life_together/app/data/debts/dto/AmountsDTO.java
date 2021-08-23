package ru.drsanches.life_together.app.data.debts.dto;

import java.util.List;

public class AmountsDTO {

    private List<AmountDTO> sent;

    private List<AmountDTO> received;

    public AmountsDTO() {}

    public List<AmountDTO> getSent() {
        return sent;
    }

    public List<AmountDTO> getReceived() {
        return received;
    }

    public void setSent(List<AmountDTO> sent) {
        this.sent = sent;
    }

    public void setReceived(List<AmountDTO> received) {
        this.received = received;
    }

    @Override
    public String toString() {
        return "AmountsDTO{" +
                "sent=" + sent +
                ", received=" + received +
                '}';
    }
}