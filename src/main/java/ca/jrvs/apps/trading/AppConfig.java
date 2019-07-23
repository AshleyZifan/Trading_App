package ca.jrvs.apps.trading;

@Configuration
public class AppConfig {

  private Logger logger = LoggerFactory.getLogger(AppConfig.class);

  //you can hard code this for now or use env_var
  @Value("${iex.host}")
  private String iex_host;

  @Bean
  public MarketDataConfig marketDataConfig() {
    if (StringUtil.isEmpty(System.getenv("IEX_PUB_TOKEN")) || StringUtil.isEmpty(iex_host)) {
      throw new IllegalArgumentException("ENV:IEX_PUB_TOKEN or property:iex_host is not set");
    }
    MarketDataConfig marketDataConfig = new MarketDataConfig();
    marketDataConfig.setToken(System.getenv("IEX_PUB_TOKEN"));
    marketDataConfig.setHost(iex_host);
    return marketDataConfig;
  }

  @Bean
  public DataSource dataSource() {

    String jdbcUrl;
    String user;
    String password;
 
    jdbcUrl = System.getenv("PSQL_URL");
    user = System.getenv("PSQL_USER");
    password = System.getenv("PSQL_PASSWORD");

    logger.error("JDBC:" + jdbcUrl);

    if (StringUtil.isEmpty(jdbcUrl, user, password)) {
      throw new IllegalArgumentException("Missing data source config env vars");
    }

    BasicDataSource basicDataSource = new BasicDataSource();
    basicDataSource.setDriverClassName("org.postgresql.Driver");
    basicDataSource.setUrl(jdbcUrl);
    basicDataSource.setUsername(user);
    basicDataSource.setPassword(password);
    return basicDataSource;
  }

  //http://bit.ly/2tWTmzQ connectionPool
  @Bean
  public HttpClientConnectionManager httpClientConnectionManager() {
    PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
    cm.setMaxTotal(50);
    cm.setDefaultMaxPerRoute(50);
    return cm;
  }
}