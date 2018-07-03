package vip.justlive.helium.httpserver.controller;

import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;
import vip.justlive.common.web.vertx.annotation.VertxRequestParam;
import vip.justlive.common.web.vertx.annotation.VertxRoute;
import vip.justlive.common.web.vertx.annotation.VertxRouteMapping;
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

  private final UserRepository userRepository;

  public AccessController() {
    userRepository = RepositoryFactory.repository(UserRepository.class);
  }

  /**
   * 登录
   *
   * @param username 用户名
   * @param password 密码
   * @return json
   */
  @VertxRouteMapping(method = {HttpMethod.GET, HttpMethod.POST}, value = "/login")
  public void login(@VertxRequestParam("username") String username,
    @VertxRequestParam("password") String password, RoutingContext ctx) {
    userRepository.findUserByUsernameAndPassword(username, password).then(result -> {
        ctx.response().end(String.valueOf(result != null && !result.isEmpty()));
      }
    );
  }


}
