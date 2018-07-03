package vip.justlive.helium.base.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import vip.justlive.common.base.annotation.Value;

/**
 * 数据源配置
 *
 * @author wubo
 */
@Data
public class DataSourceConf {

  /**
   * 数据源提供类
   */
  @JsonProperty("provider_class")
  @Value("${datasource.providerClass}")
  private String providerClass;

  /**
   * 驱动
   */
  @Value("${datasource.driverClassName}")
  private String driverClassName;

  /**
   * 连接串
   */
  @Value("${datasource.jdbcUrl}")
  private String jdbcUrl;

  /**
   * 用户名
   */
  @Value("${datasource.username}")
  private String username;

  /**
   * 密码
   */
  @Value("${datasource.password}")
  private String password;

  /**
   * 最大线程数
   */
  @Value("${datasource.maximumPoolSize:10}")
  private Integer maximumPoolSize;

  /**
   * 最小空闲线程数
   */
  @Value("${datasource.minimumIdle:5}")
  private Integer minimumIdle;

  /**
   * prepStatement缓存数
   */
  @Value("${datasource.prepStmtCacheSize:250}")
  private Integer prepStmtCacheSiz;

  /**
   * prepStatement缓存大小
   */
  @Value("${datasource.prepStmtCacheSqlLimit:2048}")
  private Integer prepStmtCacheSqlLimit;

}
