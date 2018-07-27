package vip.justlive.helium.httpserver.controller;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.UpdateResult;
import io.vertx.ext.web.RoutingContext;
import vip.justlive.common.base.domain.Response;
import vip.justlive.common.web.vertx.annotation.VertxRequestBody;
import vip.justlive.common.web.vertx.annotation.VertxRoute;
import vip.justlive.common.web.vertx.annotation.VertxRouteMapping;
import vip.justlive.common.web.vertx.auth.JdbcAuth;
import vip.justlive.common.web.vertx.datasource.JdbcPromise;
import vip.justlive.common.web.vertx.datasource.ModelsPromise;
import vip.justlive.common.web.vertx.datasource.RepositoryFactory;
import vip.justlive.helium.base.entity.User;
import vip.justlive.helium.base.factory.AuthFactory;
import vip.justlive.helium.base.repository.UserRepository;

/**
 * 用户路由
 *
 * @author wubo
 */
@VertxRoute
public class UserController {

  private final UserRepository userRepository;
  private final JdbcAuth jdbcAuth;

  public UserController() {
    this.userRepository = RepositoryFactory.repository(UserRepository.class);
    this.jdbcAuth = AuthFactory.jdbcAuth();
  }

  @VertxRouteMapping(value = "/register", method = {HttpMethod.POST})
  public void register(@VertxRequestBody User user, RoutingContext ctx) {

    User model = new User();
    model.setUsername(user.getUsername());

    ModelsPromise<User> modelsPromise = userRepository.findByModel(model);
    modelsPromise.then(users -> {
      if (users == null || users.isEmpty()) {
        String raw = jdbcAuth.encode(user.getPassword());
        user.setPassword(raw);
        JdbcPromise<UpdateResult> promise = userRepository.save(user);
        promise.succeeded(rs -> ctx.response()
          .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON.toString())
          .end(JsonObject.mapFrom(Response.success()).toBuffer())
        );
      } else {
        ctx.response()
          .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON.toString())
          .end(JsonObject.mapFrom(Response.error("用户名已被注册")).toBuffer());
      }
    });
  }
}
