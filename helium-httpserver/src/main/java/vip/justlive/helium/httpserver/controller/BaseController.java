/*
 *  Copyright (C) 2018 justlive1
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

import io.vertx.ext.web.RoutingContext;
import vip.justlive.helium.base.entity.User;

/**
 * base controller
 *
 * @author wubo
 */
public class BaseController {

  /**
   * 获取登录用户
   *
   * @param ctx 上下文
   * @return 用户
   */
  protected User user(RoutingContext ctx) {
    return (User) ctx.user();
  }

  /**
   * 获取会话id
   *
   * @param ctx 上下文
   * @return sessionId
   */
  protected String sessionId(RoutingContext ctx) {
    return user(ctx).getId().toString();
  }

  /**
   * 请求有误
   *
   * @param ctx 上下文
   */
  protected void fail(RoutingContext ctx) {
    ctx.fail(400);
  }
}
