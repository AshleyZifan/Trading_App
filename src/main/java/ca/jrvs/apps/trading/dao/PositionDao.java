package ca.jrvs.apps.trading.dao;

import ca.jrvs.apps.trading.model.domain.Position;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import java.util.List;

public class PositionDao {

    private static final Logger logger = LoggerFactory.getLogger(QuoteDao.class);

    private final static String TABLE_NAME = "position";
    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert simpleJdbcInsert;

    public List<Position> findByAccountId(Integer accountId) {
        if (accountId == null) {
            throw new IllegalArgumentException("Account ID can't be null");
        }
        List<Position> positions = null;
        try {
            positions = jdbcTemplate.query("select * from " + TABLE_NAME + " where account_id= ?",
                            BeanPropertyRowMapper.newInstance(Position.class), accountId);
        } catch (EmptyResultDataAccessException e) {
            logger.debug("Can't find account id:" + accountId, e);
        }
        return positions;
    }

}
