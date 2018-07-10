package vip.justlive.helium.httpserver.verticle;

import io.vertx.core.eventbus.EventBus;
import io.vertx.ext.bridge.BridgeEventType;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.ext.web.handler.sockjs.SockJSHandlerOptions;
import io.vertx.ext.web.handler.sockjs.SockJSSocket;
import lombok.extern.slf4j.Slf4j;
import vip.justlive.common.base.support.ConfigFactory;
import vip.justlive.common.web.vertx.BaseWebVerticle;
import vip.justlive.common.web.vertx.annotation.VertxVerticle;
import vip.justlive.helium.base.config.AuthConf;
import vip.justlive.helium.base.config.ServerConf;

/**
 * websocket 单元
 *
 * @author wubo
 */
@VertxVerticle
@Slf4j
public class WebSocketVerticle extends BaseWebVerticle {

  @Override
  public void start() {

    Router router = Router.router(vertx);

    baseRoute(router);
    websocketRoute(router);

    ServerConf conf = ConfigFactory.load(ServerConf.class);
    vertx.createHttpServer().requestHandler(router::accept).listen(conf.getPort() + 10);

  }

  private void websocketRoute(Router router) {
    ServerConf conf = ConfigFactory.load(ServerConf.class);
    SockJSHandlerOptions sockjsopt = new SockJSHandlerOptions()
      .setHeartbeatInterval(conf.getSockjsHeartbeatInterval());

    SockJSHandler sockJSHandler = SockJSHandler.create(vertx, sockjsopt);
    BridgeOptions opt = new BridgeOptions();
    opt.setPingTimeout(conf.getSockjsPingTimeout());
    opt.addInboundPermitted(
      new PermittedOptions().setAddressRegex(conf.getSockjsInboundPermittedPattern()));
    opt.addOutboundPermitted(
      new PermittedOptions().setAddressRegex(conf.getSockjsOutboundPermittedPattern()));

    sockJSHandler.bridge(opt, be -> {
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
    router.route(authConf.getAuthUrlPattern()).handler(sockJSHandler);
  }

}
