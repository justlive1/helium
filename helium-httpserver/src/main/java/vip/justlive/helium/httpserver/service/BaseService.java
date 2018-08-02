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
package vip.justlive.helium.httpserver.service;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import vip.justlive.common.base.domain.Response;

/**
 * 基础服务
 *
 * @author wubo
 */
public abstract class BaseService {

  public void success(RoutingContext ctx) {
    ctx.response().putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON.toString())
      .end(JsonObject.mapFrom(Response.success()).toBuffer());
  }

  public void success(Object data, RoutingContext ctx) {
    ctx.response().putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON.toString())
      .end(JsonObject.mapFrom(Response.success(data)).toBuffer());
  }

  public void error(String msg, RoutingContext ctx) {
    ctx.response().putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON.toString())
      .end(JsonObject.mapFrom(Response.error(msg)).toBuffer());
  }

  public void error(String code, String msg, RoutingContext ctx) {
    ctx.response().putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON.toString())
      .end(JsonObject.mapFrom(Response.error(code, msg)).toBuffer());
  }

}
