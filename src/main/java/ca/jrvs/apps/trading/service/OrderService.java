package ca.jrvs.apps.trading.service;

import ca.jrvs.apps.trading.dao.AccountDao;
import ca.jrvs.apps.trading.dao.PositionDao;
import ca.jrvs.apps.trading.dao.QuoteDao;
import ca.jrvs.apps.trading.dao.SecurityOrderDao;
import ca.jrvs.apps.trading.model.domain.*;
import ca.jrvs.apps.trading.model.dto.MarketOrderDto;
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
    double ask_price = quote.getAskPrice();
    double bid_price = quote.getBidPrice();
    Account account = accountDao.findById(orderDto.getAccountId());
    Double balance = account.getAmount();
    Position position = positionDao.findBy_AccountId_Ticker(account.getId(),ticker);

    SecurityOrder securityOrder = new SecurityOrder();
    securityOrder.setAccountId(account.getId());
    securityOrder.setSize(size);
    securityOrder.setTicker(ticker);

    if(size > 0){
      //- buy order : check account balance
      securityOrder.setPrice(ask_price);
      if(balance < ask_price * size){
        securityOrder.setStatus("CANCELED");
        securityOrder.setNotes("insufficient fund");
        securityOrderDao.save(securityOrder);
      }else{
        account.setAmount(balance - ask_price*size);
        accountDao.updateAmount(account);
        position.setPosition(position.getPosition() + size);
        securityOrder.setStatus("FILLED");
        securityOrderDao.save(securityOrder);
      }

    }else{
      //- sell order: check position for the ticker/symbol
      securityOrder.setPrice(bid_price);
      if(position.getPosition() < size){
        securityOrder.setStatus("CANCELED");
        securityOrder.setNotes("insufficient position");
        securityOrderDao.save(securityOrder);
      }else{
        account.setAmount(balance + bid_price*size);
        accountDao.updateAmount(account);
        position.setPosition(position.getPosition() + size);
        securityOrder.setStatus("FILLED");
        securityOrderDao.save(securityOrder);
      }

    }

    return securityOrder;

  }

}
