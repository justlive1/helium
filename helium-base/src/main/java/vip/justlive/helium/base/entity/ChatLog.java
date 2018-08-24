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
import lombok.Data;
import vip.justlive.common.base.annotation.Column;
import vip.justlive.common.base.annotation.Id;
import vip.justlive.common.base.annotation.Table;

/**
 * 聊天记录
 *
 * @author wubo
 */
@Data
@Table(name = "chat_log")
public class ChatLog implements Serializable {

  private static final long serialVersionUID = 1L;

  public enum TYPE {
    /**
     * 文本消息
     */
    TEXT(1);

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
     * 未读
     */
    UNREAD(0),
    /**
     * 已读
     */
    READ(1);

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
   * 发送者
   */
  @Column(name = "from_id")
  private Long fromId;

  /**
   * 接收者
   */
  @Column(name = "to_id")
  private Long toId;

  /**
   * 类型
   */
  @Column
  private Integer type;

  /**
   * 内容
   */
  @Column
  private String content;

  /**
   * 状态
   */
  @Column
  private Integer status;

  /**
   * 发送时间
   */
  @Column
  private Long timestamp;

}
