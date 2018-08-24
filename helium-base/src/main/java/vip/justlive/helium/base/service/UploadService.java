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

import java.util.Base64;
import vip.justlive.common.base.annotation.Inject;
import vip.justlive.common.base.annotation.Singleton;

/**
 * 上传服务
 *
 * @author wubo
 */
@Singleton
public class UploadService {

  private final FileStorage fileStorage;

  @Inject
  public UploadService(FileStorage fileStorage) {
    this.fileStorage = fileStorage;
  }

  /**
   * 上传base64格式文件
   *
   * @param base64 数据
   * @return 保存地址
   */
  public String uploadBase64Png(String base64) {

    String[] arr = base64.split(",");
    if (arr.length != 2) {
      throw new IllegalArgumentException("base64格式不正确");
    }

    byte[] bytes = Base64.getDecoder().decode(arr[1]);
    String suffix = ".png";
    return fileStorage.store(bytes, suffix);
  }
}
