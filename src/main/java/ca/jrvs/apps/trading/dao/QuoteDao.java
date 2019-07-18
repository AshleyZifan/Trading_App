package ca.jrvs.apps.trading.dao;

import ca.jrvs.apps.trading.model.domain.Quote;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import javax.sql.DataSource;

public class QuoteDao extends JdbcCrudDao<Quote, String>{

    private final static String TABLE_NAME = "quote";
    private final static String ID_NAME = "ticker";
    private JdbcTemplate jdbcTemplate;

    private SimpleJdbcInsert simpleJdbcInsert;

    public QuoteDao(DataSource dataSource){
        jdbcTemplate = new JdbcTemplate(dataSource);
        simpleJdbcInsert = new SimpleJdbcInsert(dataSource).withTableName(TABLE_NAME);
    }


    @Override
    public Object save(Object entity) {
        return null;
    }

    @Override
    public Object findById(Object o) {
        return null;
    }

    @Override
    public boolean existsById(Object o) {
        return false;
    }

    @Override
    public void deleteById(Object o) {

    }
}
