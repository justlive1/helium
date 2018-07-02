package vip.justlive.helium.base.config;

import lombok.Data;
import vip.justlive.common.base.annotation.Value;

/**
 * @author wubo
 */
@Data
public class DataSourceConf {

  @Value("${datasource.providerClass}")
  private String providerClass;

  @Value("${datasource.driverClassName}")
  private String driverClassName;

  @Value("${datasource.jdbcUrl}")
  private String jdbcUrl;

  @Value("${datasource.username}")
  private String username;

  @Value("${datasource.password}")
  private String password;

  @Value("${datasource.maximumPoolSize:10}")
  private Integer maximumPoolSize;

  @Value("${datasource.minimumIdle:5}")
  private Integer minimumIdle;

  @Value("${datasource.prepStmtCacheSize:250}")
  private Integer prepStmtCacheSiz;

  @Value("${datasource.prepStmtCacheSqlLimit:2048}")
  private Integer prepStmtCacheSqlLimit;

}
