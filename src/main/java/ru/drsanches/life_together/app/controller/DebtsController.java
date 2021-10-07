package ru.drsanches.life_together.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.drsanches.life_together.app.data.debts.dto.AmountsDTO;
import ru.drsanches.life_together.app.data.debts.dto.CancelDTO;
import ru.drsanches.life_together.app.data.debts.dto.SendMoneyDTO;
import ru.drsanches.life_together.app.data.debts.dto.TransactionDTO;
import ru.drsanches.life_together.app.service.web.DebtsWebService;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/debts")
public class DebtsController {

    @Autowired
    private DebtsWebService debtsWebService;

    @RequestMapping(path = "/send", method = RequestMethod.POST)
    @Operation(summary = "Sends money to other user(s)")
    @Parameter(name = "Authorization", description = "Access token", in = ParameterIn.HEADER, required = true)
    @ResponseStatus(HttpStatus.CREATED)
    public void sendMoney(@RequestBody SendMoneyDTO sendMoneyDTO) {
        debtsWebService.sendMoney(sendMoneyDTO);
    }

    @RequestMapping(path = "", method = RequestMethod.GET)
    @Operation(summary = "Returns total sent and received amounts")
    @Parameter(name = "Authorization", description = "Access token", in = ParameterIn.HEADER, required = true)
    public AmountsDTO getDebts() {
        return debtsWebService.getDebts();
    }

    @RequestMapping(path = "/history", method = RequestMethod.GET)
    @Operation(summary = "Returns transactions history")
    @Parameter(name = "Authorization", description = "Access token", in = ParameterIn.HEADER, required = true)
    public List<TransactionDTO> getHistory(@Parameter(description = "Pagination parameter. From which element to display (inclusive)")
                                           @RequestParam(required = false) Integer from,
                                           @Parameter(description = "Pagination parameter. By which element to display (not inclusive)")
                                           @RequestParam(required = false) Integer to) {
        return debtsWebService.getHistory(from, to);
    }

    @RequestMapping(path = "/cancel", method = RequestMethod.POST)
    @Operation(summary = "Cancels all debts from/to other user")
    @Parameter(name = "Authorization", description = "Access token", in = ParameterIn.HEADER, required = true)
    public void cancel(@RequestBody CancelDTO cancelDTO) {
        debtsWebService.cancel(cancelDTO);
    }
}