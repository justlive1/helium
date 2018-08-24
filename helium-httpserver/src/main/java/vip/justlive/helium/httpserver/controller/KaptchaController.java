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
package vip.justlive.helium.httpserver.controller;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.RoutingContext;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import vip.justlive.common.base.annotation.Inject;
import vip.justlive.common.web.vertx.annotation.VertxRoute;
import vip.justlive.common.web.vertx.annotation.VertxRouteMapping;

/**
 * Kaptcha 路由
 *
 * @author wubo
 */
@VertxRoute("/kaptcha")
public class KaptchaController {

  private final DefaultKaptcha kaptcha;

  @Inject
  public KaptchaController(DefaultKaptcha kaptcha) {
    this.kaptcha = kaptcha;
  }

  /**
   * 图片验证码
   *
   * @param context 上下文
   * @throws IOException io异常
   */
  @VertxRouteMapping("/image")
  public void image(RoutingContext context) throws IOException {

    String text = kaptcha.createText();

    context.response().putHeader(HttpHeaderNames.PRAGMA.toString(), "No-cache");
    context.response().putHeader(HttpHeaderNames.CACHE_CONTROL.toString(), "no-cache");
    context.response().putHeader(HttpHeaderNames.EXPIRES.toString(), "0");
    context.response().putHeader(HttpHeaderNames.CONTENT_TYPE.toString(), "image/jpeg");
    context.session().put("kaptcha", text);

    BufferedImage image = kaptcha.createImage(text);
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ImageIO.write(image, "JPEG", bos);

    context.response().end(Buffer.buffer(bos.toByteArray()));

  }

}
