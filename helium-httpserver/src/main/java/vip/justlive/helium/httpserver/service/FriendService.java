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
import java.util.Objects;
import vip.justlive.common.base.annotation.Inject;
import vip.justlive.common.base.annotation.Singleton;
import vip.justlive.common.base.domain.Page;
import vip.justlive.common.base.util.SnowflakeIdWorker;
import vip.justlive.common.web.vertx.JustLive;
import vip.justlive.common.web.vertx.datasource.JdbcPromise;
import vip.justlive.common.web.vertx.datasource.ModelPromise;
import vip.justlive.helium.base.constant.AddressTemplate;
import vip.justlive.helium.base.entity.Friend;
import vip.justlive.helium.base.entity.FriendGroup;
import vip.justlive.helium.base.entity.Notify;
import vip.justlive.helium.base.entity.Notify.STATUS;
import vip.justlive.helium.base.entity.Notify.TYPE;
import vip.justlive.helium.base.entity.User;
import vip.justlive.helium.base.repository.FriendGroupRepository;
import vip.justlive.helium.base.repository.FriendRepository;
import vip.justlive.helium.base.repository.NotifyRepository;
import vip.justlive.helium.base.repository.UserRepository;
import vip.justlive.helium.base.session.Session;
import vip.justlive.helium.base.session.SessionManager;
import vip.justlive.helium.httpserver.vo.Mine;
import vip.justlive.helium.httpserver.vo.MineFriend;
import vip.justlive.helium.httpserver.vo.MineNotify;

/**
 * 好友相关服务
 *
 * @author wubo
 */
@Singleton
public class FriendService extends BaseService {

  private final UserRepository userRepository;
  private final FriendRepository friendRepository;
  private final NotifyRepository notifyRepository;
  private final FriendGroupRepository friendGroupRepository;
  private final SessionManager sessionManager;

  @Inject
  public FriendService(UserRepository userRepository, FriendRepository friendRepository,
    NotifyRepository notifyRepository, FriendGroupRepository friendGroupRepository,
    SessionManager sessionManager) {
    this.userRepository = userRepository;
    this.friendRepository = friendRepository;
    this.notifyRepository = notifyRepository;
    this.friendGroupRepository = friendGroupRepository;
    this.sessionManager = sessionManager;
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
        }).failed(rs -> fail(rs, ctx));
      }
    }).failed(r -> fail(r, ctx));
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
    }).failed(r -> fail(r, ctx));
  }

  /**
   * 获取我的通知
   *
   * @param userId 用户id
   * @param ctx 上下文
   */
  public void findMineNotifies(Long userId, RoutingContext ctx) {
    notifyRepository.findMineNotifies(userId).failed(r -> fail(r, ctx)).succeeded(rs -> {
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
        fail(null, ctx);
      } else {
        // to 添加 from
        Friend friend = new Friend();
        friend.setId(SnowflakeIdWorker.defaultNextId());
        friend.setFriendGroupId(groupId);
        friend.setFriendUserId(notify.getFromId());
        friend.setUserId(notify.getToId());
        friendRepository.save(friend).failed(r -> fail(r, ctx)).succeeded(r -> {
          // from 添加 to
          friend.setId(SnowflakeIdWorker.defaultNextId());
          friend.setUserId(notify.getFromId());
          friend.setFriendUserId(notify.getToId());
          friend.setFriendGroupId(notify.getGroupId());
          friendRepository.save(friend).succeeded(
            rs -> notifyRepository.updateNotifyStatus(id, STATUS.PASSED.value()).succeeded(ur -> {
              Notify agreeNotify = replyNotify(notify);
              agreeNotify.setStatus(STATUS.PASSED.value());
              notifyRepository.save(agreeNotify)
                .succeeded(rst -> sendNotify(notify.getFromId(), agreeNotify.getId()));
            }).succeeded(ur -> success(ctx))).failed(rs -> fail(rs, ctx));
        });
      }
    }).failed(r -> fail(r, ctx));
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
        fail(null, ctx);
        return;
      }
      //修改状态
      notifyRepository.updateNotifyStatus(id, STATUS.REFUSED.value()).failed(r -> fail(r, ctx))
        .succeeded(r -> {
          //记录拒绝通知
          Notify refuseNotify = replyNotify(notify);
          refuseNotify.setStatus(STATUS.REFUSED.value());
          notifyRepository.save(refuseNotify)
            .succeeded(rs -> sendNotify(notify.getFromId(), refuseNotify.getId()));
          success(ctx);
        });
    }).failed(r -> fail(r, ctx));
  }

  /**
   * 添加好友分组
   *
   * @param name 名称
   * @param userId 用户id
   * @param ctx 上下文
   */
  public void addFriendGroup(String name, Long userId, RoutingContext ctx) {
    FriendGroup group = new FriendGroup();
    group.setUserId(userId);
    friendGroupRepository.findByModel(group).then(list -> {
      if (list != null) {
        for (FriendGroup friendGroup : list) {
          if (Objects.equals(name, friendGroup.getName())) {
            error("分组名已存在", ctx);
            return;
          }
        }
        group.setOrderIndex(list.size() + 1);
      }
      group.setName(name);
      group.setId(SnowflakeIdWorker.defaultNextId());
      friendGroupRepository.save(group).succeeded(r -> success(group.getId().toString(), ctx))
        .failed(r -> fail(r, ctx));
    }).failed(r -> fail(r, ctx));
  }

  /**
   * 修改分组名称
   *
   * @param name 分组名
   * @param id 分组id
   * @param userId 用户id
   * @param ctx 上下文
   */
  public void updateFriendGroupName(String name, Long id, Long userId, RoutingContext ctx) {
    FriendGroup group = new FriendGroup();
    group.setUserId(userId);
    friendGroupRepository.findByModel(group).then(list -> {
      if (list != null) {
        for (FriendGroup friendGroup : list) {
          if (!friendGroup.getId().equals(id) && Objects.equals(name, friendGroup.getName())) {
            error("分组名已存在", ctx);
            return;
          }
        }
      }
      friendGroupRepository.updateGroupName(name, id).succeeded(r -> success(ctx))
        .failed(r -> fail(r, ctx));
    }).failed(r -> fail(r, ctx));
  }

  /**
   * 删除好友分组
   *
   * @param id 分组id
   * @param userId 用户id
   * @param ctx 上下文
   */
  public void delFriendGroup(Long id, Long userId, RoutingContext ctx) {
    friendRepository.countGroupFriend(id).failed(r -> fail(r, ctx)).succeeded(jsonArray -> {
      if (jsonArray != null && jsonArray.getLong(0) > 1) {
        error("该分组下存在好友，不能删除", ctx);
      } else {
        FriendGroup group = new FriendGroup();
        group.setUserId(userId);
        friendGroupRepository.findByModel(group).then(list -> {
          if (list == null || list.size() < 2) {
            error("至少保留一个分组", ctx);
          } else {
            friendGroupRepository.deleteGroup(id).succeeded(r -> success(ctx))
              .failed(r -> fail(r, ctx));
          }
        }).failed(r -> fail(r, ctx));
      }
    });
  }

  /**
   * 删除好友
   *
   * @param userId 用户id
   * @param friendId 好友id
   * @param ctx 上下文
   */
  public void delFriend(long userId, long friendId, RoutingContext ctx) {
    friendRepository.deleteFriend(userId, friendId).failed(r -> fail(r, ctx))
      .succeeded(r -> success(ctx));
  }

  /**
   * 修改好友备注
   *
   * @param userId 用户id
   * @param friendId 好友id
   * @param memo 备注名
   * @param ctx 上下文
   */
  public void updateFriendMemo(long userId, long friendId, String memo, RoutingContext ctx) {
    friendRepository.updateFriendMemo(userId, friendId, memo).failed(r -> fail(r, ctx))
      .succeeded(r -> success(ctx));
  }

  private ModelPromise<User> findMine(Long userId, RoutingContext ctx, Map<String, Object> result) {
    return userRepository.findById(userId).then(user -> {
      if (user == null) {
        ctx.fail(403);
      } else {
        Mine mine = create(user.getId().toString(), user.getUsername(), null, user.getSignature(),
          user.getAvatar());
        if (sessionManager.isOnline(user.getId())) {
          mine.setStatus(Mine.ONLINE);
        } else {
          mine.setStatus(Mine.OFFLINE);
        }
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
    }).failed(r -> fail(r, ctx));
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
    return create(jsonArray.getLong(0).toString(), jsonArray.getString(1), null,
      jsonArray.getString(2), jsonArray.getString(3));
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
    mine.setAvatar(jsonArray.getString(8));
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
