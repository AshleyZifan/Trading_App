package ca.jrvs.apps.trading.controller;

import ca.jrvs.apps.trading.AppConfig;
import ca.jrvs.apps.trading.dao.MarketDataDao;
import ca.jrvs.apps.trading.dao.QuoteDao;
import ca.jrvs.apps.trading.model.domain.IexQuote;
import ca.jrvs.apps.trading.service.QuoteService;
import org.apache.http.conn.HttpClientConnectionManager;
import org.junit.Test;

import static org.junit.Assert.*;

public class QuoteControllerTest {

    @Test
    public void getQuoteTest() {
        QuoteService service = new QuoteService();
        QuoteDao quoteDao = new QuoteDao();
        AppConfig config = new AppConfig();
        HttpClientConnectionManager connectionManager = config.httpClientConnectionManager();
        MarketDataDao marketDataDao = new MarketDataDao(connectionManager);
        QuoteController controller = new QuoteController(service, quoteDao, marketDataDao);
        IexQuote quote = controller.getQuote("AAPL");
        assertNotNull(quote);
        System.out.println(quote.toString());
    }
}