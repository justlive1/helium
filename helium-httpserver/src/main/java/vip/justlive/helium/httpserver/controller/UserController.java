/*
 * Copyright (C) 2018 justlive1
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package vip.justlive.helium.httpserver.controller;

import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.lang3.StringUtils;
import vip.justlive.common.web.vertx.annotation.VertxRequestBody;
import vip.justlive.common.web.vertx.annotation.VertxRoute;
import vip.justlive.common.web.vertx.annotation.VertxRouteMapping;
import vip.justlive.helium.base.entity.User;
import vip.justlive.helium.httpserver.service.UserService;

/**
 * 用户路由
 *
 * @author wubo
 */
@VertxRoute
public class UserController {

  private final UserService userService;

  public UserController() {
    userService = new UserService();
  }

  /**
   * 注册
   *
   * @param user 用户信息
   * @param ctx 上下文
   */
  @VertxRouteMapping(value = "/register", method = {HttpMethod.POST})
  public void register(@VertxRequestBody User user, RoutingContext ctx) {
    if (!StringUtils.isNoneBlank(user.getUsername(), user.getPassword())) {
      ctx.fail(400);
    } else {
      userService.register(user.getUsername(), user.getPassword(), ctx);
    }
  }

  /**
   * 登录
   *
   * @param user 登录用户
   * @param ctx 上下文
   */
  @VertxRouteMapping(value = "/login", method = {HttpMethod.POST})
  public void login(@VertxRequestBody User user, RoutingContext ctx) {
    if (!StringUtils.isNoneBlank(user.getUsername(), user.getPassword())) {
      ctx.fail(400);
    } else {
      userService.login(user.getUsername(), user.getPassword(), ctx);
    }
  }
}
