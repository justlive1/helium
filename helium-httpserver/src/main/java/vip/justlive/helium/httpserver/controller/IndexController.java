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
package vip.justlive.helium.httpserver.controller;

import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.templ.ThymeleafTemplateEngine;
import io.vertx.ext.web.templ.impl.CachingTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import vip.justlive.common.base.support.ConfigFactory;
import vip.justlive.common.web.vertx.annotation.VertxRoute;
import vip.justlive.common.web.vertx.annotation.VertxRouteMapping;
import vip.justlive.helium.base.config.WebImConf;
import vip.justlive.helium.base.tmpl.DelegateTemplateEngine;

/**
 * 页面路由
 *
 * @author wubo
 */
@VertxRoute
public class IndexController {

  private final DelegateTemplateEngine htmlEngine;
  private final DelegateTemplateEngine jsEngine;

  public IndexController() {
    System
      .setProperty(CachingTemplateEngine.DISABLE_TEMPL_CACHING_PROP_NAME, Boolean.TRUE.toString());
    this.htmlEngine = DelegateTemplateEngine.create(ThymeleafTemplateEngine.create());
    this.jsEngine = DelegateTemplateEngine.create(ThymeleafTemplateEngine.create().setMode(
      TemplateMode.JAVASCRIPT));
  }

  /**
   * 登录页
   *
   * @param ctx 上下文
   */
  @VertxRouteMapping("/login.html")
  public void login(RoutingContext ctx) {
    htmlEngine.render(ctx, "/login.html");
  }

  /**
   * 注册页
   *
   * @param ctx 上下文
   */
  @VertxRouteMapping("/register.html")
  public void register(RoutingContext ctx) {
    htmlEngine.render(ctx, "/register.html");
  }

  /**
   * web IM 首页
   *
   * @param ctx 上下文
   */
  @VertxRouteMapping("/index.html")
  public void index(RoutingContext ctx) {
    WebImConf conf = ConfigFactory.load(WebImConf.class);
    ctx.put("webImConf", conf);
    htmlEngine.render(ctx, "/index.html");
  }

  /**
   * him.js
   *
   * @param ctx 上下文
   */
  @VertxRouteMapping("/interface/him.js")
  public void himJs(RoutingContext ctx) {
    WebImConf conf = ConfigFactory.load(WebImConf.class);
    ctx.put("webImConf", conf);
    jsEngine.render(ctx, "/him.js");
  }

}
