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

import io.vertx.ext.web.RoutingContext;
import vip.justlive.common.web.vertx.datasource.RepositoryFactory;
import vip.justlive.helium.base.entity.Friend;
import vip.justlive.helium.base.repository.FriendRepository;
import vip.justlive.helium.base.session.Session;
import vip.justlive.helium.base.session.SessionManager;
import vip.justlive.helium.httpserver.session.SessionManagerImpl;

/**
 * 好友相关服务
 *
 * @author wubo
 */
public class FriendService extends BaseService {

  private final FriendRepository friendRepository;
  private final SessionManager sessionManager;

  public FriendService() {
    friendRepository = RepositoryFactory.repository(FriendRepository.class);
    sessionManager = new SessionManagerImpl();
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

}
