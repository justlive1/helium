package vip.justlive.helium.base.repository;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.lang.reflect.Method;

/**
 * 用户Repository
 *
 * @author wubo
 */
public class UserRepository extends BaseRepository {

  /**
   * 根据用户名和密码获取用户
   *
   * @param username 用户名
   * @param password 密码
   * @return promise
   */
  public Promise findUserByUsernameAndPassword(String username, String password) {
    Promise promise = promise();
    jdbcClient().querySingleWithParams("select * from t_user where username = ? and password = ?",
      new JsonArray().add(username).add(password), promise);
    return promise;
  }

}
