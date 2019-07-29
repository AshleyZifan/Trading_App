package ca.jrvs.apps.trading.service;

import ca.jrvs.apps.trading.AppConfig;
import ca.jrvs.apps.trading.controller.QuoteController;
import ca.jrvs.apps.trading.dao.MarketDataDao;
import ca.jrvs.apps.trading.dao.QuoteDao;
import ca.jrvs.apps.trading.model.domain.IexQuote;
import ca.jrvs.apps.trading.model.domain.Quote;
import org.apache.http.conn.HttpClientConnectionManager;
import org.junit.Test;

import static org.junit.Assert.*;

public class QuoteServiceTest {

    QuoteDao quoteDao;
    AppConfig config = new AppConfig();
    HttpClientConnectionManager connectionManager = config.httpClientConnectionManager();
    MarketDataDao marketDataDao = new MarketDataDao(connectionManager);
    QuoteService quoteService = new QuoteService(quoteDao, marketDataDao);

    @Test
    public void buildQuoteFromIexQuote() {
        IexQuote iex_quote = marketDataDao.findIexQuoteByTicker("AAPL");
        Quote quote = quoteService.buildQuoteFromIexQuote(iex_quote);
        System.out.println(quote.toString());
    }

    @Test
    public void initQuotes() {
    }

    @Test
    public void initQuote() {
    }

    @Test
    public void updateMarketData() {
    }
}