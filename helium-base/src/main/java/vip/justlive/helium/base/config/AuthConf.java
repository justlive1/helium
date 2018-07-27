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
package vip.justlive.helium.base.config;

import lombok.Data;
import vip.justlive.common.base.annotation.Value;

/**
 * 认证配置
 *
 * @author wubo
 */
@Data
public class AuthConf {

  /**
   * 需要认证的路径
   */
  @Value("${auth.authUrlPattern:/api/*}")
  private String authUrlPattern;

  /**
   * jwt认证类型
   */
  @Value("${auth.jwt.keystore.type:jceks}")
  private String jwtKeystoreType;

  /**
   * jwt认证证书路径
   */
  @Value("${auth.jwt.keystore.path}")
  private String jwtKeystorePath;

  /**
   * jwt认证密码
   */
  @Value("${auth.jwt.keystore.password}")
  private String jwtKeystorePassword;

  /**
   * jwt证书算法
   */
  @Value("${auth.jwt.keystore.algorithm:HS512}")
  private String jwtKeystoreAlgorithm;
}
