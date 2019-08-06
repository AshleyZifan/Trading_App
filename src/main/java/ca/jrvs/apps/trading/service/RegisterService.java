package ca.jrvs.apps.trading.service;

import ca.jrvs.apps.trading.dao.AccountDao;
import ca.jrvs.apps.trading.dao.PositionDao;
import ca.jrvs.apps.trading.dao.SecurityOrderDao;
import ca.jrvs.apps.trading.dao.TraderDao;
import ca.jrvs.apps.trading.model.domain.Account;
import ca.jrvs.apps.trading.model.domain.Position;
import ca.jrvs.apps.trading.model.domain.Trader;
import ca.jrvs.apps.trading.model.view.TraderAccountView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegisterService {

  private TraderDao traderDao;
  private AccountDao accountDao;
  private PositionDao positionDao;
  private SecurityOrderDao securityOrderDao;

  @Autowired
  public RegisterService(TraderDao traderDao, AccountDao accountDao,
      PositionDao positionDao, SecurityOrderDao securityOrderDao) {
    this.traderDao = traderDao;
    this.accountDao = accountDao;
    this.positionDao = positionDao;
    this.securityOrderDao = securityOrderDao;
  }

  /**
   * Create a new trader and initialize a new account with 0 amount.
   * - validate user input (all fields must be non empty)
   * - create a trader
   * - create an account
   * - create, setup, and return a new traderAccountView
   *
   * @param trader trader info
   * @return traderAccountView
   * @throws ca.jrvs.apps.trading.dao.ResourceNotFoundException if ticker is not found from IEX
   * @throws org.springframework.dao.DataAccessException if unable to retrieve data
   * @throws IllegalArgumentException for invalid input
   */
  public TraderAccountView createTraderAndAccount(Trader trader) {
    //create a trader
    Trader trader_new = traderDao.save(trader);
    //create an account
    Account account = new Account();
    account.setTraderId(trader_new.getId());
    account.setAmount(0);
    accountDao.save(account);
    //create, setup, and return a new traderAccountView
    TraderAccountView view = new TraderAccountView();
    view.setTrader(trader_new);
    view.setAccount(account);
    return view;
  }

  /**
   * A trader can be deleted iff no open position and no cash balance.
   * - validate traderID
   * - get trader account by traderId and check account balance
   * - get positions by accountId and check positions
   * - delete all securityOrders, account, trader (in this order)
   *
   * @param traderId
   * @throws ca.jrvs.apps.trading.dao.ResourceNotFoundException if ticker is not found from IEX
   * @throws org.springframework.dao.DataAccessException if unable to retrieve data
   * @throws IllegalArgumentException for invalid input
   */
  public void deleteTraderById(Integer traderId) {
    //validate
    Account account = accountDao.findByTraderId(traderId);
    if(account.getAmount()!= 0){
      throw new RuntimeException("The trader has cash balance");
    }else if(!isZeroPosition(positionDao.findByAccountId(account.getId()))){
      throw new RuntimeException("Has open position");
    }else{
      //- delete all securityOrders
      securityOrderDao.deleteByAccountId(account.getId());
      // delete all account
      accountDao.deleteByTraderId(traderId);
      // delete all trader
      traderDao.deleteById(traderId);
  }
  }

  //helper function to check if the position sum is 0 or not
  public boolean isZeroPosition(List<Position> positions){
    int sum = 0;
    for(Position p : positions){
      sum += p.getPosition();
    }

    if(sum == 0){
      return true;
    }else{
      return false;
    }
  }

}
