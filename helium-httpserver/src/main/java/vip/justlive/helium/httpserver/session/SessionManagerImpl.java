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
package vip.justlive.helium.httpserver.session;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.vertx.ext.web.handler.SessionHandler;
import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import vip.justlive.common.base.annotation.Singleton;
import vip.justlive.helium.base.entity.User;
import vip.justlive.helium.base.session.Session;
import vip.justlive.helium.base.session.SessionManager;

/**
 * sessionManager实现类
 *
 * @author wubo
 */
@Singleton
public class SessionManagerImpl implements SessionManager {

  private static final Cache<String, Session> SESSIONIDS = CacheBuilder.newBuilder()
    .expireAfterAccess(SessionHandler.DEFAULT_SESSION_TIMEOUT, TimeUnit.MILLISECONDS).build();
  private static final Cache<String, Session> SESSIONTOKENS = CacheBuilder.newBuilder()
    .expireAfterAccess(SessionHandler.DEFAULT_SESSION_TIMEOUT, TimeUnit.MILLISECONDS).build();

  @Override
  public Session create(User user) {
    Session session = new EventBusSession();
    session.setId(user.getId().toString());
    session.setUsername(user.getUsername());
    session.setUserId(user.getId());
    session.setToken(UUID.randomUUID().toString());
    session.setLoginAt(ZonedDateTime.now());
    SESSIONIDS.put(session.getId(), session);
    SESSIONTOKENS.put(session.getToken(), session);
    return session;
  }

  @Override
  public Session getSessionById(String sessionId) {
    return SESSIONIDS.getIfPresent(sessionId);
  }

  @Override
  public Session getSessionByToken(String token) {
    return SESSIONTOKENS.getIfPresent(token);
  }

  @Override
  public void remove(String sessionId) {
    Session session = SESSIONIDS.getIfPresent(sessionId);
    if (session != null) {
      SESSIONIDS.invalidate(sessionId);
      SESSIONTOKENS.invalidate(session.getToken());
    }
  }

  @Override
  public boolean isOnline(Long userId) {
    Session session = SESSIONIDS.getIfPresent(userId.toString());
    return session != null && session.isActive();
  }

  @Override
  public void login(Long userId) {
    Session session = SESSIONIDS.getIfPresent(userId.toString());
    if (session != null) {
      session.login();
    }
  }

  @Override
  public void logout(Long userId) {
    Session session = SESSIONIDS.getIfPresent(userId.toString());
    if (session != null) {
      session.logout();
    }
  }
}
