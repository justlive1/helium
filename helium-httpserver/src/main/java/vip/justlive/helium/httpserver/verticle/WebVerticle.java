package vip.justlive.helium.httpserver.verticle;

import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.bridge.BridgeEventType;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.UserSessionHandler;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.ext.web.handler.sockjs.SockJSHandlerOptions;
import io.vertx.ext.web.handler.sockjs.SockJSSocket;
import lombok.extern.slf4j.Slf4j;
import vip.justlive.common.base.support.ConfigFactory;
import vip.justlive.common.web.vertx.annotation.VertxVerticle;
import vip.justlive.common.web.vertx.core.BaseWebVerticle;
import vip.justlive.common.web.vertx.core.JWTLoginHandlerImpl;
import vip.justlive.common.web.vertx.core.TokenJWTAuthHandlerImpl;
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

    baseRoute("(https|http)://.*", router);

    AuthConf authConf = ConfigFactory.load(AuthConf.class);
    AuthProvider jdbcAuth = AuthFactory.jdbcAuth();
    JWTAuth jwtAuth = AuthFactory.jwtAuth();
    router.route().handler(UserSessionHandler.create(AuthFactory.jwtAuth()));
    router.route("/login").handler(new JWTLoginHandlerImpl(jwtAuth, jdbcAuth,
      JWTLoginHandlerImpl.DEFAULT_U_PARAM, JWTLoginHandlerImpl.DEFAULT_P_PARAM, true)
      .setAlgorithm(authConf.getJwtKeystoreAlgorithm()));

    router.route(authConf.getAuthUrlPattern()).handler(new TokenJWTAuthHandlerImpl(jwtAuth, null));

    serviceRoute(router, "vip.justlive.helium");
    websocketRoute(router);

    ServerConf conf = ConfigFactory.load(ServerConf.class);
    vertx.createHttpServer().requestHandler(router::accept).listen(conf.getPort());

  }

  private void websocketRoute(Router router) {
    ServerConf conf = ConfigFactory.load(ServerConf.class);
    SockJSHandlerOptions sockjsopt = new SockJSHandlerOptions()
      .setHeartbeatInterval(conf.getSockjsHeartbeatInterval());

    SockJSHandler sockJSHandler = SockJSHandler.create(vertx, sockjsopt);

    BridgeOptions options = new BridgeOptions()
      .addInboundPermitted(new PermittedOptions()
        .setAddressRegex(conf.getSockjsInboundPermittedPattern()))
      .addOutboundPermitted(new PermittedOptions()
        .setAddressRegex(conf.getSockjsOutboundPermittedPattern()));

    sockJSHandler.bridge(options, be -> {
      if (be.type() != BridgeEventType.SOCKET_PING && log.isDebugEnabled()) {
        log.debug("receive msg: [{}]", be.getRawMessage());
      }

      if (be.type() == BridgeEventType.SOCKET_CREATED) {
        SockJSSocket socket = be.socket();
//        vertx.eventBus().consumer(socket.writeHandlerID());
      }

      be.complete(true);
    });

    AuthConf authConf = ConfigFactory.load(AuthConf.class);
    router.route(authConf.getAuthUrlPattern()).handler(ctx -> {
      log.info("sockjs user -> {}", ctx.user());
      ctx.next();
    });
    router.route(authConf.getAuthUrlPattern()).handler(sockJSHandler);

    router.route().handler(StaticHandler.create());
  }
}
