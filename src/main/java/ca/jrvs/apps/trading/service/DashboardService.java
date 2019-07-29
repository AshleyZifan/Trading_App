package ca.jrvs.apps.trading.service;

import ca.jrvs.apps.trading.dao.AccountDao;
import ca.jrvs.apps.trading.dao.PositionDao;
import ca.jrvs.apps.trading.dao.QuoteDao;
import ca.jrvs.apps.trading.dao.TraderDao;
import ca.jrvs.apps.trading.model.domain.Account;
import ca.jrvs.apps.trading.model.domain.Position;
import ca.jrvs.apps.trading.model.domain.SecurityRow;
import ca.jrvs.apps.trading.model.domain.Trader;
import ca.jrvs.apps.trading.model.view.PortfolioView;
import ca.jrvs.apps.trading.model.view.TraderAccountView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional
public class DashboardService {

  private TraderDao traderDao;
  private PositionDao positionDao;
  private AccountDao accountDao;
  private QuoteDao quoteDao;

  @Autowired
  public DashboardService(TraderDao traderDao, PositionDao positionDao, AccountDao accountDao,
                          QuoteDao quoteDao) {
    this.traderDao = traderDao;
    this.positionDao = positionDao;
    this.accountDao = accountDao;
    this.quoteDao = quoteDao;
  }

  /**
   * Create and return a traderAccountView by trader ID
   * - get trader account by id
   * - get trader info by id
   * - create and return a traderAccountView
   *
   * @param traderId trader ID
   * @return traderAccountView
   * @throws ca.jrvs.apps.trading.dao.ResourceNotFoundException if ticker is not found from IEX
   * @throws org.springframework.dao.DataAccessException if unable to retrieve data
   * @throws IllegalArgumentException for invalid input
   */
  public TraderAccountView getTraderAccount(Integer traderId) {
    TraderAccountView traderAccountView = new TraderAccountView();
    traderAccountView.setTrader(traderDao.findById(traderId));
    traderAccountView.setAccount(accountDao.findByTraderId(traderId));
    return traderAccountView;
  }

  /**
   * Create and return portfolioView by trader ID
   * - get account by trader id
   * - get positions by account id
   * - create and return a portfolioView
   *
   * @param traderId
   * @return portfolioView
   * @throws ca.jrvs.apps.trading.dao.ResourceNotFoundException if ticker is not found from IEX
   * @throws org.springframework.dao.DataAccessException if unable to retrieve data
   * @throws IllegalArgumentException for invalid input
   */
  public PortfolioView getProfileViewByTraderId(Integer traderId) {
    //get account by trader id
    Account account = accountDao.findByTraderId(traderId);
    //get positions by account id
    List<Position> positions = positionDao.findByAccountId(account.getId());
    //create and return a portfolioView
    PortfolioView portfolioView = new PortfolioView();
    List<SecurityRow> securityRows = null;
    positions.forEach(position -> {
      SecurityRow securityRow = new SecurityRow();
      String ticker = position.getTicker();
      securityRow.setTicker(ticker);
      securityRow.setQuote(quoteDao.findById(ticker));
      securityRow.setPosition(position);
      securityRows.add(securityRow);
    });
    SecurityRow[] rows = securityRows.toArray(new SecurityRow[securityRows.size()]);
    portfolioView.setSecurityRows(rows);

    return portfolioView;
  }
}
