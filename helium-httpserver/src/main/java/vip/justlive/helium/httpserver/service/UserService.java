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
import io.vertx.ext.web.RoutingContext;
import vip.justlive.common.base.annotation.Inject;
import vip.justlive.common.base.annotation.Singleton;
import vip.justlive.common.base.crypto.Encoder;
import vip.justlive.common.base.util.SnowflakeIdWorker;
import vip.justlive.common.web.vertx.datasource.ModelsPromise;
import vip.justlive.helium.base.entity.FriendGroup;
import vip.justlive.helium.base.entity.User;
import vip.justlive.helium.base.repository.ChatLogRepository;
import vip.justlive.helium.base.repository.FriendGroupRepository;
import vip.justlive.helium.base.repository.UserRepository;
import vip.justlive.helium.base.service.UploadService;
import vip.justlive.helium.base.session.SessionManager;
import vip.justlive.helium.httpserver.vo.Mine;

/**
 * 认证服务，包含注册和登录
 *
 * @author wubo
 */
@Singleton
public class UserService extends BaseService {

  private final UserRepository userRepository;
  private final FriendGroupRepository friendGroupRepository;
  private final ChatLogRepository chatLogRepository;
  private final SessionManager sessionManager;
  private final Encoder encoder;
  private final UploadService uploadService;

  @Inject
  public UserService(UserRepository userRepository, FriendGroupRepository friendGroupRepository,
    ChatLogRepository chatLogRepository, SessionManager sessionManager, Encoder encoder,
    UploadService uploadService) {
    this.userRepository = userRepository;
    this.friendGroupRepository = friendGroupRepository;
    this.chatLogRepository = chatLogRepository;
    this.sessionManager = sessionManager;
    this.encoder = encoder;
    this.uploadService = uploadService;
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
        model.setId(SnowflakeIdWorker.defaultNextId());
        model.setPassword(raw);
        model.setAvatar("/static/image/avatar.jpg");
        userRepository.save(model).failed(r -> fail(r, ctx)).succeeded(r -> {
          FriendGroup group = new FriendGroup();
          group.setId(SnowflakeIdWorker.defaultNextId());
          group.setName("我的好友");
          group.setOrderIndex(0);
          group.setUserId(model.getId());
          friendGroupRepository.save(group);
        }).succeeded(rs -> success(ctx));
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
        ctx.fail(401);
      } else {
        User user = result.get(0);
        if (encoder.match(password, user.getPassword())) {
          ctx.setUser(user);
          String token = sessionManager.create(user).getToken();
          success(ImmutableMap.of("token", token, "uid", user.getId().toString()), ctx);
        } else {
          ctx.fail(401);
        }
      }
    });
  }

  /**
   * 修改签名
   *
   * @param userId 用户id
   * @param signature 签名
   */
  public void updateSignature(long userId, String signature, RoutingContext ctx) {
    User user = new User();
    user.setId(userId);
    user.setSignature(signature);
    userRepository.update(user).failed(r -> fail(r, ctx)).succeeded(r -> success(ctx));
  }

  public void online(long userId, long friendId, RoutingContext ctx) {
    chatLogRepository.updateReadByFriendId(userId, friendId);
    userRepository.findById(friendId).then(user -> {
      Mine mine = new Mine();
      mine.setId(user.getId().toString());
      mine.setAvatar(user.getAvatar());
      mine.setSign(user.getSignature());
      if (sessionManager.isOnline(friendId)) {
        mine.setStatus(Mine.ONLINE);
      } else {
        mine.setStatus(Mine.OFFLINE);
      }
      success(mine, ctx);
    }).failed(r -> fail(r, ctx));
  }

  public void avatar(long userId, String img, RoutingContext ctx) {
    String url = uploadService.uploadBase64Png(img);
    User user = new User();
    user.setId(userId);
    user.setAvatar(url);
    userRepository.update(user).failed(r -> fail(r, ctx))
      .succeeded(r -> success(url, ctx));
  }
}
