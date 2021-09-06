package ru.drsanches.life_together.app.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
@Api(description = "all debts operations")
public class DebtsController {

    @Autowired
    private DebtsWebService debtsWebService;

    @RequestMapping(path = "/send", method = RequestMethod.POST)
    @ApiOperation(value = "Sends money to other user(s)")
    @ApiImplicitParam(name = "Authorization", value = "Access token", paramType = "header", required = true)
    @ResponseStatus(HttpStatus.CREATED)
    public void sendMoney(@RequestBody SendMoneyDTO sendMoneyDTO) {
        debtsWebService.sendMoney(sendMoneyDTO);
    }

    @RequestMapping(path = "", method = RequestMethod.GET)
    @ApiOperation(value = "Returns total sent and received amounts")
    @ApiImplicitParam(name = "Authorization", value = "Access token", paramType = "header", required = true)
    public AmountsDTO getDebts() {
        return debtsWebService.getDebts();
    }

    @RequestMapping(path = "/history", method = RequestMethod.GET)
    @ApiOperation(value = "Returns transactions history")
    @ApiImplicitParam(name = "Authorization", value = "Access token", paramType = "header", required = true)
    public List<TransactionDTO> getHistory(@ApiParam("Pagination parameter. From which element to display (inclusive)")
                                           @RequestParam(required = false) Integer from,
                                           @ApiParam("Pagination parameter. By which element to display (not inclusive)")
                                           @RequestParam(required = false) Integer to) {
        return debtsWebService.getHistory(from, to);
    }

    @RequestMapping(path = "/cancel", method = RequestMethod.POST)
    @ApiOperation(value = "Cancels all debts from/to other user")
    @ApiImplicitParam(name = "Authorization", value = "Access token", paramType = "header", required = true)
    public void cancel(@RequestBody CancelDTO cancelDTO) {
        debtsWebService.cancel(cancelDTO);
    }
}