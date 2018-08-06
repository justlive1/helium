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
package vip.justlive.helium.base.tmpl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.TemplateHandler;
import io.vertx.ext.web.templ.TemplateEngine;

/**
 * 模板引擎委托
 *
 * @author wubo
 */
public class DelegateTemplateEngine implements TemplateEngine {

  private final TemplateEngine delegate;
  private final String templateDirectory;

  DelegateTemplateEngine(TemplateEngine engine, String templateDirectory) {
    this.delegate = engine;
    this.templateDirectory = templateDirectory;
  }

  /**
   * 创建委托
   *
   * @param engine 引擎
   * @return 委托
   */
  public static DelegateTemplateEngine create(TemplateEngine engine) {
    return new DelegateTemplateEngine(engine, TemplateHandler.DEFAULT_TEMPLATE_DIRECTORY);
  }

  public static DelegateTemplateEngine create(TemplateEngine engine, String templateDirectory) {
    return new DelegateTemplateEngine(engine, templateDirectory);
  }

  @Override
  public void render(RoutingContext context, String templateDirectory, String templateFileName,
    Handler<AsyncResult<Buffer>> handler) {
    this.delegate.render(context, templateDirectory, templateFileName, handler);
  }

  /**
   * 渲染模板
   *
   * @param context 上下文
   * @param templateFileName 模板名称
   */
  public void render(RoutingContext context, String templateFileName) {
    this.delegate.render(context, templateDirectory, templateFileName, res -> {
      if (res.succeeded()) {
        context.response().putHeader(HttpHeaders.CONTENT_TYPE, TemplateHandler.DEFAULT_CONTENT_TYPE)
          .end(res.result());
      } else {
        context.fail(res.cause());
      }
    });
  }

}
