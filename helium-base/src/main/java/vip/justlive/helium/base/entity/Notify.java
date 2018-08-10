/*
 *  Copyright (C) 2018 justlive1
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
package vip.justlive.helium.base.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import vip.justlive.common.base.annotation.Column;
import vip.justlive.common.base.annotation.Id;
import vip.justlive.common.base.annotation.Table;

/**
 * 好友通知
 *
 * @author wubo
 */
@Data
@Table
public class Notify implements Serializable {

  private static final long serialVersionUID = 1L;

  public enum TYPE {
    /**
     * 系统通知
     */
    SYSTEM(-1),
    /**
     * 好友通知
     */
    FRIEND(0);
    private int value;

    TYPE(int value) {
      this.value = value;
    }

    public int value() {
      return this.value;
    }
  }

  public enum STATUS {
    /**
     * 待处理
     */
    PENDING(0),

    /**
     * 通过
     */
    PASSED(1),

    /**
     * 拒绝
     */
    REFUSED(2);

    private int value;

    STATUS(int value) {
      this.value = value;
    }

    public int value() {
      return this.value;
    }
  }

  @Id
  @Column
  private Long id;

  /**
   * 类型
   */
  @Column
  private Integer type;
  /**
   * 发送者
   */
  @Column(name = "from_id")
  private Long fromId;

  /**
   * 接收者
   */
  @Column(name = "to_id")
  private Long toId;

  @Column(name = "group_id")
  private Long groupId;

  /**
   * 状态
   */
  @Column
  private Integer status;

  /**
   * 附加信息
   */
  @Column
  private String remark;

  @Column(name = "create_at")
  private Date createAt;

  /**
   * 所属
   */
  @Column(name = "belong_to")
  private Long belongTo;

  /**
   * 失效天数
   */
  @Column
  private Integer expire;

  /**
   * 未读标记
   */
  @Column
  private Integer unread;
}
