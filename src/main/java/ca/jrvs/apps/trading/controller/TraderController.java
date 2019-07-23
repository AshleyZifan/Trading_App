package ca.jrvs.apps.trading.controller;

import ca.jrvs.apps.trading.dao.AccountDao;
import ca.jrvs.apps.trading.dao.TraderDao;
import ca.jrvs.apps.trading.model.domain.Account;
import ca.jrvs.apps.trading.model.domain.SecurityRow;
import ca.jrvs.apps.trading.model.domain.Trader;
import ca.jrvs.apps.trading.service.FundTransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/trader")
public class TraderController {

    private TraderDao traderDao;
    private AccountDao accountDao;
    private FundTransferService fundTransferService;

    @Autowired
    public TraderController(TraderDao traderDao, AccountDao accountDao, FundTransferService fundTransferService){
        this.traderDao = traderDao;
        this.accountDao = accountDao;
        this.fundTransferService = fundTransferService;

    }

    @GetMapping(path = "/traderId/{traderId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteTrader(@PathVariable Integer traderId){
        try {
            traderDao.deleteById(traderId);
        } catch (Exception e) {
            throw ResponseExceptionUtil.getResponseStatusException(e);
        }
    }

    @GetMapping(path = "/")
    @ResponseStatus(HttpStatus.OK)
    public void createTrader(@RequestBody Trader trader){
        try {
            traderDao.save(trader);
        } catch (Exception e) {
            throw ResponseExceptionUtil.getResponseStatusException(e);
        }
    }

    @GetMapping(path = "/firstname/{firstname}/lastname/{lastname}/dob/{dob}/country/{country}/email/{email}")
    @ResponseStatus(HttpStatus.OK)
    public void createTrader_Account(@PathVariable String firstName, String lastName, String dob, String country, String email){
        try {
            Trader trader = new Trader();
            trader.setFirstName(firstName);
            trader.setLastName(lastName);
            trader.setDob(dob);
            trader.setCountry(country);
            trader.setEmail(email);
            Trader trader_new = traderDao.save(trader);
            Account account = new Account();
            account.setTraderId(trader_new.getId());
            account.setAmount(0);
            accountDao.save(account);
        } catch (Exception e) {
            throw ResponseExceptionUtil.getResponseStatusException(e);
        }
    }


    @GetMapping(path = "/deposit/accountId/{accountId}/amount/{amount}")
    @ResponseStatus(HttpStatus.OK)
    public void depositFund(@PathVariable Integer accountId, Double amount){
        try {
            int traderId = accountDao.findById(accountId).getTraderId();
            fundTransferService.deposit(traderId, amount);
        } catch (Exception e) {
            throw ResponseExceptionUtil.getResponseStatusException(e);
        }
    }


    @GetMapping(path = "/withdraw/accountId/{accountId}/amount/{amount}")
    @ResponseStatus(HttpStatus.OK)
    public void withdrawFund(@PathVariable Integer accountId, Double amount){
        try {
            int traderId = accountDao.findById(accountId).getTraderId();
            fundTransferService.withdraw(traderId, amount);
        } catch (Exception e) {
            throw ResponseExceptionUtil.getResponseStatusException(e);
        }
    }
}
