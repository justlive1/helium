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
import vip.justlive.common.base.annotation.Inject;
import vip.justlive.common.web.vertx.annotation.VertxRequestBody;
import vip.justlive.common.web.vertx.annotation.VertxRequestParam;
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
public class UserController extends BaseController {

  private final UserService userService;

  @Inject
  public UserController(UserService userService) {
    this.userService = userService;
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
      fail(ctx);
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
      fail(ctx);
    } else {
      userService.login(user.getUsername(), user.getPassword(), ctx);
    }
  }

  /**
   * 修改签名
   *
   * @param signature 签名
   * @param ctx 上下文
   */
  @VertxRouteMapping("/interface/user/updateSignature")
  public void updateSignature(@VertxRequestParam("signature") String signature,
    RoutingContext ctx) {
    userService.updateSignature(user(ctx).getId(), signature, ctx);
  }

  /**
   * 用户是否在线
   *
   * @param id 用户id
   * @param ctx 上下文
   */
  @VertxRouteMapping("/interface/user/online")
  public void online(@VertxRequestParam("id") Long id, RoutingContext ctx) {
    userService.online(user(ctx).getId(), id, ctx);
  }

  /**
   * 修改头像
   *
   * @param img 图片
   * @param ctx 上下文
   */
  @VertxRouteMapping("/interface/user/avatar")
  public void avatar(@VertxRequestParam("img") String img, RoutingContext ctx) {
    userService.avatar(user(ctx).getId(), img, ctx);
  }
}
