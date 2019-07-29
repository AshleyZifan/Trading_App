package ca.jrvs.apps.trading.service;

import ca.jrvs.apps.trading.AppConfig;
import ca.jrvs.apps.trading.controller.QuoteController;
import ca.jrvs.apps.trading.dao.MarketDataDao;
import ca.jrvs.apps.trading.dao.QuoteDao;
import ca.jrvs.apps.trading.model.domain.IexQuote;
import ca.jrvs.apps.trading.model.domain.Quote;
import org.apache.http.conn.HttpClientConnectionManager;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class QuoteServiceTest {

    @Mock
    private QuoteDao quoteDao;

    AppConfig config;
    HttpClientConnectionManager connectionManager = config.httpClientConnectionManager();
    MarketDataDao marketDataDao = new MarketDataDao(connectionManager);
    QuoteService quoteService = new QuoteService(quoteDao, marketDataDao);

    @Test
    public void buildQuoteFromIexQuote() {
        String ticker = "AAPL";

        IexQuote iex_quote = marketDataDao.findIexQuoteByTicker(ticker);
        Quote quote = quoteService.buildQuoteFromIexQuote(iex_quote);
        System.out.println(quote.toString());
    }

    @Test
    public void initQuotes() {

        List<String> target = new ArrayList<>();
        target.add("AAPL");
        target.add("INTL");
        target.add("TSLA");

        for (String t : target){
            when(quoteDao.existsById(t)).thenReturn(false);
            quoteService.initQuotes(target);
        }
    }

    @Test
    public void initQuote() {
        String ticker = "TSLA";
        quoteService.initQuote(ticker);

    }

    @Test
    public void updateMarketData() {
        quoteService.updateMarketData();
    }
}