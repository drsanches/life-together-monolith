package ru.drsanches.life_together.app.data.debts.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public class AmountsDTO {

    @Schema
    private List<AmountDTO> sent;

    @Schema
    private List<AmountDTO> received;

    public AmountsDTO() {}

    public AmountsDTO(List<AmountDTO> sent, List<AmountDTO> received) {
        this.sent = sent;
        this.received = received;
    }

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