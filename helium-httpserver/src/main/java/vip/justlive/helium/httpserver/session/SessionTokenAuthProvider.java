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

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.auth.User;
import vip.justlive.common.web.vertx.auth.TokenAuthHandlerImpl;
import vip.justlive.common.web.vertx.datasource.RepositoryFactory;
import vip.justlive.helium.base.repository.UserRepository;
import vip.justlive.helium.base.session.Session;
import vip.justlive.helium.base.session.SessionManager;

/**
 * session token 认证提供
 *
 * @author wubo
 */
public class SessionTokenAuthProvider implements AuthProvider {

  private final SessionManager sessionManager;
  private final UserRepository userRepository;

  public SessionTokenAuthProvider(SessionManagerImpl sessionManager) {
    this.sessionManager = sessionManager;
    this.userRepository = RepositoryFactory.repository(UserRepository.class);
  }

  @Override
  public void authenticate(JsonObject authInfo, Handler<AsyncResult<User>> resultHandler) {
    String token = authInfo.getString(TokenAuthHandlerImpl.DEFAULT_TOKEN_PARAM);
    if (token == null) {
      resultHandler.handle(Future.failedFuture("authInfo must contain token field"));
      return;
    }
    Session session = sessionManager.getSessionByToken(token);
    if (session == null) {
      resultHandler.handle(Future.failedFuture("Failure in authentication"));
    } else {
      userRepository.findById(session.getUserId())
        .then(user -> resultHandler.handle(Future.succeededFuture(user)))
        .failed(e -> resultHandler.handle(Future.failedFuture(e)));
    }
  }
}
