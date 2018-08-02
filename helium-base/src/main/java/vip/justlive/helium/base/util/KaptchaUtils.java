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
package vip.justlive.helium.base.util;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import java.util.Properties;

/**
 * Kaptcha工具类
 *
 * @author wubo
 */
public class KaptchaUtils {

  KaptchaUtils() {
  }

  /**
   * 谷歌验证码
   *
   * @return 谷歌验证码
   */
  public static DefaultKaptcha googleKaptcha() {

    DefaultKaptcha kaptcha = new DefaultKaptcha();
    Config config = new Config(new Properties());
    kaptcha.setConfig(config);
    return kaptcha;
  }

}
