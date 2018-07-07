package vip.justlive.helium.httpserver.controller;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jdbc.JDBCAuth;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.jwt.JWTOptions;
import io.vertx.ext.web.RoutingContext;
import vip.justlive.common.base.support.ConfigFactory;
import vip.justlive.common.web.vertx.annotation.VertxRequestParam;
import vip.justlive.common.web.vertx.annotation.VertxRoute;
import vip.justlive.common.web.vertx.annotation.VertxRouteMapping;
import vip.justlive.helium.base.config.AuthConf;
import vip.justlive.helium.base.factory.AuthFactory;

/**
 * 访问路由
 *
 * @author wubo
 */
@VertxRoute
public class AccessController {

  private final JDBCAuth jdbcAuth;

  private final JWTAuth jwtAuth;

  public AccessController() {
    jdbcAuth = AuthFactory.jdbcAuth();
    jwtAuth = AuthFactory.jwtAuth();
  }

  /**
   * jwt 认证 TODO 参数加密
   *
   * @param username 用户名
   * @param password 密码
   */
  @VertxRouteMapping(value = "/jwt")
  public void jwt(@VertxRequestParam(value = "username", required = false) String username,
    @VertxRequestParam(value = "password", required = false) String password, RoutingContext ctx) {
    jdbcAuth
      .authenticate(new JsonObject().put("username", username).put("password", password), r -> {
        if (r.succeeded()) {
          AuthConf authConf = ConfigFactory.load(AuthConf.class);
          ctx.response().end(
            jwtAuth.generateToken(r.result().principal(),
              new JWTOptions().setAlgorithm(authConf.getJwtKeystoreAlgorithm())));
        } else {
          ctx.fail(401);
        }
      });
  }

}
