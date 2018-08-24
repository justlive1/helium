/*
 * Copyright (C) 2018 justlive1
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
 * 好友
 *
 * @author wubo
 */
@Data
@Table
public class Friend implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @Column
  private Long id;

  @Column(name = "meno_name")
  private String memoName;

  @Column(name = "user_id")
  private Long userId;

  @Column(name = "friend_user_id")
  private Long friendUserId;

  @Column(name = "friend_group_id")
  private Long friendGroupId;
}
