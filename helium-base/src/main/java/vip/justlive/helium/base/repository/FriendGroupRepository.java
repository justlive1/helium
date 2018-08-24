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
import io.vertx.ext.sql.UpdateResult;
import vip.justlive.common.base.annotation.Singleton;
import vip.justlive.common.web.vertx.datasource.JdbcPromise;
import vip.justlive.common.web.vertx.datasource.Repository;
import vip.justlive.helium.base.entity.FriendGroup;

/**
 * 好友分组Repository
 *
 * @author wubo
 */
@Singleton
public class FriendGroupRepository extends Repository<FriendGroup> {

  public JdbcPromise<UpdateResult> updateGroupName(String name, Long id) {
    JdbcPromise<UpdateResult> promise = new JdbcPromise<>();
    jdbcClient().updateWithParams("update friend_group set name = ? where id = ? ",
      new JsonArray().add(name).add(id), promise);
    return promise;
  }

  public JdbcPromise<UpdateResult> deleteGroup(Long id) {
    JdbcPromise<UpdateResult> promise = new JdbcPromise<>();
    jdbcClient()
      .updateWithParams("delete from friend_group where id = ? ", new JsonArray().add(id), promise);
    return promise;
  }
}
