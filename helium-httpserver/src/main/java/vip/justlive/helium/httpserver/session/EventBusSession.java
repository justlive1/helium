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
package vip.justlive.helium.httpserver.session;

import vip.justlive.helium.base.session.Session;

/**
 * 基于EventBus的Session
 *
 * @author wubo
 */
public class EventBusSession extends Session {

  private static final long serialVersionUID = 1L;

  private volatile boolean active = true;

  @Override
  public boolean isActive() {
    return active;
  }

  @Override
  public boolean write(Object msg) {
    return false;
  }

  @Override
  public void login() {
    active = true;
  }

  @Override
  public void logout() {
    active = false;
  }
}
