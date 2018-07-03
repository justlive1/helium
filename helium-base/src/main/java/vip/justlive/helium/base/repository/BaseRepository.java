package vip.justlive.helium.base.repository;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.jdbc.JDBCClient;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import vip.justlive.common.base.exception.Exceptions;
import vip.justlive.helium.base.factory.DataSourceFactory;

/**
 * 抽象Repository
 *
 * @author wubo
 */
@Slf4j
public abstract class BaseRepository {

  /**
   * 获取jdbcClient
   *
   * @return jdbcClient
   */
  public JDBCClient jdbcClient() {
    return DataSourceFactory.sharedSingleJdbcClient(getClass());
  }

  protected Promise promise() {
    return new Promise();
  }

  @FunctionalInterface
  public interface Then {

    /**
     * 处理
     *
     * @param result 结果
     */
    void then(JsonArray result);
  }

  public static class Promise implements Handler<AsyncResult<JsonArray>> {

    private List<Then> thens = new ArrayList<>();

    @Override
    public void handle(AsyncResult<JsonArray> event) {
      if (event.succeeded()) {
        JsonArray result = event.result();
        for (Then then : thens) {
          then.then(result);
        }
      } else {
        log.error("result error:", event.cause());
        throw Exceptions.wrap(event.cause());
      }
    }

    /**
     * 后续处理
     *
     * @param then 处理逻辑
     * @return promise
     */
    public Promise then(Then then) {
      thens.add(then);
      return this;
    }
  }

}
