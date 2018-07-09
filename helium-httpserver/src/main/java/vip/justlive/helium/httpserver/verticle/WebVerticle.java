package vip.justlive.helium.httpserver.verticle;

import com.google.common.collect.ImmutableSet;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.AuthHandler;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CookieHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.handler.TimeoutHandler;
import io.vertx.ext.web.handler.UserSessionHandler;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.ext.web.handler.sockjs.SockJSHandlerOptions;
import io.vertx.ext.web.sstore.LocalSessionStore;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import vip.justlive.common.base.constant.BaseConstants;
import vip.justlive.common.base.support.ConfigFactory;
import vip.justlive.common.web.vertx.support.RouteRegisterFactory;
import vip.justlive.helium.base.config.AuthConf;
import vip.justlive.helium.base.config.ServerConf;
import vip.justlive.helium.base.factory.AuthFactory;

/**
 * web单元
 *
 * @author wubo
 */
@Slf4j
public class WebVerticle extends AbstractVerticle {

  static {
    ConfigFactory.loadProperties("conf.properties");
  }

  @Override
  public void start() {

    Router router = Router.router(vertx);

    baseRoute(router);
    authRoute(router);
    serviceRoute(router);
    websocketRoute(router);

    ServerConf conf = ConfigFactory.load(ServerConf.class);
    vertx.createHttpServer().requestHandler(router::accept).listen(conf.getPort());

  }

  /**
   * 基础路由
   *
   * @param router router
   */
  private void baseRoute(Router router) {

    Set<HttpMethod> methods = ImmutableSet.<HttpMethod>builder().add(HttpMethod.GET)
      .add(HttpMethod.POST).add(HttpMethod.OPTIONS).add(HttpMethod.PUT).add(HttpMethod.DELETE)
      .add(HttpMethod.HEAD).build();

    router.route().handler(CorsHandler.create(BaseConstants.ANY).allowedMethods(methods));
    router.route().handler(CookieHandler.create());
    router.route().handler(BodyHandler.create());
    router.route().handler(TimeoutHandler.create());
    router.route().handler(SessionHandler.create(LocalSessionStore.create(vertx)));
    router.exceptionHandler(r -> log.error("something is wrong:", r));
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

  private void websocketRoute(Router router) {
    ServerConf conf = ConfigFactory.load(ServerConf.class);
    SockJSHandlerOptions sockjsopt = new SockJSHandlerOptions()
      .setHeartbeatInterval(conf.getSockjsHeartbeatInterval());

    SockJSHandler sockJSHandler = SockJSHandler.create(vertx, sockjsopt);
    BridgeOptions opt = new BridgeOptions();
    opt.setPingTimeout(conf.getSockjsPingTimeout());
    opt.addInboundPermitted(
      new PermittedOptions().setRequiredAuthority(conf.getSockjsRequiredAuthority()));

    sockJSHandler.bridge(opt, be -> {
      System.out.println(be);
      be.complete();
    });

    AuthConf authConf = ConfigFactory.load(AuthConf.class);
    router.route(authConf.getAuthUrlPattern()).handler(sockJSHandler);
  }

}
