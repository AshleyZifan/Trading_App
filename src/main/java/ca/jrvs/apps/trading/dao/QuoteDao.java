package ca.jrvs.apps.trading.dao;

import ca.jrvs.apps.trading.model.domain.Quote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

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
}
