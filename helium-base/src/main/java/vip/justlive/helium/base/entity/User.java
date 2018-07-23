package vip.justlive.helium.base.entity;

import java.io.Serializable;
import lombok.Data;
import vip.justlive.common.base.annotation.Column;
import vip.justlive.common.base.annotation.Id;
import vip.justlive.common.base.annotation.Table;

/**
 * 用户属性
 *
 * @author wubo
 */
@Data
@Table
public class User implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @Column
  private Long id;

  @Column
  private String username;

  @Column
  private String password;

  @Column(name = "password_salt")
  private String passwordSalt;

}
