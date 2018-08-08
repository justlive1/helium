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
package vip.justlive.helium.httpserver.service;

import com.google.common.collect.ImmutableMap;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.jwt.JWTOptions;
import io.vertx.ext.web.RoutingContext;
import vip.justlive.common.base.crypto.Encoder;
import vip.justlive.common.base.crypto.Md5Encoder;
import vip.justlive.common.base.support.ConfigFactory;
import vip.justlive.common.web.vertx.datasource.ModelsPromise;
import vip.justlive.common.web.vertx.datasource.RepositoryFactory;
import vip.justlive.helium.base.config.AuthConf;
import vip.justlive.helium.base.entity.FriendGroup;
import vip.justlive.helium.base.entity.User;
import vip.justlive.helium.base.factory.AuthFactory;
import vip.justlive.helium.base.repository.FriendGroupRepository;
import vip.justlive.helium.base.repository.UserRepository;
import vip.justlive.helium.base.session.SessionManager;
import vip.justlive.helium.httpserver.session.SessionManagerImpl;

/**
 * 认证服务，包含注册和登录
 *
 * @author wubo
 */
public class UserService extends BaseService {

  private final UserRepository userRepository;
  private final FriendGroupRepository friendGroupRepository;
  private final JWTAuth jwtAuth;
  private final SessionManager sessionManager;
  private final Encoder encoder;

  public UserService() {
    this.userRepository = RepositoryFactory.repository(UserRepository.class);
    this.friendGroupRepository = RepositoryFactory.repository(FriendGroupRepository.class);
    this.jwtAuth = AuthFactory.jwtAuth();
    this.sessionManager = new SessionManagerImpl();
    this.encoder = new Md5Encoder();
  }

  /**
   * 注册
   *
   * @param username 用户名
   * @param password 密码
   * @param ctx 上下文
   */
  public void register(final String username, final String password, RoutingContext ctx) {
    final User model = new User();
    model.setUsername(username);

    ModelsPromise<User> modelsPromise = userRepository.findByModel(model);
    modelsPromise.then(users -> {
      if (users == null || users.isEmpty()) {
        String raw = encoder.encode(password);
        model.setPassword(raw);
        model.setNickname(model.getUsername());
        model.setAvatar("/static/image/avatar.jpg");
        userRepository.save(model).succeeded(r -> userRepository.findByModel(model).then(u -> {
          FriendGroup group = new FriendGroup();
          group.setName("我的好友");
          group.setOrderIndex(0);
          group.setUserId(u.get(0).getId());
          friendGroupRepository.save(group);
        }).then(rs -> success(ctx)));
      } else {
        error("用户名已被注册", ctx);
      }
    });
  }

  /**
   * 登录
   *
   * @param username 用户名
   * @param password 密码
   * @param ctx 上下文
   */
  public void login(final String username, final String password, RoutingContext ctx) {
    User model = new User();
    model.setUsername(username);
    userRepository.findByModel(model).then(result -> {
      if (result == null || result.isEmpty()) {
        ctx.fail(403);
      } else {
        User user = result.get(0);
        if (encoder.match(password, user.getPassword())) {
          ctx.setUser(user);
          JWTOptions jwtOptions = new JWTOptions();
          AuthConf authConf = ConfigFactory.load(AuthConf.class);
          if (authConf.getJwtKeystoreAlgorithm() != null
            && authConf.getJwtKeystoreAlgorithm().length() > 0) {
            jwtOptions.setAlgorithm(authConf.getJwtKeystoreAlgorithm());
          }
          String token = jwtAuth.generateToken(
            new JsonObject().put("username", user.getUsername()).put("uid", user.getId()),
            jwtOptions);
          sessionManager.create(user, token);
          success(ImmutableMap.of("token", token, "uid", user.getId()), ctx);
        } else {
          ctx.fail(403);
        }
      }
    });
  }


}
