package vip.justlive.helium.repository.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.Data;

/**
 * 用户信息
 *
 * @author wubo
 */
@Data
@Entity
public class User {

  @Id
  @GeneratedValue
  private Long id;

  @Column
  private String username;

  @Column
  private String password;
}
