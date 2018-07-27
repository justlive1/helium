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
package vip.justlive.helium.base.factory;

import io.vertx.ext.auth.KeyStoreOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import vip.justlive.common.base.support.ConfigFactory;
import vip.justlive.common.web.vertx.JustLive;
import vip.justlive.common.web.vertx.auth.JdbcAuth;
import vip.justlive.common.web.vertx.datasource.DataSourceFactory;
import vip.justlive.helium.base.config.AuthConf;

/**
 * 认证工厂
 *
 * @author wubo
 */
public class AuthFactory {

  AuthFactory() {
  }

  /**
   * 获取jdbc认证
   *
   * @return JDBCAuth
   */
  public static JdbcAuth jdbcAuth() {
    return new JdbcAuth(DataSourceFactory.sharedJdbcClient());
  }

  /**
   * 获取jwt认证
   *
   * @return JWTAuth
   */
  public static JWTAuth jwtAuth() {
    AuthConf conf = ConfigFactory.load(AuthConf.class);
    KeyStoreOptions keystoreOptions = new KeyStoreOptions();
    keystoreOptions.setPassword(conf.getJwtKeystorePassword());
    keystoreOptions.setPath(conf.getJwtKeystorePath());
    keystoreOptions.setType(conf.getJwtKeystoreType());
    JWTAuthOptions config = new JWTAuthOptions().setKeyStore(keystoreOptions);
    return JWTAuth.create(JustLive.vertx(), config);
  }

}
