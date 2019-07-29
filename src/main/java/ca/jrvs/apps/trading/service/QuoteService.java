package ca.jrvs.apps.trading.service;

import ca.jrvs.apps.trading.dao.MarketDataDao;
import ca.jrvs.apps.trading.dao.QuoteDao;
import ca.jrvs.apps.trading.model.domain.IexQuote;
import ca.jrvs.apps.trading.model.domain.Quote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.List;


@Service
public class QuoteService {

    private QuoteDao quoteDao;
    private MarketDataDao marketDataDao;

    @Autowired
    public QuoteService(QuoteDao quoteDao, MarketDataDao marketDataDao){
        this.quoteDao = quoteDao;
        this.marketDataDao = marketDataDao;
    }

    public static Quote buildQuoteFromIexQuote(IexQuote iexQuote){
        Quote quote = null;
        Field[] Iexfields = IexQuote.class.getDeclaredFields();
        for ( Field f : Iexfields ) {
            try {
                Field t = Quote.class.getDeclaredField( f.getName() );

                if ( t.getType() == f.getType() ) {
                    f.setAccessible(true);
                    t.setAccessible(true);
                    t.set(quote, f.get(iexQuote) );
                }
            } catch (NoSuchFieldException ex) {
                // skip it
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return quote;
    }

    public void initQuotes(List<String> tickers){
        for(String ticker: tickers){
            if(!quoteDao.existsById(ticker)){
                quoteDao.save(buildQuoteFromIexQuote((marketDataDao.findIexQuoteByTicker(ticker))));
            }
        }
    }

    public void initQuote(String ticker){
        if(!quoteDao.existsById(ticker)){
            quoteDao.save(buildQuoteFromIexQuote((marketDataDao.findIexQuoteByTicker(ticker))));
        }
    }

    public void updateMarketData(){
        List<Quote> quotes = quoteDao.getAllQuotes();
        for(Quote quote: quotes){
            quoteDao.update(buildQuoteFromIexQuote(marketDataDao.findIexQuoteByTicker(quote.getTicker())));
        }
    }
}
