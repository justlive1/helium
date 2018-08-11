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
import vip.justlive.common.web.vertx.annotation.VertxRequestParam;
import vip.justlive.common.web.vertx.annotation.VertxRoute;
import vip.justlive.common.web.vertx.annotation.VertxRouteMapping;
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
   * @param ctx 上下文
   */
  @VertxRouteMapping("/mine")
  public void mine(RoutingContext ctx) {
    friendService.mine(sessionId(ctx), ctx);
  }

  /**
   * 查询好友
   *
   * @param keyword 关键字
   * @param type 类型
   * @param pageIndex 第几页
   * @param pageSize 每页条数
   * @param ctx 上下文
   */
  @VertxRouteMapping("/find")
  public void find(@VertxRequestParam("keyword") String keyword,
    @VertxRequestParam("type") Integer type, @VertxRequestParam("pageIndex") Integer pageIndex,
    @VertxRequestParam("pageSize") Integer pageSize, RoutingContext ctx) {
    if (type == 0) {
      friendService.findFriend(keyword, user(ctx).getId(), pageIndex, pageSize, ctx);
    } else {
      fail(ctx);
    }
  }

  /**
   * 添加好友
   *
   * @param ctx 上下文
   */
  @VertxRouteMapping("/addFriend")
  public void addFriend(@VertxRequestParam("groupId") Long groupId,
    @VertxRequestParam("to") Long to, @VertxRequestParam("remark") String remark,
    RoutingContext ctx) {
    friendService.addFriend(user(ctx).getId(), to, groupId, remark, ctx);
  }

  /**
   * 统计未读通知
   *
   * @param ctx 上下文
   */
  @VertxRouteMapping("/countMineUnreadNotifies")
  public void countMineUnreadNotifies(RoutingContext ctx) {
    friendService.countUnreadNotifies(user(ctx).getId(), ctx);
  }

  /**
   * 获取我的通知
   *
   * @param ctx 上下文
   */
  @VertxRouteMapping("/findMineNotifies")
  public void findMineNotifies(RoutingContext ctx) {
    friendService.findMineNotifies(user(ctx).getId(), ctx);
  }

  /**
   * 同意添加好友
   *
   * @param id id
   * @param ctx 上下文
   */
  @VertxRouteMapping("/agreeAddFriend")
  public void agreeAddFriend(@VertxRequestParam("id") Long id,
    @VertxRequestParam("groupId") Long groupId, RoutingContext ctx) {
    friendService.agreeAddFriend(id, groupId, ctx);
  }

  /**
   * 拒绝添加好友
   *
   * @param id id
   * @param ctx 上下文
   */
  @VertxRouteMapping("/refuseAddFriend")
  public void refuseAddFriend(@VertxRequestParam("id") Long id, RoutingContext ctx) {
    friendService.refuseAddFriend(id, ctx);
  }
}
