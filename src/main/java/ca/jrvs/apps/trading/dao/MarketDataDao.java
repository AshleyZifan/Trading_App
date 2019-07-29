package ca.jrvs.apps.trading.dao;

import ca.jrvs.apps.trading.Util.JsonUtil;
import ca.jrvs.apps.trading.model.domain.IexQuote;
import com.google.common.base.Joiner;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public class MarketDataDao {

    private static final String API_BASE_URI = "https://cloud.iexapis.com";
    private static final String BATCH_QUOTE_PATH = "/v1/stock/market/batch?symbols=%s&types=quote&token=";
    private static final String TOKEN = System.getenv("token");
    private HttpClientConnectionManager clientConnectionManager;

    public MarketDataDao(HttpClientConnectionManager clientConnectionManager){
        this.clientConnectionManager = clientConnectionManager;
    }

   public List<IexQuote> findIexQuoteByTicker(List<String> tickers){
       String response = executeHttpGet(getURI(tickers));
       JSONObject quotesJson = new JSONObject(response);
       if (quotesJson.length() == 0) {
           throw new ResourceNotFoundException("Not Found");
       }
       if (quotesJson.length() != tickers.size()) {
           throw new IllegalArgumentException("Invalid ticker/symbol");
       }
       List<IexQuote> iexQuotes = new ArrayList<>();
       quotesJson.keys().forEachRemaining(ticker -> {
           try {
               String quoteStr = ((JSONObject)quotesJson.get(ticker)).get("quote").toString();
               IexQuote iexQuote = JsonUtil.toObjectFromJson(quoteStr, IexQuote.class);
               iexQuotes.add(iexQuote);
           } catch (IOException e){
               throw new DataRetrievalFailureException("Unable parse response" + quotesJson.get(ticker), e);
           }
       });
       return iexQuotes;
    }

    public IexQuote findIexQuoteByTicker(String ticker){
        List<IexQuote> quotes = findIexQuoteByTicker(Arrays.asList(ticker));
        if (quotes == null || quotes.size() != 1) {
            throw new DataRetrievalFailureException("Unable to get data");
        }
        return quotes.get(0);
    }


    protected String getURI(List<String> tickers){
        String symbols = Joiner.on(",").join(tickers);
        String resultUri = String.format(BATCH_QUOTE_PATH,symbols);
        return API_BASE_URI + resultUri + TOKEN;
    }


    protected String executeHttpGet(String url) {
        try (CloseableHttpClient httpClient = getHttpClient()) {
            HttpGet httpGet = new HttpGet(url);
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                switch (response.getStatusLine().getStatusCode()) {
                    case 200:
                        //EntityUtils toString will also close inputStream in Entity
                        String body = EntityUtils.toString(response.getEntity());
                        return Optional.ofNullable(body).orElseThrow(
                                () -> new IOException("Unexpected empty http response body"));
                    case 404:
                        throw new ResourceNotFoundException("Not found");
                    default:
                        throw new DataRetrievalFailureException(
                                "Unexpected status:" + response.getStatusLine().getStatusCode());
                }
            }
        } catch (IOException e) {
            throw new DataRetrievalFailureException("Unable Http execution error", e);
        }
    }

    protected CloseableHttpClient getHttpClient(){
        return HttpClients.custom().setConnectionManager(clientConnectionManager).setConnectionManagerShared(true).build();
    }

}
