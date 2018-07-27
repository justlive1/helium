package vip.justlive.helium.base.factory;

import io.vertx.ext.auth.KeyStoreOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import vip.justlive.common.base.support.ConfigFactory;
import vip.justlive.common.web.vertx.JustLive;
import vip.justlive.common.web.vertx.auth.JdbcAuth;
import vip.justlive.common.web.vertx.datasource.DataSourceFactory;
import vip.justlive.helium.base.config.AuthConf;

/**
 * 认证工厂
 *
 * @author wubo
 */
public class AuthFactory {

  AuthFactory() {
  }

  /**
   * 获取jdbc认证
   *
   * @return JDBCAuth
   */
  public static JdbcAuth jdbcAuth() {
    return new JdbcAuth(DataSourceFactory.sharedJdbcClient());
  }

  /**
   * 获取jwt认证
   *
   * @return JWTAuth
   */
  public static JWTAuth jwtAuth() {
    AuthConf conf = ConfigFactory.load(AuthConf.class);
    KeyStoreOptions keystoreOptions = new KeyStoreOptions();
    keystoreOptions.setPassword(conf.getJwtKeystorePassword());
    keystoreOptions.setPath(conf.getJwtKeystorePath());
    keystoreOptions.setType(conf.getJwtKeystoreType());
    JWTAuthOptions config = new JWTAuthOptions().setKeyStore(keystoreOptions);
    return JWTAuth.create(JustLive.vertx(), config);
  }

}
