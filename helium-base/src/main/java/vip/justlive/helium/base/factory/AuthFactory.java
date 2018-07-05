package vip.justlive.helium.base.factory;

import com.google.common.collect.Lists;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.auth.jdbc.JDBCAuth;
import io.vertx.ext.web.handler.AuthHandler;
import io.vertx.ext.web.handler.BasicAuthHandler;
import java.util.List;
import vip.justlive.common.base.support.ConfigFactory;
import vip.justlive.helium.base.JustLive;
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
        authProvider = JDBCAuth.create(JustLive.vertx(), DataSourceFactory.sharedJdbcClient());
        break;
      }
      case NONE:
      default:
        break;

    }
    return authProvider;
  }

  public static List<AuthHandler> authHandlers(AuthProvider authProvider) {
    List<AuthHandler> list = Lists.newArrayList();
    AuthConf conf = ConfigFactory.load(AuthConf.class);
    String[] authUsed = conf.getAuthUsed();
    if (authUsed != null && authUsed.length > 0) {
      for (String auth : authUsed) {
        AuthConf.AUTH_TYPE authType = AuthConf.AUTH_TYPE.valueOf(auth);
        switch (authType) {
          case BASIC: {
            list.add(BasicAuthHandler.create(authProvider));
            break;
          }
          default:
            break;
        }
      }
    }
    return list;
  }

}
