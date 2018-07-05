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

  public enum PROVIDER_TYPE {
    /**
     * 不进行认证
     */
    NONE,
    /**
     * jdbc认证
     */
    JDBC
  }

  public enum AUTH_TYPE {
    /**
     * Basic Auth
     */
    BASIC
  }

  /**
   * 认证提供类型
   */
  @Value("${auth.providerType:NONE}")
  private String providerType;

  /**
   * 使用认证类型
   */
  @Value("${auth.authUserd:BASIC}")
  private String[] authUsed;

  /**
   * 需要认证的路径
   */
  @Value("${auth.authUrlPattern:/**}")
  private String authUrlPattern;

}
