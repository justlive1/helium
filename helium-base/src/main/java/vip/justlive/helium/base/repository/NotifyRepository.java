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
import vip.justlive.helium.base.entity.Notify;

/**
 * 好友通知Repository
 *
 * @author wubo
 */
@Singleton
public class NotifyRepository extends Repository<Notify> {

  /**
   * 获取未读通知总数
   *
   * @param userId 用户id
   * @return promise
   */
  public JdbcPromise<JsonArray> countUnreadNotifies(Long userId) {
    JdbcPromise<JsonArray> promise = new JdbcPromise<>();
    jdbcClient()
      .querySingleWithParams("select count(*) from notify  where belong_to = ? and unread = 1",
        new JsonArray().add(userId), promise);
    return promise;
  }

  /**
   * 获取未读通知
   *
   * @param userId 用户id
   * @return promise
   */
  public JdbcPromise<ResultSet> findMineNotifies(Long userId) {
    JdbcPromise<ResultSet> promise = new JdbcPromise<>();
    jdbcClient().queryWithParams(
      "select n.id, n.type, n.status, n.remark, n.create_at, n.group_id, u.id as user_id, u.username, u.avatar"
        + " from notify n left join user u on n.from_id = u.id where n.belong_to = ? order by n.create_at desc",
      new JsonArray().add(userId), promise);
    return promise;
  }

  public JdbcPromise<JsonArray> findMineNotifyById(Long id) {
    JdbcPromise<JsonArray> promise = new JdbcPromise<>();
    jdbcClient().querySingleWithParams(
      "select n.id, n.type, n.status, n.remark, n.create_at, n.group_id, u.id as user_id, u.username, u.avatar"
        + " from notify n left join user u on n.from_id = u.id where n.id = ?",
      new JsonArray().add(id), promise);
    return promise;
  }

  /**
   * 修改未读通知为已读
   *
   * @param userId 用户id
   * @return promise
   */
  public JdbcPromise<UpdateResult> updateReadNotifies(Long userId) {
    JdbcPromise<UpdateResult> promise = new JdbcPromise<>();
    jdbcClient().updateWithParams("update notify set unread = 0 where belong_to = ? and unread = 1",
      new JsonArray().add(userId), promise);
    return promise;
  }

  /**
   * 修改通知状态
   *
   * @param id 用户id
   * @param status 状态
   * @return promise
   */
  public JdbcPromise<UpdateResult> updateNotifyStatus(Long id, Integer status) {
    JdbcPromise<UpdateResult> promise = new JdbcPromise<>();
    jdbcClient().updateWithParams("update notify set status = ? where id = ?",
      new JsonArray().add(status).add(id), promise);
    return promise;
  }

}
