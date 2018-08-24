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
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.bridge.BridgeEventType;
import io.vertx.ext.web.handler.sockjs.BridgeEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import lombok.extern.slf4j.Slf4j;
import vip.justlive.common.base.annotation.Inject;
import vip.justlive.common.base.annotation.Singleton;
import vip.justlive.common.base.domain.Response;
import vip.justlive.common.base.util.SnowflakeIdWorker;
import vip.justlive.common.web.vertx.JustLive;
import vip.justlive.helium.base.constant.AddressTemplate;
import vip.justlive.helium.base.constant.MessageCode;
import vip.justlive.helium.base.constant.SockjsDataKey;
import vip.justlive.helium.base.entity.ChatLog;
import vip.justlive.helium.base.entity.ChatLog.STATUS;
import vip.justlive.helium.base.entity.ChatLog.TYPE;
import vip.justlive.helium.base.entity.User;
import vip.justlive.helium.base.repository.ChatLogRepository;
import vip.justlive.helium.base.session.SessionManager;
import vip.justlive.helium.httpserver.vo.MineChat;

/**
 * sockjs服务
 *
 * @author wubo
 */
@Slf4j
@Singleton
public class SockjsService implements Handler<BridgeEvent> {

  private Map<BridgeEventType, BiConsumer<Long, JsonObject>> functions = new HashMap<>(8);
  private Map<String, BiConsumer<Long, JsonObject>> msgFunctions = new HashMap<>(8);
  private static final BiConsumer<Long, JsonObject> EMPTY_FUNCTION = (a, b) -> {
  };

  private final ChatLogRepository chatLogRepository;
  private final SessionManager sessionManager;

  @Inject
  public SockjsService(ChatLogRepository chatLogRepository, SessionManager sessionManager) {
    this.chatLogRepository = chatLogRepository;
    this.sessionManager = sessionManager;
    initFunctions();
    initServerReply();
  }

  private void initFunctions() {
    functions.put(BridgeEventType.SOCKET_CREATED, this::handleForSocketCreated);
    functions.put(BridgeEventType.SOCKET_CLOSED, this::handleForSocketClosed);
    functions.put(BridgeEventType.SOCKET_PING, this::handleForSocketPing);
    functions.put(BridgeEventType.SEND, this::handleForSend);

    msgFunctions.put(MessageCode.M_200.name(), this::handleForM200);
    msgFunctions.put(MessageCode.M_300.name(), this::handleForM300);
  }

  private void initServerReply() {
    JustLive.vertx().eventBus().consumer(AddressTemplate.USER_TO_SERVER.value(), this::replyToM200);
  }

  @Override
  public void handle(BridgeEvent event) {
    BiConsumer<Long, JsonObject> consumer = functions.getOrDefault(event.type(), EMPTY_FUNCTION);
    Long uid = ((User) event.socket().webUser()).getId();
    JsonObject data = event.getRawMessage();
    consumer.accept(uid, data);
    event.setRawMessage(data);
    event.complete(true);
  }

  /**
   * socket创建
   *
   * @param uid 用户id
   * @param data 数据
   */
  private void handleForSocketCreated(Long uid, JsonObject data) {
    if (log.isDebugEnabled()) {
      log.debug("socket created event [{}] [{}]", uid, data);
    }
    sessionManager.login(uid);
  }

  /**
   * socket关闭
   *
   * @param uid 用户id
   * @param data 数据
   */
  private void handleForSocketClosed(Long uid, JsonObject data) {
    if (log.isDebugEnabled()) {
      log.debug("socket closed event [{}] [{}]", uid, data);
    }
    sessionManager.logout(uid);
  }

  /**
   * socket ping
   *
   * @param uid 用户id
   * @param data 数据
   */
  private void handleForSocketPing(Long uid, JsonObject data) {
    if (log.isDebugEnabled()) {
      log.debug("socket ping event [{}] [{}]", uid, data);
    }
    sessionManager.getSessionById(uid.toString());
  }

  /**
   * 处理发送消息事件
   *
   * @param uid 用户id
   * @param data 消息
   */
  private void handleForSend(Long uid, JsonObject data) {
    if (log.isDebugEnabled()) {
      log.debug("send msg event [{}] [{}]", uid, data);
    }

    JsonObject headers = data.getJsonObject(SockjsDataKey.HEADERS.value(), new JsonObject());
    //添加时间戳
    headers.put(SockjsDataKey.TIMESTAMP.value(), System.currentTimeMillis());
    data.put(SockjsDataKey.HEADERS.value(), headers);

    if (data.containsKey(SockjsDataKey.BODY.value())) {
      JsonObject body = data.getJsonObject(SockjsDataKey.BODY.value());
      String msgCode = body.getString(SockjsDataKey.MSG_CODE.value());
      if (msgCode != null) {
        BiConsumer<Long, JsonObject> consumer = msgFunctions.getOrDefault(msgCode, EMPTY_FUNCTION);
        consumer.accept(uid, data);
      }
    }

  }

  /**
   * 处理M200消息
   *
   * @param uid 用户id
   * @param data 消息
   */
  private void handleForM200(Long uid, JsonObject data) {
    data.getJsonObject(SockjsDataKey.BODY.value()).put(SockjsDataKey.FROM.value(), uid);
  }

  /**
   * 回复M200消息
   *
   * @param message
   */
  private void replyToM200(Message<JsonObject> message) {
    long uid = message.body().getLong(SockjsDataKey.FROM.value());
    chatLogRepository.queryMineUnread(uid).succeeded(rs -> {
      List<JsonArray> list = rs.getResults();
      if (list != null && !list.isEmpty()) {
        List<MineChat> chats = new ArrayList<>(list.size());
        list.forEach(item -> chats.add(convert(item)));
        message.reply(JsonObject.mapFrom(Response.success(chats)));
      }
    });
  }

  /**
   * 处理M300消息
   *
   * @param uid 用户id
   * @param data 消息
   */
  private void handleForM300(Long uid, JsonObject data) {

    JsonObject body = data.getJsonObject(SockjsDataKey.BODY.value());
    long fromId = Long.parseLong(body.getString(SockjsDataKey.FROM.value()));
    if (uid.longValue() != uid) {
      log.warn("消息被篡改 uid[{}], data[{}]", uid, data);
      return;
    }

    JsonObject headers = data.getJsonObject(SockjsDataKey.HEADERS.value(), new JsonObject());
    //添加mid
    long mid = SnowflakeIdWorker.defaultNextId();
    headers.put(SockjsDataKey.MSG_ID.value(), Long.toString(mid));
    data.put(SockjsDataKey.HEADERS.value(), headers);

    ChatLog chatLog = new ChatLog();
    chatLog.setTimestamp(headers.getLong(SockjsDataKey.TIMESTAMP.value()));
    chatLog.setFromId(fromId);
    chatLog.setToId(Long.parseLong(body.getString(SockjsDataKey.TO.value())));
    chatLog.setContent(body.getString(SockjsDataKey.DATA.value()));
    chatLog.setId(mid);
    chatLog.setType(TYPE.TEXT.value());
    chatLog.setStatus(STATUS.UNREAD.value());

    chatLogRepository.save(chatLog);
  }

  private MineChat convert(JsonArray jsonArray) {
    MineChat chat = new MineChat();
    chat.setId(jsonArray.getLong(0).toString());
    chat.setFromId(jsonArray.getLong(1).toString());
    chat.setToId(jsonArray.getLong(2).toString());
    chat.setType(jsonArray.getInteger(3));
    chat.setContent(jsonArray.getString(4));
    chat.setStatus(jsonArray.getInteger(5));
    chat.setTimestamp(jsonArray.getLong(6));
    return chat;
  }
}
