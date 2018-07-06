package vip.justlive.helium.httpserver.controller;

import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jdbc.JDBCAuth;
import io.vertx.ext.web.RoutingContext;
import vip.justlive.common.base.domain.Response;
import vip.justlive.common.web.vertx.annotation.VertxRequestParam;
import vip.justlive.common.web.vertx.annotation.VertxRoute;
import vip.justlive.common.web.vertx.annotation.VertxRouteMapping;
import vip.justlive.helium.base.factory.AuthFactory;
import vip.justlive.helium.base.factory.RepositoryFactory;
import vip.justlive.helium.base.repository.BaseRepository.Promise;
import vip.justlive.helium.base.repository.UserRepository;

/**
 * 访问路由
 *
 * @author wubo
 */
@VertxRoute
public class AccessController {

  private final JDBCAuth jdbcAuth;

  public AccessController() {
    jdbcAuth = AuthFactory.jdbcAuth();
  }

  /**
   * jwt 认证 TODO 参数加密
   *
   * @param username 用户名
   * @param password 密码
   * @return json
   */
  @VertxRouteMapping(value = "/jwt")
  public void jwt(@VertxRequestParam(value = "username", required = false) String username,
    @VertxRequestParam(value = "password", required = false) String password, RoutingContext ctx) {
    jdbcAuth
      .authenticate(new JsonObject().put("username", username).put("password", password), r -> {
        if (r.succeeded()) {
          ctx.response().end("success");
        } else {
          ctx.fail(401);
        }
      });
  }

}
