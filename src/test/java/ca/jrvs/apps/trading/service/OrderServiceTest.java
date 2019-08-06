package ca.jrvs.apps.trading.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import ca.jrvs.apps.trading.dao.AccountDao;
import ca.jrvs.apps.trading.dao.PositionDao;
import ca.jrvs.apps.trading.dao.QuoteDao;
import ca.jrvs.apps.trading.dao.SecurityOrderDao;
import ca.jrvs.apps.trading.model.domain.Account;
import ca.jrvs.apps.trading.model.domain.Quote;
import ca.jrvs.apps.trading.model.domain.SecurityOrder;
import ca.jrvs.apps.trading.model.dto.MarketOrderDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OrderServiceTest {

  //capture parameter when calling securityOrderDao.save
  @Captor
  ArgumentCaptor<SecurityOrder> captorSecurityOrder;
  
  //mock all dependencies
  @Mock
  private AccountDao accountDao;
  @Mock
  private SecurityOrderDao securityOrderDao;
  @Mock
  private QuoteDao quoteDao;
  @Mock
  private PositionDao positionDao;
  
  //injecting mocked dependencies to the testing class via constructor
  @InjectMocks
  private OrderService orderService;
  
  //setup test data
  private MarketOrderDto orderDto;

  @Before
  public void setup() {
    orderDto = new MarketOrderDto();
    orderDto.setAccountId(1);
    orderDto.setSize(1);
    orderDto.setTicker("AAPL");
  }

  @Test
  public void executeMarketOrderHappyPath() {
    when(quoteDao.existsById(orderDto.getTicker())).thenReturn(true);
    when(accountDao.existsById(orderDto.getAccountId())).thenReturn(true);
    Quote quote = new Quote();
    quote.setAskSize(10);
    quote.setAskPrice(100.00);
    when(quoteDao.findById(orderDto.getTicker())).thenReturn(quote);

    Account account = new Account();
    account.setAmount(100.00);
    account.setId(orderDto.getAccountId());
    //accountDao.findByIdForUpdate equivalents accountDao.findById
    when(accountDao.findById(orderDto.getAccountId())).thenReturn(account);

    orderService.executeMarketOrder(orderDto);
    verify(securityOrderDao).save(captorSecurityOrder.capture());
    SecurityOrder captorOrder = captorSecurityOrder.getValue();
    assertEquals("FILLED", captorOrder.getStatus());
  }
  
  @Test
  public void executeMarketOrderSadPath() {
    when(quoteDao.existsById(orderDto.getTicker())).thenReturn(true);
    when(accountDao.existsById(orderDto.getAccountId())).thenReturn(true);
    Quote quote = new Quote();
    quote.setAskSize(10);
    quote.setAskPrice(100.00);
    when(quoteDao.findById(orderDto.getTicker())).thenReturn(quote);

    Account account = new Account();
    account.setAmount(1.00);
    account.setId(orderDto.getAccountId());
    //accountDao.findByIdForUpdate equivalents accountDao.findById
    when(accountDao.findById(orderDto.getAccountId())).thenReturn(account);

    orderService.executeMarketOrder(orderDto);
    verify(securityOrderDao).save(captorSecurityOrder.capture());
    SecurityOrder captorOrder = captorSecurityOrder.getValue();
    assertEquals("CANCELED", captorOrder.getStatus());
    
  }
  
}