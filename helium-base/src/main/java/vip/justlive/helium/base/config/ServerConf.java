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
}
