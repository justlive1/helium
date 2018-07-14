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
