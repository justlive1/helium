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
package vip.justlive.helium.base.session;

import vip.justlive.helium.base.entity.User;

/**
 * session管理
 *
 * @author wubo
 */
public interface SessionManager {

  /**
   * 创建session
   *
   * @param user 用户
   * @return 会话
   */
  Session create(User user);

  /**
   * 获取session
   *
   * @param token token
   * @return 会话
   */
  Session getSessionByToken(String token);

  /**
   * 获取session
   *
   * @param sessionId 会话id
   * @return 会话
   */
  Session getSessionById(String sessionId);

  /**
   * 删除session
   *
   * @param sessionId 会话id
   */
  void remove(String sessionId);

  /**
   * 用户是否登录
   *
   * @param userId 用户id
   * @return true是登录
   */
  boolean isOnline(Long userId);

  /**
   * 用户上线
   *
   * @param userId 用户id
   */
  void login(Long userId);

  /**
   * 用户下线
   *
   * @param userId 用户id
   */
  void logout(Long userId);

}
