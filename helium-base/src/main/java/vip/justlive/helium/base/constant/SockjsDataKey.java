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
package vip.justlive.helium.base.constant;

/**
 * sockjs数据key
 *
 * @author wubo
 */
public enum SockjsDataKey {

  /**
   * 消息头
   */
  HEADERS("headers"),
  /**
   * 消息体
   */
  BODY("body"),
  /**
   * 时间戳
   */
  TIMESTAMP("timestamp"),
  /**
   * 消息id
   */
  MSG_ID("mid"),
  /**
   * 消息类型
   */
  MSG_CODE("code"),
  /**
   * 发送者
   */
  FROM("from"),
  /**
   * 接收者
   */
  TO("to"),
  /**
   * 数据
   */
  DATA("data"),
  /**
   * 消息
   */
  CONTENT("content"),
  /**
   * reply地址
   */
  REPLY_ADDRESS("replyAddress");

  private String val;

  SockjsDataKey(String val) {
    this.val = val;
  }

  public String value() {
    return this.val;
  }
}
