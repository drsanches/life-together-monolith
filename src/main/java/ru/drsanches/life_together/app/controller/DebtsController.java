package ru.drsanches.life_together.app.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.drsanches.life_together.app.data.debts.dto.AmountsDTO;
import ru.drsanches.life_together.app.data.debts.dto.SendMoneyDTO;
import ru.drsanches.life_together.app.data.debts.dto.TransactionDTO;
import ru.drsanches.life_together.app.service.web.DebtsWebService;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/debts")
@Api(description = "all debts operations")
public class DebtsController {

    @Autowired
    private DebtsWebService debtsWebService;

    @RequestMapping(path = "/send", method = RequestMethod.POST)
    @ApiOperation(value = "Sends money to other user(s)")
    @ResponseStatus(HttpStatus.CREATED)
    public void sendMoney(@RequestHeader("Authorization") String token, @RequestBody SendMoneyDTO sendMoneyDTO) {
        debtsWebService.sendMoney(token, sendMoneyDTO);
    }

    @RequestMapping(path = "", method = RequestMethod.GET)
    @ApiOperation(value = "Returns total sent and received amounts")
    public AmountsDTO getDebts(@RequestHeader("Authorization") String token) {
        return debtsWebService.getDebts(token);
    }

    @RequestMapping(path = "/history", method = RequestMethod.GET)
    @ApiOperation(value = "Returns transactions history")
    public List<TransactionDTO> getHistory(@RequestHeader("Authorization") String token,
                                           @ApiParam("Pagination parameter. From which element to display (inclusive)")
                                           @RequestParam(required = false) Integer from,
                                           @ApiParam("Pagination parameter. By which element to display (not inclusive)")
                                           @RequestParam(required = false) Integer to) {
        return debtsWebService.getHistory(token, from, to);
    }

    @RequestMapping(path = "/cancel/{userId}", method = RequestMethod.GET)
    @ApiOperation(value = "Cancels all debts from/to other user")
    public void cancel(@RequestHeader("Authorization") String token, @PathVariable String userId) {
        debtsWebService.cancel(token, userId);
    }
}