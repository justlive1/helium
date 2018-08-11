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

import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import vip.justlive.helium.base.entity.User;
import vip.justlive.helium.base.session.Session;
import vip.justlive.helium.base.session.SessionManager;

/**
 * sessionManager实现类
 *
 * @author wubo
 */
public class SessionManagerImpl implements SessionManager {

  private static final ConcurrentMap<String, Session> SESSIONIDS = new ConcurrentHashMap<>();
  private static final ConcurrentMap<String, Session> SESSIONTOKENS = new ConcurrentHashMap<>();

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
    return SESSIONIDS.get(sessionId);
  }

  @Override
  public Session getSessionByToken(String token) {
    return SESSIONTOKENS.get(token);
  }

  @Override
  public void remove(String sessionId) {
    Session session = SESSIONIDS.remove(sessionId);
    if (session != null) {
      SESSIONTOKENS.remove(session.getToken());
    }
  }
}
