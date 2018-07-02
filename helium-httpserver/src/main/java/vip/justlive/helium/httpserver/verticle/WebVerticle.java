package vip.justlive.helium.httpserver.verticle;

import com.google.common.collect.ImmutableSet;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CookieHandler;
import io.vertx.ext.web.handler.CorsHandler;
import java.util.Set;
import vip.justlive.common.base.constant.BaseConstants;
import vip.justlive.common.base.support.ConfigFactory;
import vip.justlive.common.web.vertx.support.RouteRegisterFactory;
import vip.justlive.helium.base.config.ServerConf;

/**
 * web单元
 *
 * @author wubo
 */
public class WebVerticle extends AbstractVerticle {

  static {
    ConfigFactory.loadProperties("conf.properties");
  }

  @Override
  public void start() {

    Router router = Router.router(vertx);

    Set<HttpMethod> methods = ImmutableSet.<HttpMethod>builder().add(HttpMethod.GET)
        .add(HttpMethod.POST).add(HttpMethod.OPTIONS).add(HttpMethod.PUT).add(HttpMethod.DELETE)
        .add(HttpMethod.HEAD).build();

    router.route().handler(CorsHandler.create(BaseConstants.ANY).allowedMethods(methods));
    router.route().handler(CookieHandler.create());
    router.route().handler(BodyHandler.create());

    RouteRegisterFactory routeRegisterFactory = new RouteRegisterFactory(router);
    routeRegisterFactory.execute("vip.justlive.helium");

    ServerConf conf = ConfigFactory.load(ServerConf.class);

    vertx.createHttpServer().requestHandler(router::accept).listen(conf.getPort());

  }

}