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
package vip.justlive.helium.base.config;

import lombok.Data;
import vip.justlive.common.base.annotation.Value;

/**
 * web版IM配置
 *
 * @author wubo
 */
@Data
public class WebImConf {

  /**
   * web版im地址
   */
  @Value("${web.im.url}")
  private String webImUrl;

  /**
   * 备案号
   */
  @Value("${web.icp}")
  private String icp;

  @Value("${web.beianInfo}")
  private String beianInfo;

  @Value("${web.beianUrl}")
  private String beianUrl;

}
