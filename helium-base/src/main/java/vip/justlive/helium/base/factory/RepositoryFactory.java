package vip.justlive.helium.base.factory;

import com.google.common.collect.Maps;
import java.util.Map;
import vip.justlive.common.base.exception.Exceptions;
import vip.justlive.helium.base.repository.BaseRepository;

/**
 * Repository工厂类
 *
 * @author wubo
 */
public class RepositoryFactory {

  RepositoryFactory() {
  }

  private static final Map<Class<? extends BaseRepository>, BaseRepository> REPOSITORIES = Maps
    .newConcurrentMap();

  /**
   * 获取Repository
   *
   * @param clazz BaseRepository子类
   * @param <T> 泛型
   * @return Repository
   */
  public static <T extends BaseRepository> T repository(Class<T> clazz) {
    BaseRepository repository = REPOSITORIES.get(clazz);
    if (repository != null) {
      return clazz.cast(repository);
    }
    try {
      T obj = clazz.newInstance();
      REPOSITORIES.putIfAbsent(clazz, obj);
    } catch (InstantiationException | IllegalAccessException e) {
      throw Exceptions.wrap(e);
    }
    return clazz.cast(REPOSITORIES.get(clazz));
  }
}
