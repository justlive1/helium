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
 * 地址模板
 *
 * @author wubo
 */
public enum AddressTemplate {

  /**
   * 用户向服务器发送通知
   */
  USER_TO_SERVER("im.server"),
  /**
   * 用户私聊
   */
  MSG_USER_TO_USER("im.user."),
  /**
   * 服务器向用户发送通知
   */
  NOTIFY_SERVER_TO_USER("im.notify.user.");

  private String address;

  AddressTemplate(String address) {
    this.address = address;
  }

  public String value() {
    return address;
  }

}
