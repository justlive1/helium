package vip.justlive.helium.base;

import lombok.Data;
import vip.justlive.common.base.annotation.Value;

/**
 * 配置
 *
 * @author wubo
 */
@Data
public class Conf {

  @Value("${server.port:8080}")
  private Integer port;

}
