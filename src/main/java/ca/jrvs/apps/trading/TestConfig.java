package ca.jrvs.apps.trading;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@ComponentScan(basePackages = {"ca.jrvs.apps.trading.dao","ca.jrvs.apps.trading.service"})
public class TestConfig {

    private static final String JDBC_URL = "jdbc:postgresql://localhost:5432/jrvstrading_test";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "password";

    @Bean
    public DataSource dataSource(){
        System.out.println("Creating apacheDataSource");
        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setUrl(JDBC_URL);
        basicDataSource.setUsername(DB_USER);
        basicDataSource.setPassword(DB_PASSWORD);

        return basicDataSource;
    }


}
