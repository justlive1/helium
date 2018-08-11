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
package vip.justlive.helium.httpserver.service;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.web.RoutingContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import vip.justlive.common.base.domain.Page;
import vip.justlive.common.base.util.SnowflakeIdWorker;
import vip.justlive.common.web.vertx.JustLive;
import vip.justlive.common.web.vertx.datasource.JdbcPromise;
import vip.justlive.common.web.vertx.datasource.ModelPromise;
import vip.justlive.common.web.vertx.datasource.RepositoryFactory;
import vip.justlive.helium.base.constant.AddressTemplate;
import vip.justlive.helium.base.entity.Friend;
import vip.justlive.helium.base.entity.Notify;
import vip.justlive.helium.base.entity.Notify.STATUS;
import vip.justlive.helium.base.entity.Notify.TYPE;
import vip.justlive.helium.base.entity.User;
import vip.justlive.helium.base.repository.FriendRepository;
import vip.justlive.helium.base.repository.NotifyRepository;
import vip.justlive.helium.base.repository.UserRepository;
import vip.justlive.helium.base.session.Session;
import vip.justlive.helium.base.session.SessionManager;
import vip.justlive.helium.httpserver.session.SessionManagerImpl;
import vip.justlive.helium.httpserver.vo.Mine;
import vip.justlive.helium.httpserver.vo.MineFriend;
import vip.justlive.helium.httpserver.vo.MineNotify;

/**
 * 好友相关服务
 *
 * @author wubo
 */
public class FriendService extends BaseService {

  private final UserRepository userRepository;
  private final FriendRepository friendRepository;
  private final NotifyRepository notifyRepository;
  private final SessionManager sessionManager;

  public FriendService() {
    userRepository = RepositoryFactory.repository(UserRepository.class);
    friendRepository = RepositoryFactory.repository(FriendRepository.class);
    notifyRepository = RepositoryFactory.repository(NotifyRepository.class);
    sessionManager = new SessionManagerImpl();
  }

  /**
   * 我的*
   *
   * @param sessionId 会话id
   * @param ctx 上下文
   */
  public void mine(String sessionId, RoutingContext ctx) {
    Session session = sessionManager.getSessionById(sessionId);
    if (session == null) {
      ctx.fail(403);
    } else {
      Map<String, Object> result = new HashMap<>(16);
      // mine
      this.findMine(session.getUserId(), ctx, result).then(user -> {
        // friend
        JdbcPromise<ResultSet> friendPromise = findMineFriend(user.getId(), ctx, result);
        friendPromise.succeeded(rs -> {
          // group TODO
          result.put("group", Collections.emptyList());
          success(result, ctx);
        });
      });
    }
  }

  /**
   * 查询好友
   *
   * @param keyword 关键字
   * @param userId 用户id
   * @param pageIndex 第几页
   * @param pageSize 每页条数
   * @param ctx 上下文
   */
  public void findFriend(String keyword, Long userId, Integer pageIndex, Integer pageSize,
    RoutingContext ctx) {
    int offset = (pageIndex - 1) * pageSize;
    friendRepository.countFindFriend(keyword, userId).succeeded(r -> {
      long total = r.getLong(0);
      if (total == 0) {
        success(new Page<>(pageIndex, pageSize, total, Collections.emptyList()), ctx);
      } else {
        friendRepository.findFriend(keyword, userId, offset, pageSize).succeeded(result -> {
          List<JsonArray> list = result.getResults();
          List<Mine> friends = new ArrayList<>(10);
          if (list != null && !list.isEmpty()) {
            list.forEach(jsonArray -> friends.add(convertToMine(jsonArray)));
          }
          success(new Page<>(pageIndex, pageSize, total, friends), ctx);
        }).failed(rs -> fail(ctx));
      }
    }).failed(r -> fail(ctx));
  }

  /**
   * 添加好友
   *
   * @param from 添加者id
   * @param to 需要添加用户id
   * @param groupId 分组id
   * @param remark 附加消息
   * @param ctx 上下文
   */
  public void addFriend(Long from, Long to, Long groupId, String remark, RoutingContext ctx) {
    Notify notify = new Notify();
    notify.setId(SnowflakeIdWorker.defaultNextId());
    notify.setType(TYPE.FRIEND.value());
    notify.setFromId(from);
    notify.setToId(to);
    notify.setGroupId(groupId);
    notify.setRemark(remark);
    notify.setStatus(STATUS.PENDING.value());
    notify.setBelongTo(to);
    notify.setUnread(1);
    notifyRepository.save(notify).succeeded(r -> success("申请已发送", ctx))
      .succeeded(r -> sendNotify(to, notify.getId())).failed(r -> error("添加好友失败", ctx));
  }

  /**
   * 统计未读通知
   *
   * @param userId 用户id
   * @param ctx 上下文
   */
  public void countUnreadNotifies(Long userId, RoutingContext ctx) {
    notifyRepository.countUnreadNotifies(userId).succeeded(r -> {
      long total = r.getLong(0);
      success(total, ctx);
    }).failed(r -> fail(ctx));
  }

  /**
   * 获取我的通知
   *
   * @param userId 用户id
   * @param ctx 上下文
   */
  public void findMineNotifies(Long userId, RoutingContext ctx) {
    notifyRepository.findMineNotifies(userId).failed(r -> fail(ctx)).succeeded(rs -> {
      List<JsonArray> list = rs.getResults();
      List<MineNotify> result = new ArrayList<>(10);
      if (list != null && !list.isEmpty()) {
        list.forEach(item -> result.add(convertToMineNotify(item)));
      }
      success(result, ctx);
    }).succeeded(rs -> notifyRepository.updateReadNotifies(userId));
  }

  /**
   * 同意好友添加
   *
   * @param id 通知id
   * @param groupId 分组id
   * @param ctx 上下文
   */
  public void agreeAddFriend(Long id, Long groupId, RoutingContext ctx) {
    notifyRepository.findById(id).then(notify -> {
      if (notify == null || notify.getStatus() != STATUS.PENDING.value()) {
        fail(ctx);
      } else {
        // to 添加 from
        Friend friend = new Friend();
        friend.setId(SnowflakeIdWorker.defaultNextId());
        friend.setFriendGroupId(groupId);
        friend.setFriendUserId(notify.getFromId());
        friend.setUserId(notify.getToId());
        friendRepository.save(friend).failed(r -> fail(ctx)).succeeded(r -> {
          // from 添加 to
          friend.setId(SnowflakeIdWorker.defaultNextId());
          friend.setUserId(notify.getFromId());
          friend.setFriendUserId(notify.getToId());
          friend.setFriendGroupId(notify.getGroupId());
          friendRepository.save(friend)
            .succeeded(rs -> notifyRepository.updateNotifyStatus(id, STATUS.PASSED.value())
              .succeeded(ur -> {
                Notify agreeNotify = replyNotify(notify);
                agreeNotify.setStatus(STATUS.PASSED.value());
                notifyRepository.save(agreeNotify)
                  .succeeded(rst -> sendNotify(notify.getFromId(), agreeNotify.getId()));
              }).succeeded(ur -> success(ctx)))
            .failed(rs -> fail(ctx));
        });
      }
    }).failed(r -> fail(ctx));
  }

  /**
   * 拒绝好友添加
   *
   * @param id 通知id
   * @param ctx 上下文
   */
  public void refuseAddFriend(Long id, RoutingContext ctx) {
    notifyRepository.findById(id).then(notify -> {
      if (notify.getStatus() != STATUS.PENDING.value()) {
        fail(ctx);
        return;
      }
      //修改状态
      notifyRepository.updateNotifyStatus(id, STATUS.REFUSED.value()).failed(r -> fail(ctx))
        .succeeded(r -> {
          //记录拒绝通知
          Notify refuseNotify = replyNotify(notify);
          refuseNotify.setStatus(STATUS.REFUSED.value());
          notifyRepository.save(refuseNotify)
            .succeeded(rs -> sendNotify(notify.getFromId(), refuseNotify.getId()));
          success(ctx);
        });
    }).failed(r -> fail(ctx));
  }

  private ModelPromise<User> findMine(Long userId, RoutingContext ctx, Map<String, Object> result) {
    return userRepository.findById(userId).then(user -> {
      if (user == null) {
        ctx.fail(403);
      } else {
        Mine mine = create(user.getId().toString(), user.getUsername(), user.getNickname(),
          user.getSignature(), user.getAvatar());
        mine.setStatus("online");
        result.put("mine", mine);
      }
    });
  }

  private JdbcPromise<ResultSet> findMineFriend(Long userId, RoutingContext ctx,
    Map<String, Object> result) {
    return friendRepository.findMineFriend(userId).succeeded(resultSet -> {
      List<JsonArray> rs = resultSet.getResults();
      if (rs != null && !rs.isEmpty()) {
        Map<Long, MineFriend> friendMap = new LinkedHashMap<>();
        rs.forEach(jsonArr -> {
          Long groupId = jsonArr.getLong(0);
          MineFriend mineFriend = friendMap.get(groupId);
          if (mineFriend == null) {
            mineFriend = create(groupId.toString(), jsonArr.getString(1));
            friendMap.put(groupId, mineFriend);
          }
          if (jsonArr.getValue(3) != null) {
            Mine friend = create(jsonArr.getLong(3).toString(), jsonArr.getString(4),
              jsonArr.getString(5), jsonArr.getString(6), jsonArr.getString(7));
            friend.setStatus("online");
            mineFriend.getList().add(friend);
          }

        });
        result.put("friend", friendMap.values());
      } else {
        result.put("friend", Collections.emptyList());
      }
    }).failed(r -> fail(ctx));
  }

  private Mine create(String id, String username, String nickname, String sign, String avatar) {
    Mine mine = new Mine();
    mine.setId(id);
    mine.setUsername(username);
    mine.setNickname(nickname);
    mine.setSign(sign);
    mine.setAvatar(avatar);
    return mine;
  }

  private MineFriend create(String id, String groupname) {
    MineFriend mineFriend = new MineFriend();
    mineFriend.setId(id);
    mineFriend.setGroupname(groupname);
    mineFriend.setList(new ArrayList<>());
    return mineFriend;
  }

  private Mine convertToMine(JsonArray jsonArray) {
    return create(jsonArray.getLong(0).toString(), jsonArray.getString(1), jsonArray.getString(2),
      jsonArray.getString(3), jsonArray.getString(4));
  }

  private MineNotify convertToMineNotify(JsonArray jsonArray) {
    MineNotify notify = new MineNotify();
    notify.setId(jsonArray.getLong(0).toString());
    notify.setType(jsonArray.getInteger(1));
    notify.setStatus(jsonArray.getInteger(2));
    notify.setRemark(jsonArray.getString(3));
    notify.setCreateAt(jsonArray.getString(4));
    notify.setGroupId(jsonArray.getLong(5).toString());
    Mine mine = new Mine();
    mine.setId(jsonArray.getLong(6).toString());
    mine.setUsername(jsonArray.getString(7));
    mine.setNickname(jsonArray.getString(8));
    mine.setAvatar(jsonArray.getString(9));
    notify.setFrom(mine);
    return notify;
  }

  private void sendNotify(Long userId, Long notifyId) {
    Session session = sessionManager.getSessionById(userId.toString());
    if (session != null && session.isActive()) {
      notifyRepository.findMineNotifyById(notifyId).succeeded(jsonArray -> {
        MineNotify mineNotify = convertToMineNotify(jsonArray);
        JustLive.vertx().eventBus()
          .send(AddressTemplate.NOTIFY_SERVER_TO_USER.value().concat(userId.toString()),
            JsonObject.mapFrom(mineNotify));
      });
    }
  }

  private Notify replyNotify(Notify notify) {
    Notify replyNotify = new Notify();
    replyNotify.setId(SnowflakeIdWorker.defaultNextId());
    replyNotify.setUnread(1);
    replyNotify.setBelongTo(notify.getFromId());
    replyNotify.setExpire(0);
    replyNotify.setGroupId(notify.getGroupId());
    replyNotify.setType(TYPE.SYSTEM.value());
    replyNotify.setFromId(notify.getToId());
    replyNotify.setToId(notify.getFromId());
    return replyNotify;
  }
}
