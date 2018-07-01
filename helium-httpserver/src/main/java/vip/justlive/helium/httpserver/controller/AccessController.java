package vip.justlive.helium.httpserver.controller;

import io.vertx.core.http.HttpMethod;
import vip.justlive.common.web.vertx.annotation.VertxRequestParam;
import vip.justlive.common.web.vertx.annotation.VertxRoute;
import vip.justlive.common.web.vertx.annotation.VertxRouteMapping;

/**
 * 访问路由
 *
 * @author wubo
 */
@VertxRoute
public class AccessController {

  /**
   * 登录
   *
   * @return
   */
  @VertxRouteMapping(method = {HttpMethod.GET, HttpMethod.POST}, value = "/login")
  public String login(@VertxRequestParam("username") String username,
      @VertxRequestParam("password") String password) {

    return username + password;
  }
}
