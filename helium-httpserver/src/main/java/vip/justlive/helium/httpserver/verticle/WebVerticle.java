/*
 *  Copyright (C) 2018 justlive1
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

import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.UserSessionHandler;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.ext.web.handler.sockjs.SockJSHandlerOptions;
import lombok.extern.slf4j.Slf4j;
import vip.justlive.common.base.ioc.BeanStore;
import vip.justlive.common.base.support.ConfigFactory;
import vip.justlive.common.web.vertx.annotation.VertxVerticle;
import vip.justlive.common.web.vertx.auth.TokenAuthHandlerImpl;
import vip.justlive.common.web.vertx.core.BaseWebVerticle;
import vip.justlive.helium.base.config.ServerConf;
import vip.justlive.helium.httpserver.service.SockjsService;
import vip.justlive.helium.httpserver.session.SessionTokenAuthProvider;

/**
 * web单元
 *
 * @author wubo
 */
@Slf4j
@VertxVerticle
public class WebVerticle extends BaseWebVerticle {

  @Override
  public void start() {

    ServerConf conf = ConfigFactory.load(ServerConf.class);
    AuthProvider tokenAuthProvider = BeanStore.getBean(SessionTokenAuthProvider.class);

    Router router = Router.router(vertx);
    baseRoute("(https|http)://.*", router);
    router.route("/").handler(ctx -> ctx.reroute("/index.html"));
    router.route("/static/*").handler(StaticHandler.create().setCachingEnabled(false));
    router.route().handler(UserSessionHandler.create(tokenAuthProvider));
    router.route(conf.getAuthUrlPattern()).handler(new TokenAuthHandlerImpl(tokenAuthProvider));
    serviceRoute(router, "vip.justlive.helium");
    websocketRoute(router);

    vertx.createHttpServer().requestHandler(router::accept).listen(conf.getPort());

  }

  private void websocketRoute(Router router) {
    SockjsService sockjsService = BeanStore.getBean(SockjsService.class);
    ServerConf conf = ConfigFactory.load(ServerConf.class);
    router.route(conf.getAuthUrlPattern()).handler(SockJSHandler.create(vertx,
      new SockJSHandlerOptions().setHeartbeatInterval(conf.getSockjsHeartbeatInterval())).bridge(
      new BridgeOptions().addInboundPermitted(
        new PermittedOptions().setAddressRegex(conf.getSockjsInboundPermittedPattern()))
        .addOutboundPermitted(
          new PermittedOptions().setAddressRegex(conf.getSockjsOutboundPermittedPattern())),
      sockjsService));
  }

}
