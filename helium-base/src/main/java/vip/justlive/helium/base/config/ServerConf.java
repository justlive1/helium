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

}
