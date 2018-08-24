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
package vip.justlive.helium.base.service;

/**
 * 文件保存接口
 *
 * @author wubo
 */
public interface FileStorage {

  /**
   * 保存
   *
   * @param bytes 数据
   * @param suffix 后缀名
   * @return 保存地址
   */
  String store(byte[] bytes, String suffix);

}
