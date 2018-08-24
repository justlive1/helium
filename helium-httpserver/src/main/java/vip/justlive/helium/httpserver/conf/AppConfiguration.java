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
package vip.justlive.helium.httpserver.conf;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import io.vertx.ext.web.templ.ThymeleafTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import vip.justlive.common.base.annotation.Configuration;
import vip.justlive.common.base.annotation.Singleton;
import vip.justlive.common.base.crypto.Encoder;
import vip.justlive.common.base.crypto.Md5Encoder;
import vip.justlive.common.web.vertx.tmpl.DelegateTemplateEngine;
import vip.justlive.helium.base.util.KaptchaUtils;

/**
 * app配置
 *
 * @author wubo
 */
@Configuration
public class AppConfiguration {

  @Singleton
  DefaultKaptcha kaptcha() {
    return KaptchaUtils.googleKaptcha();
  }

  @Singleton("htmlEngine")
  DelegateTemplateEngine htmlEngine() {
    return DelegateTemplateEngine.create(ThymeleafTemplateEngine.create());
  }

  @Singleton("jsEngine")
  DelegateTemplateEngine jsEngine() {
    return DelegateTemplateEngine
      .create(ThymeleafTemplateEngine.create().setMode(TemplateMode.JAVASCRIPT));
  }

  @Singleton
  Encoder encoder() {
    return new Md5Encoder();
  }
}
