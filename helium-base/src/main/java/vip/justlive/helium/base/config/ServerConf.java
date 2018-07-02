package vip.justlive.helium.base.config;

import lombok.Data;
import vip.justlive.common.base.annotation.Value;

/**
 * @author wubo
 */
@Data
public class ServerConf {

  @Value("${server.port:8080}")
  private Integer port;

}
