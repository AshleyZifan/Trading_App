package ca.jrvs.apps.trading.service;

import ca.jrvs.apps.trading.dao.AccountDao;
import ca.jrvs.apps.trading.dao.PositionDao;
import ca.jrvs.apps.trading.dao.QuoteDao;
import ca.jrvs.apps.trading.dao.SecurityOrderDao;
import ca.jrvs.apps.trading.model.domain.*;
import ca.jrvs.apps.trading.model.dto.MarketOrderDto;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrderService {

  private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

  private AccountDao accountDao;
  private SecurityOrderDao securityOrderDao;
  private QuoteDao quoteDao;
  private PositionDao positionDao;

  @Autowired
  public OrderService(AccountDao accountDao, SecurityOrderDao securityOrderDao,
      QuoteDao quoteDao, PositionDao positionDao) {
    this.accountDao = accountDao;
    this.securityOrderDao = securityOrderDao;
    this.quoteDao = quoteDao;
    this.positionDao = positionDao;
  }

  /**
   * Execute a market order
   *
   * - validate the order (e.g. size, and ticker)
   * - Create a securityOrder (for security_order table)
   * - Handle buy or sell order
   *   - buy order : check account balance
   *   - sell order: check position for the ticker/symbol
   *   - (please don't forget to update securityOrder.status)
   * - Save and return securityOrder
   *
   * NOTE: you will need to some helper methods (protected or private)
   *
   * @param orderDto market order
   * @return SecurityOrder from security_order table
   * @throws org.springframework.dao.DataAccessException if unable to get data from DAO
   * @throws IllegalArgumentException for invalid input
   */
  public SecurityOrder executeMarketOrder(MarketOrderDto orderDto) {

    int size = orderDto.getSize();
    String ticker = orderDto.getTicker();
    Quote quote = quoteDao.findById(ticker);
    double price = quote.getBidPrice();
    Account account = accountDao.findById(orderDto.getAccountId());
    Double balance = account.getAmount();
    Position position = positionDao.findBy_AccountId_Ticker(account.getId(),ticker);
    //- buy order : check account balance
    if(size> 0){
      if (balance < price*size){
        throw new RuntimeException("balance not enough");
      }
    } else {
      //- sell order: check position for the ticker/symbol
      if(position.getPosition() < size){
        throw new RuntimeException("position not enough");
      }
    }
    account.setAmount(balance - price*size);
    accountDao.updateAmount(account);
    position.setPosition(position.getPosition() + size);
    //add position
    SecurityOrder securityOrder = new SecurityOrder();
    securityOrder.setAccountId(account.getId());
    securityOrder.setPrice(price);
    securityOrder.setSize(size);
    securityOrder.setTicker(ticker);
    return securityOrder;

  }

}
