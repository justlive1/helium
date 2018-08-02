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
package vip.justlive.helium.httpserver.verticle;

import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.bridge.BridgeEventType;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.TemplateHandler;
import io.vertx.ext.web.handler.UserSessionHandler;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.ext.web.handler.sockjs.SockJSHandlerOptions;
import io.vertx.ext.web.templ.TemplateEngine;
import io.vertx.ext.web.templ.ThymeleafTemplateEngine;
import io.vertx.ext.web.templ.impl.CachingTemplateEngine;
import lombok.extern.slf4j.Slf4j;
import org.thymeleaf.templatemode.TemplateMode;
import vip.justlive.common.base.support.ConfigFactory;
import vip.justlive.common.web.vertx.annotation.VertxVerticle;
import vip.justlive.common.web.vertx.auth.TokenJWTAuthHandlerImpl;
import vip.justlive.common.web.vertx.core.BaseWebVerticle;
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
    JWTAuth jwtAuth = AuthFactory.jwtAuth();
    router.route().handler(UserSessionHandler.create(jwtAuth));

    router.route(authConf.getAuthUrlPattern()).handler(new TokenJWTAuthHandlerImpl(jwtAuth, null));

    serviceRoute(router, "vip.justlive.helium");
    websocketRoute(router);
    staticResourceRoute(router);

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
      be.complete(true);
    });

    AuthConf authConf = ConfigFactory.load(AuthConf.class);
    router.route(authConf.getAuthUrlPattern()).handler(ctx -> {
      if (log.isDebugEnabled()) {
        log.debug("sockjs user -> {}", ctx.user());
      }
      ctx.next();
    });
    router.route(authConf.getAuthUrlPattern()).handler(sockJSHandler);
  }

  private void staticResourceRoute(Router router) {
    System
      .setProperty(CachingTemplateEngine.DISABLE_TEMPL_CACHING_PROP_NAME, Boolean.TRUE.toString());

    router.route("/static/*").handler(StaticHandler.create());

    TemplateEngine engine = ThymeleafTemplateEngine.create().setMode(TemplateMode.HTML);
    TemplateHandler handler = TemplateHandler.create(engine);
    router.route().handler(handler);

  }
}
