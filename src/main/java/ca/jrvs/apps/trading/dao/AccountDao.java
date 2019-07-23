package ca.jrvs.apps.trading.dao;

import ca.jrvs.apps.trading.model.domain.Account;
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

public class AccountDao implements CrudRepository<Account, Integer> {
    private static final Logger logger = LoggerFactory.getLogger(AccountDao.class);

    private final String TABLE_NAME = "account";
    private final String ID_COLUMN = "id";

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert simpleInsert;

    @Autowired
    public AccountDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.simpleInsert = new SimpleJdbcInsert(dataSource).withTableName(TABLE_NAME)
                .usingGeneratedKeyColumns(ID_COLUMN);
    }

    @Override
    public Account save(Account entity) {
        SqlParameterSource parameterSource = new BeanPropertySqlParameterSource(entity);
        Number newId = simpleInsert.executeAndReturnKey(parameterSource);
        entity.setId(newId.intValue());
        return entity;
    }

    @Override
    public Account findById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("ID can't be null");
        }
        Account account = null;
        try {
            account = jdbcTemplate
                    .queryForObject("select * from " + TABLE_NAME + " where " + ID_COLUMN +" = ?",
                            BeanPropertyRowMapper.newInstance(Account.class), id);
        } catch (EmptyResultDataAccessException e) {
            logger.debug("Can't find id:" + id, e);
        }
        return account;
    }

    public Account findByTraderId(Integer traderId){
        if (traderId == null) {
            throw new IllegalArgumentException("Trader ID can't be null");
        }
        Account account = null;
        try {
            account = jdbcTemplate
                    .queryForObject("select * from " + TABLE_NAME + " where trader_id=?",
                            BeanPropertyRowMapper.newInstance(Account.class), traderId);
        } catch (EmptyResultDataAccessException e) {
            logger.debug("Can't find trader id:" + traderId, e);
        }
        return account;
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

    public void updateAmount(Account account){
        String updateSql = "UPDATE quote SET amount=" + account.getAmount() + "WHERE id=" + account.getId();
        if(!existsById(account.getId())){
            throw new ResourceNotFoundException("ID not found:" + account.getId());
        }
        jdbcTemplate.update(updateSql);
    }

}
