package ca.jrvs.apps.trading.controller;

import ca.jrvs.apps.trading.dao.MarketDataDao;
import ca.jrvs.apps.trading.dao.QuoteDao;
import ca.jrvs.apps.trading.model.domain.IexQuote;
import ca.jrvs.apps.trading.service.QuoteService;

public class QuoteController {

    private MarketDataDao marketDataDao;

    public QuoteController(QuoteService quoteService, QuoteDao quoteDao, MarketDataDao marketDataDao){
        this.marketDataDao = marketDataDao;
    }

    public IexQuote getQuote(String ticker){
        return marketDataDao.findIexQupteByTicker(ticker);
    }
}
