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
package vip.justlive.helium.base.entity;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AbstractUser;
import io.vertx.ext.auth.AuthProvider;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import vip.justlive.common.base.annotation.Column;
import vip.justlive.common.base.annotation.Id;
import vip.justlive.common.base.annotation.Table;

/**
 * 用户属性
 *
 * @author wubo
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table
public class User extends AbstractUser implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @Column
  private Long id;

  @Column
  private String username;

  @Column
  private String password;

  /**
   * 昵称
   */
  @Column
  private String nickname;

  /**
   * 签名
   */
  @Column
  private String signature;

  @Column(name = "create_at")
  private String createAt;

  private transient JsonObject principal;

  @Override
  protected void doIsPermitted(String permission, Handler<AsyncResult<Boolean>> resultHandler) {
    resultHandler.handle(Future.succeededFuture(true));
  }

  @Override
  public JsonObject principal() {
    if (principal == null) {
      principal = new JsonObject().put("username", username);
    }
    return principal;
  }

  @Override
  public void setAuthProvider(AuthProvider authProvider) {
    // nothing to do
  }
}
