package vip.justlive.helium.base.factory;

import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.auth.KeyStoreOptions;
import io.vertx.ext.auth.jdbc.JDBCAuth;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.ext.web.handler.AuthHandler;
import io.vertx.ext.web.handler.BasicAuthHandler;
import io.vertx.ext.web.handler.JWTAuthHandler;
import vip.justlive.common.base.support.ConfigFactory;
import vip.justlive.common.web.vertx.JustLive;
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
   * 获取认证提供
   *
   * @return authProvider
   */
  public static AuthProvider authProvider() {
    AuthConf conf = ConfigFactory.load(AuthConf.class);
    AuthConf.PROVIDER_TYPE providerType = AuthConf.PROVIDER_TYPE.valueOf(conf.getProviderType());
    AuthProvider authProvider = null;
    switch (providerType) {
      case JDBC: {
        authProvider = jdbcAuth();
        break;
      }
      case JWT: {
        authProvider = jwtAuth();
        break;
      }
      case NONE:
      default:
        break;

    }
    return authProvider;
  }

  /**
   * 获取jdbc认证
   *
   * @return JDBCAuth
   */
  public static JDBCAuth jdbcAuth() {
    return JDBCAuth.create(JustLive.vertx(), DataSourceFactory.sharedJdbcClient());
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

  /**
   * 获取认证处理
   *
   * @param authProvider 认证提供
   * @return AuthHandler
   */
  public static AuthHandler authHandler(AuthProvider authProvider) {
    AuthConf conf = ConfigFactory.load(AuthConf.class);
    String authUsed = conf.getAuthUsed();
    AuthHandler authHandler = null;
    if (authUsed != null) {
      AuthConf.AUTH_TYPE authType = AuthConf.AUTH_TYPE.valueOf(authUsed);
      switch (authType) {
        case BASIC: {
          authHandler = BasicAuthHandler.create(authProvider);
          break;
        }
        case JWT: {
          authHandler = JWTAuthHandler.create((JWTAuth) authProvider);
          break;
        }
        default:
          break;
      }
    }
    return authHandler;
  }

}
