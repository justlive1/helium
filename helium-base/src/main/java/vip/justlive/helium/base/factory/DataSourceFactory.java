package vip.justlive.helium.base.factory;

import com.google.common.collect.Maps;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import java.util.Map;
import vip.justlive.common.base.support.ConfigFactory;
import vip.justlive.helium.base.JustLive;
import vip.justlive.helium.base.config.DataSourceConf;

/**
 * 数据源工厂
 *
 * @author wubo
 */
public class DataSourceFactory {

  DataSourceFactory() {
  }

  private static final Map<Class<?>, JDBCClient> CLIENTS = Maps.newConcurrentMap();

  /**
   * 共享jdbcClient
   *
   * @return jdbcClient
   */
  public static JDBCClient sharedJdbcClient() {
    DataSourceConf conf = ConfigFactory.load(DataSourceConf.class);
    JsonObject json = JsonObject.mapFrom(conf);
    return JDBCClient.createShared(JustLive.vertx(), json);
  }

  /**
   * 共享单例jdbcClient
   *
   * @return jdbcClient
   */
  public static JDBCClient sharedSingleJdbcClient() {
    return sharedSingleJdbcClient(DataSourceFactory.class);
  }

  /**
   * 共享单例jdbcClient
   *
   * @param clazz 类
   * @return jdbcClient
   */
  public static JDBCClient sharedSingleJdbcClient(Class<?> clazz) {
    JDBCClient client = CLIENTS.get(DataSourceFactory.class);
    if (client == null) {
      CLIENTS.putIfAbsent(clazz, sharedJdbcClient());
    }
    return CLIENTS.get(clazz);
  }

}
