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
package vip.justlive.helium.base.repository;

import io.vertx.core.json.JsonArray;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.UpdateResult;
import vip.justlive.common.base.annotation.Singleton;
import vip.justlive.common.web.vertx.datasource.JdbcPromise;
import vip.justlive.common.web.vertx.datasource.Repository;
import vip.justlive.helium.base.entity.Friend;

/**
 * 好友 Repository
 *
 * @author wubo
 */
@Singleton
public class FriendRepository extends Repository<Friend> {

  /**
   * 根据用户id获取我的*
   *
   * @param userId 用户id
   * @return promise
   */
  public JdbcPromise<ResultSet> findMineFriend(long userId) {
    JdbcPromise<ResultSet> promise = new JdbcPromise<>();
    jdbcClient().queryWithParams(
      "select g.id, g.name as groupname,g.order_index,u.id, u.username, f.memo_name, u.signature, u.avatar, f.remark"
        + " from friend_group g left join friend f on f.friend_group_id = g.id"
        + " left join user u on f.friend_user_id = u.id where g.user_id = ? order by g.order_index",
      new JsonArray().add(userId), promise);
    return promise;
  }

  /**
   * 统计查找好友总数
   *
   * @param keyword 关键字
   * @param userId 用户id
   * @return promise
   */
  public JdbcPromise<JsonArray> countFindFriend(String keyword, long userId) {
    JdbcPromise<JsonArray> promise = new JdbcPromise<>();
    jdbcClient().querySingleWithParams(
      "select count(*) from user u where username like concat(?, '%')"
        + " and id != ? and id not in (select f.friend_user_id from friend f where f.user_id = ?) ",
      new JsonArray().add(keyword).add(userId).add(userId), promise);
    return promise;
  }

  /**
   * 查找好友
   *
   * @param keyword 关键字
   * @param userId 用户id
   * @param offset 偏移量
   * @param limit 分页限制
   * @return promise
   */
  public JdbcPromise<ResultSet> findFriend(String keyword, long userId, int offset, int limit) {
    JdbcPromise<ResultSet> promise = new JdbcPromise<>();
    jdbcClient().queryWithParams("select id, username, signature, avatar from "
        + " (select u.* from user u where username like concat(?, '%')"
        + " and id != ? and id not in (select f.friend_user_id from friend f where f.user_id = ?) "
        + " order by create_at desc) tm limit ?, ?",
      new JsonArray().add(keyword).add(userId).add(userId).add(offset).add(limit), promise);
    return promise;
  }

  public JdbcPromise<JsonArray> countGroupFriend(long groupId) {
    JdbcPromise<JsonArray> promise = new JdbcPromise<>();
    jdbcClient().querySingleWithParams("select count(*) from friend where friend_group_id = ?",
      new JsonArray().add(groupId), promise);
    return promise;
  }

  public JdbcPromise<UpdateResult> deleteFriend(long userId, long friendId) {
    JdbcPromise<UpdateResult> promise = new JdbcPromise<>();
    jdbcClient().updateWithParams(
      "delete from friend where (user_id = ? and friend_user_id = ?) or (user_id = ? and friend_user_id = ?)",
      new JsonArray().add(userId).add(friendId).add(friendId).add(userId), promise);
    return promise;
  }

  public JdbcPromise<UpdateResult> updateFriendMemo(long userId, long friendId, String memo) {
    JdbcPromise<UpdateResult> promise = new JdbcPromise<>();
    jdbcClient()
      .updateWithParams("update friend set memo_name = ? where user_id = ? and friend_user_id = ?",
        new JsonArray().add(memo).add(userId).add(friendId), promise);
    return promise;
  }

}
