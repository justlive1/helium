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
import vip.justlive.common.web.vertx.annotation.VertxRequestBody;
import vip.justlive.common.web.vertx.annotation.VertxRoute;
import vip.justlive.common.web.vertx.annotation.VertxRouteMapping;
import vip.justlive.helium.base.entity.Friend;
import vip.justlive.helium.httpserver.service.FriendService;

/**
 * 好友相关路由
 *
 * @author wubo
 */
@VertxRoute("/interface/friend")
public class FriendController extends BaseController {

  private final FriendService friendService;

  public FriendController() {
    friendService = new FriendService();
  }

  /**
   * 我的*
   *
   * @param ctx
   */
  @VertxRouteMapping("/mine")
  public void mine(RoutingContext ctx) {
    friendService.mine(sessionId(ctx), ctx);
  }

  /**
   * 添加好友
   *
   * @param friend 好友信息
   * @param ctx 上下文
   */
  @VertxRouteMapping("/addFriend")
  public void addFriend(@VertxRequestBody Friend friend, RoutingContext ctx) {
    if (friend.getFriendGroupId() == null || friend.getFriendUserId() == null) {
      ctx.fail(400);
    } else {
      friendService.addFriend(friend, sessionId(ctx), ctx);
    }
  }

}
