package ca.jrvs.apps.trading.dao;

import ca.jrvs.apps.trading.model.domain.Quote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Repository
public class QuoteDao  implements CrudRepository<Quote, String> {

    private static final Logger logger = LoggerFactory.getLogger(QuoteDao.class);

    private final static String TABLE_NAME = "quote";
    private final static String ID_NAME = "ticker";
    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert simpleJdbcInsert;

    @Autowired
    public QuoteDao(DataSource dataSource){
        jdbcTemplate = new JdbcTemplate(dataSource);
        simpleJdbcInsert = new SimpleJdbcInsert(dataSource).withTableName(TABLE_NAME);
    }


    @Override
    public Quote save(Quote entity) {
        SqlParameterSource parameterSource = new BeanPropertySqlParameterSource(entity);
        Number newId = simpleJdbcInsert.executeAndReturnKey(parameterSource);
        entity.setId(newId.toString());
        return entity;
    }

    @Override
    public Quote findById(String id) {
        if (id == null) {
            throw new IllegalArgumentException("ID can't be null");
        }
        Quote quote = null;
        try {
            quote = jdbcTemplate
                    .queryForObject("select * from " + TABLE_NAME + " where " + ID_NAME +" = ?",
                            BeanPropertyRowMapper.newInstance(Quote.class), id);
        } catch (EmptyResultDataAccessException e) {
            logger.debug("Can't find quote id:" + id, e);
        }
        return quote;
    }

    @Override
    public boolean existsById(String id) {
        return (findById(id)!=null);
    }

    @Override
    public void deleteById(String id) {
        if(id == null) {
            throw new IllegalArgumentException("ID can't be null");
        }
        jdbcTemplate.update("delete from " + TABLE_NAME + " where " + ID_NAME +" = ?", id);
    }

    public List<Quote> getAllQuotes(){
        List<Quote> quotes = null;
        try {
            quotes = jdbcTemplate.query("select * from " + TABLE_NAME, BeanPropertyRowMapper.newInstance(Quote.class));
        } catch (EmptyResultDataAccessException e) {
            logger.debug("Can't find quotes", e);
        }
        return quotes;
    }

    public void update(List<Quote> quotes){
        String updateSql = "UPDATE quote SET last_price=?, bid_price=?, bid_size=? ask_price=? ask_size=? WHERE ticker=?";
        List<Object[]> batch = new ArrayList<>();
        quotes.forEach(quote -> {
            if(!existsById(quote.getTicker())){
                throw new ResourceNotFoundException("Ticker not found:" + quote.getTicker());
            }
            Object[] values = new Object[]{quote.getLastPrice(),quote.getBidPrice(),quote.getBidSize(),quote.getAskPrice(), quote.getAskSize(), quote.getTicker()};
            batch.add(values);
        });
        int[] rows = jdbcTemplate.batchUpdate(updateSql, batch);
        int totalRow = Arrays.stream(rows).sum();
        if(totalRow != quotes.size()){
            throw new IncorrectResultSizeDataAccessException("Number of rows", quotes.size(), totalRow);
        }

    }

    public void update(Quote quote){
        List<Quote> quotes = new ArrayList<>();
        quotes.add(quote);
        update(quotes);
    }
}
