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

import java.io.Serializable;
import java.time.ZonedDateTime;
import lombok.Data;

/**
 * 会话
 *
 * @author wubo
 */
@Data
public abstract class Session implements Serializable {

  private static final long serialVersionUID = 1L;

  private String id;

  /**
   * 用户
   */
  private String username;

  private Long userId;

  /**
   * 登录授权码
   */
  private String token;

  /**
   * 登录时间
   */
  private ZonedDateTime loginAt;

  /**
   * 是否激活状态
   *
   * @return true是激活
   */
  public abstract boolean isActive();

  /**
   * 向session写入消息
   *
   * @param msg 消息
   * @return true写入成功
   */
  public abstract boolean write(Object msg);

  /**
   * 上线
   */
  public abstract void login();

  /**
   * 下线
   */
  public abstract void logout();

}
