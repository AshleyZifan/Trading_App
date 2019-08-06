package ca.jrvs.apps.trading.dao;

import ca.jrvs.apps.trading.model.domain.SecurityOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import javax.sql.DataSource;
import org.springframework.stereotype.Repository;


@Repository
public class SecurityOrderDao implements CrudRepository<SecurityOrder, Integer> {

    private static final Logger logger = LoggerFactory.getLogger(SecurityOrderDao.class);

    private final String TABLE_NAME = "security_order";
    private final String ID_COLUMN = "id";

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert simpleInsert;

    @Autowired
    public SecurityOrderDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.simpleInsert = new SimpleJdbcInsert(dataSource).withTableName(TABLE_NAME)
                .usingGeneratedKeyColumns(ID_COLUMN);
    }

    @Override
    public SecurityOrder save(SecurityOrder entity) {
        SqlParameterSource parameterSource = new BeanPropertySqlParameterSource(entity);
        Number newId = simpleInsert.executeAndReturnKey(parameterSource);
        entity.setId(newId.intValue());
        return entity;
    }

    @Override
    public SecurityOrder findById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("ID can't be null");
        }
        SecurityOrder order = null;
        try {
            order = jdbcTemplate
                    .queryForObject("select * from " + TABLE_NAME + " where " + ID_COLUMN +" = ?",
                            BeanPropertyRowMapper.newInstance(SecurityOrder.class), id);
        } catch (EmptyResultDataAccessException e) {
            logger.debug("Can't find trader id:" + id, e);
        }
        return order;
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

    public void deleteByAccountId(Integer accountId) {
        if(accountId == null) {
            throw new IllegalArgumentException("account ID can't be null");
        }
        jdbcTemplate.update("delete from " + TABLE_NAME + " where account_id= ?", accountId);
    }
}
