package ca.jrvs.apps.trading.service;

import ca.jrvs.apps.trading.dao.AccountDao;
import ca.jrvs.apps.trading.model.domain.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FundTransferService {

  private AccountDao accountDao;

  @Autowired
  public FundTransferService(AccountDao accountDao) {
    this.accountDao = accountDao;
  }

  /**
   * Deposit a fund to the account which is associated with the traderId
   * - validate user input
   * - account = accountDao.findByTraderId
   * - accountDao.updateAmountById
   *
   * @param traderId trader id
   * @param fund found amount (can't be 0)
   * @return updated Account object
   * @throws ca.jrvs.apps.trading.dao.ResourceNotFoundException if ticker is not found from IEX
   * @throws org.springframework.dao.DataAccessException if unable to retrieve data
   * @throws IllegalArgumentException for invalid input
   */
  public Account deposit(Integer traderId, Double fund) {
    if(fund <= 0){
      throw new RuntimeException("deposit fund should be greater than 0");
    }
    Account account = accountDao.findByTraderId(traderId);
    account.setAmount(account.getAmount() + fund);
    accountDao.updateAmount(account);
    return account;
  }

  /**
   * Withdraw a fund from the account which is associated with the traderId
   *
   * - validate user input
   * - account = accountDao.findByTraderId
   * - accountDao.updateAmountById
   *
   * @param traderId trader ID
   * @param fund amount can't be 0
   * @return updated Account object
   * @throws ca.jrvs.apps.trading.dao.ResourceNotFoundException if ticker is not found from IEX
   * @throws org.springframework.dao.DataAccessException if unable to retrieve data
   * @throws IllegalArgumentException for invalid input
   */
  public Account withdraw(Integer traderId, Double fund) {
    if(fund <= 0){
      throw new RuntimeException("withdraw fund should be greater than 0");
    }
    Account account = accountDao.findById(traderId);
    account.setAmount(account.getAmount() - fund);
    accountDao.updateAmount(account);
    return account;
    }
}
