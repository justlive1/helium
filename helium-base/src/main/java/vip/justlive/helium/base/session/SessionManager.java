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
   * @param token 登录授权码
   * @return 会话
   */
  Session create(User user, String token);

  /**
   * 获取session
   *
   * @param sessionId 会话id
   * @return 会话
   */
  Session getSession(String sessionId);

  /**
   * 删除session
   *
   * @param sessionId 会话id
   */
  void remove(String sessionId);

}
