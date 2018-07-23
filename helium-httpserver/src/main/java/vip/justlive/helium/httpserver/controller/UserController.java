package vip.justlive.helium.httpserver.controller;

import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.UpdateResult;
import io.vertx.ext.web.RoutingContext;
import vip.justlive.common.base.domain.Response;
import vip.justlive.common.web.vertx.annotation.VertxRequestBody;
import vip.justlive.common.web.vertx.annotation.VertxRoute;
import vip.justlive.common.web.vertx.annotation.VertxRouteMapping;
import vip.justlive.common.web.vertx.datasource.RepositoryFactory;
import vip.justlive.helium.base.entity.User;
import vip.justlive.helium.base.repository.UserRepository;

/**
 * 用户路由
 *
 * @author wubo
 */
@VertxRoute
public class UserController {

  @VertxRouteMapping(value = "/register", method = {HttpMethod.POST})
  public void register(@VertxRequestBody User user, RoutingContext ctx) {

    UserRepository repository = RepositoryFactory.repository(UserRepository.class);

    user.setPasswordSalt("salt");
    UserRepository.JdbcPromise<UpdateResult> promise = repository.save(user);
    promise.succeeded(rs -> {
      ctx.response().putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
        .end(JsonObject.mapFrom(Response.success("success")).toBuffer());
    });

  }
}
