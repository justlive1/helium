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
package vip.justlive.helium.httpserver.controller;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.UpdateResult;
import io.vertx.ext.web.RoutingContext;
import vip.justlive.common.base.domain.Response;
import vip.justlive.common.web.vertx.annotation.VertxRequestBody;
import vip.justlive.common.web.vertx.annotation.VertxRoute;
import vip.justlive.common.web.vertx.annotation.VertxRouteMapping;
import vip.justlive.common.web.vertx.auth.JdbcAuth;
import vip.justlive.common.web.vertx.datasource.JdbcPromise;
import vip.justlive.common.web.vertx.datasource.ModelsPromise;
import vip.justlive.common.web.vertx.datasource.RepositoryFactory;
import vip.justlive.helium.base.entity.User;
import vip.justlive.helium.base.factory.AuthFactory;
import vip.justlive.helium.base.repository.UserRepository;

/**
 * 用户路由
 *
 * @author wubo
 */
@VertxRoute
public class UserController {

  private final UserRepository userRepository;
  private final JdbcAuth jdbcAuth;

  public UserController() {
    this.userRepository = RepositoryFactory.repository(UserRepository.class);
    this.jdbcAuth = AuthFactory.jdbcAuth();
  }

  @VertxRouteMapping(value = "/register", method = {HttpMethod.POST})
  public void register(@VertxRequestBody User user, RoutingContext ctx) {

    User model = new User();
    model.setUsername(user.getUsername());

    ModelsPromise<User> modelsPromise = userRepository.findByModel(model);
    modelsPromise.then(users -> {
      if (users == null || users.isEmpty()) {
        String raw = jdbcAuth.encode(user.getPassword());
        user.setPassword(raw);
        JdbcPromise<UpdateResult> promise = userRepository.save(user);
        promise.succeeded(rs -> ctx.response()
          .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON.toString())
          .end(JsonObject.mapFrom(Response.success()).toBuffer())
        );
      } else {
        ctx.response()
          .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON.toString())
          .end(JsonObject.mapFrom(Response.error("用户名已被注册")).toBuffer());
      }
    });
  }
}
