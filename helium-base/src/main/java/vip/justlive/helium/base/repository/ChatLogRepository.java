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
package vip.justlive.helium.base.repository;

import io.vertx.core.json.JsonArray;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.UpdateResult;
import vip.justlive.common.base.annotation.Singleton;
import vip.justlive.common.web.vertx.datasource.JdbcPromise;
import vip.justlive.common.web.vertx.datasource.Repository;
import vip.justlive.helium.base.entity.ChatLog;

/**
 * 聊天记录Repository
 *
 * @author wubo
 */
@Singleton
public class ChatLogRepository extends Repository<ChatLog> {

  /**
   * 获取我的未读聊天
   *
   * @param userId 用户id
   * @return promise
   */
  public JdbcPromise<ResultSet> queryMineUnread(long userId) {
    JdbcPromise<ResultSet> promise = new JdbcPromise<>();
    jdbcClient().queryWithParams(
      "select id,from_id,to_id,type,content,status,timestamp from chat_log where to_id = ? and status = 0 order by timestamp",
      new JsonArray().add(userId), promise);
    return promise;
  }

  /**
   * 修改消息已读
   *
   * @param userId 用户id
   * @param friendId 好友id
   * @return promise
   */
  public JdbcPromise<UpdateResult> updateReadByFriendId(long userId, long friendId) {
    JdbcPromise<UpdateResult> promise = new JdbcPromise<>();
    jdbcClient().updateWithParams(
      "update chat_log set status = 1 where to_id = ? and from_id = ? and status = 0",
      new JsonArray().add(userId).add(friendId), promise);
    return promise;
  }

}
