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
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.web.RoutingContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import vip.justlive.common.web.vertx.datasource.JdbcPromise;
import vip.justlive.common.web.vertx.datasource.ModelPromise;
import vip.justlive.common.web.vertx.datasource.RepositoryFactory;
import vip.justlive.helium.base.entity.Friend;
import vip.justlive.helium.base.entity.User;
import vip.justlive.helium.base.repository.FriendRepository;
import vip.justlive.helium.base.repository.UserRepository;
import vip.justlive.helium.base.session.Session;
import vip.justlive.helium.base.session.SessionManager;
import vip.justlive.helium.httpserver.session.SessionManagerImpl;
import vip.justlive.helium.httpserver.vo.Mine;
import vip.justlive.helium.httpserver.vo.MineFriend;

/**
 * 好友相关服务
 *
 * @author wubo
 */
public class FriendService extends BaseService {

  private final UserRepository userRepository;
  private final FriendRepository friendRepository;
  private final SessionManager sessionManager;

  public FriendService() {
    userRepository = RepositoryFactory.repository(UserRepository.class);
    friendRepository = RepositoryFactory.repository(FriendRepository.class);
    sessionManager = new SessionManagerImpl();
  }

  /**
   * 我的*
   *
   * @param sessionId 会话id
   * @param ctx 上下文
   */
  public void mine(String sessionId, RoutingContext ctx) {
    Session session = sessionManager.getSession(sessionId);
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
   * 添加好友
   *
   * @param friend 好友信息
   * @param sessionId 会话id
   * @param ctx 上下文
   */
  public void addFriend(Friend friend, String sessionId, RoutingContext ctx) {
    Session session = sessionManager.getSession(sessionId);
    if (session == null) {
      ctx.fail(403);
    } else {
      friend.setUserId(session.getUserId());
      friendRepository.save(friend).succeeded(r -> success("添加好友成功", ctx))
        .failed(r -> error("添加好友失败", ctx));
    }
  }

  private ModelPromise<User> findMine(Long userId, RoutingContext ctx, Map<String, Object> result) {
    return userRepository.findById(userId).then(user -> {
      if (user == null) {
        ctx.fail(403);
      } else {
        Mine mine = create(user.getUsername(), user.getNickname(), user.getSignature());
        mine.setStatus("online");
        mine.setAvatar("/static/image/avatar.jpg");
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
            Mine friend = create(jsonArr.getLong(3).toString(), jsonArr.getString(5),
              jsonArr.getString(6));
            friend.setAvatar("/static/image/avatar.jpg");
            friend.setStatus("online");
            mineFriend.getList().add(friend);
          }

        });
        result.put("friend", friendMap.values());
      } else {
        result.put("friend", Collections.emptyList());
      }
    }).failed(r -> ctx.fail(500));
  }

  private Mine create(String id, String username, String sign) {
    Mine mine = new Mine();
    mine.setId(id);
    mine.setUsername(username);
    mine.setSign(sign);
    return mine;
  }

  private MineFriend create(String id, String groupname) {
    MineFriend mineFriend = new MineFriend();
    mineFriend.setId(id);
    mineFriend.setGroupname(groupname);
    mineFriend.setList(new ArrayList<>());
    return mineFriend;
  }
}
