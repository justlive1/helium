package vip.justlive.helium.httpserver.verticle;

import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.AuthHandler;
import io.vertx.ext.web.handler.UserSessionHandler;
import lombok.extern.slf4j.Slf4j;
import vip.justlive.common.base.support.ConfigFactory;
import vip.justlive.common.web.vertx.BaseWebVerticle;
import vip.justlive.common.web.vertx.annotation.VertxVerticle;
import vip.justlive.common.web.vertx.support.RouteRegisterFactory;
import vip.justlive.helium.base.config.AuthConf;
import vip.justlive.helium.base.config.ServerConf;
import vip.justlive.helium.base.factory.AuthFactory;

/**
 * web单元
 *
 * @author wubo
 */
@VertxVerticle
@Slf4j
public class WebVerticle extends BaseWebVerticle {

  @Override
  public void start() {

    Router router = Router.router(vertx);

    baseRoute(router);
    authRoute(router);
    serviceRoute(router);

    ServerConf conf = ConfigFactory.load(ServerConf.class);
    vertx.createHttpServer().requestHandler(router::accept).listen(conf.getPort());

  }


  /**
   * 安全认证路由
   *
   * @param router router
   */
  private void authRoute(Router router) {

    AuthProvider authProvider = AuthFactory.authProvider();

    if (authProvider != null) {
      router.route().handler(UserSessionHandler.create(authProvider));
      AuthHandler authHandler = AuthFactory.authHandler(authProvider);
      AuthConf authConf = ConfigFactory.load(AuthConf.class);
      router.route(authConf.getAuthUrlPattern()).handler(authHandler);
    }
  }

  /**
   * 业务路由
   *
   * @param router route
   */
  private void serviceRoute(Router router) {

    RouteRegisterFactory routeRegisterFactory = new RouteRegisterFactory(router);
    routeRegisterFactory.execute("vip.justlive.helium");
  }

}
