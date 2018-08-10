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
package vip.justlive.helium.httpserver.service;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.bridge.BridgeEventType;
import io.vertx.ext.web.handler.sockjs.BridgeEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import lombok.extern.slf4j.Slf4j;

/**
 * sockjs服务
 *
 * @author wubo
 */
@Slf4j
public class SockjsService implements Handler<BridgeEvent> {

  private Map<BridgeEventType, BiConsumer<Long, JsonObject>> functions = new HashMap<>(8);

  public SockjsService() {
    init();
  }

  private void init() {
    functions.put(BridgeEventType.SEND, this::handleForSend);
  }

  @Override
  public void handle(BridgeEvent event) {
    BiConsumer<Long, JsonObject> consumer = functions.get(event.type());
    if (consumer != null) {
      Long uid = event.socket().webUser().principal().getLong("uid");
      JsonObject data = event.getRawMessage();
      consumer.accept(uid, data);
      event.setRawMessage(data);
    }
    event.complete(true);
  }

  private void handleForSend(Long uid, JsonObject data) {
    if (log.isDebugEnabled()) {
      log.debug("uid[{}] data[{}]", uid, data);
    }

    JsonObject headers = data.getJsonObject("headers", new JsonObject());
    //添加时间戳
    headers.put("timestamp", System.currentTimeMillis());
    data.put("headers", headers);

  }

}
