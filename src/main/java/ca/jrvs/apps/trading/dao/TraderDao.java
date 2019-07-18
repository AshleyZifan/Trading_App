package ca.jrvs.apps.trading.dao;

import ca.jrvs.apps.trading.model.domain.Trader;
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
public class TraderDao implements CrudRepository<Trader, Integer> {

    private static final Logger logger = LoggerFactory.getLogger(TraderDao.class);

    private final String TABLE_NAME = "trader";
    private final String ID_COLUMN = "id";

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert simpleInsert;

    @Autowired
    public TraderDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.simpleInsert = new SimpleJdbcInsert(dataSource).withTableName(TABLE_NAME)
                .usingGeneratedKeyColumns(ID_COLUMN);
    }

    @Override
    public Trader save(Trader entity) {
        SqlParameterSource parameterSource = new BeanPropertySqlParameterSource(entity);
        Number newId = simpleInsert.executeAndReturnKey(parameterSource);
        entity.setId(newId.intValue());
        return entity;
    }

    @Override
    public Trader findById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("ID can't be null");
        }
        Trader trader = null;
        try {
            trader = jdbcTemplate
                    .queryForObject("select * from " + TABLE_NAME + " where " + ID_COLUMN +" = ?",
                            BeanPropertyRowMapper.newInstance(Trader.class), id);
        } catch (EmptyResultDataAccessException e) {
            logger.debug("Can't find trader id:" + id, e);
        }
        return trader;
    }

    @Override
    public boolean existsById(Integer id) {
        return (findById(id)!=null);
    }

    @Override
    public void deleteById(Integer id) {
        if(id == null) {
            throw new IllegalArgumentException("ID can't be null");
        }
        jdbcTemplate.update("delete from " + TABLE_NAME + " where " + ID_COLUMN +" = ?", id);
    }
}
