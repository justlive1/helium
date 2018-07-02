package vip.justlive.helium.base.datasource;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import vip.justlive.common.base.support.ConfigFactory;
import vip.justlive.helium.base.JustLive;
import vip.justlive.helium.base.config.DataSourceConf;

/**
 * @author wubo
 */
public class DataSourceFactory {

  DataSourceFactory() {
  }

  /**
   * @return jdbcClient
   */
  public static JDBCClient sharedJdbcClient() {
    DataSourceConf conf = ConfigFactory.load(DataSourceConf.class);
    JsonObject json = JsonObject.mapFrom(conf);
    return JDBCClient.createShared(JustLive.vertx(), json);
  }

}
