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
 * 服务器配置
 *
 * @author wubo
 */
@Data
public class ServerConf {

  /**
   * 端口
   */
  @Value("${server.port:8080}")
  private Integer port;

  /**
   * 心跳间隔
   */
  @Value("${server.sockjs.heartBeatInterval:25000}")
  private Integer sockjsHeartbeatInterval;

  /**
   * ping超时时间
   */
  @Value("${server.sockjs.pingTimeout:30000}")
  private Integer sockjsPingTimeout;

  /**
   * 登录用户权限
   */
  @Value("${server.sockjs.requiredAuthority:user}")
  private String sockjsRequiredAuthority;

  /**
   * Bridge 进入边界通配
   */
  @Value("${server.sockjs.inboundPermittedPattern:im\\..*}")
  private String sockjsInboundPermittedPattern;

  /**
   * Bridge 出去边界通配
   */
  @Value("${server.sockjs.outboundPermittedPattern:im\\..*}")
  private String sockjsOutboundPermittedPattern;
}
