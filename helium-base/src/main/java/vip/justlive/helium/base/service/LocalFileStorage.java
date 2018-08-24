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

import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import vip.justlive.common.base.annotation.Singleton;
import vip.justlive.common.base.exception.Exceptions;
import vip.justlive.common.base.support.ConfigFactory;
import vip.justlive.common.base.util.SnowflakeIdWorker;

/**
 * 本地存储实现
 *
 * @author wubo
 */
@Singleton("localFileStorage")
public class LocalFileStorage implements FileStorage {

  private static final String STORE_PATH = "file.storage.local.storePath";
  private static final String ACCESS_URL = "file.storage.local.accessUrl";
  private static final String DEFAULT_STORE_PATH = "/tmp/helium/files";
  private static final String DEFAULT_ACCESS_URL = "/";

  @Override
  public String store(byte[] bytes, String suffix) {
    String path = ConfigFactory.getProperty(STORE_PATH, DEFAULT_STORE_PATH);
    File dir = new File(path);
    if (!dir.exists()) {
      dir.mkdirs();
    }
    String fileName = SnowflakeIdWorker.defaultNextId() + suffix;
    File file = new File(dir, fileName);
    try {
      Files.write(bytes, file);
    } catch (IOException e) {
      throw Exceptions.wrap(e);
    }
    return ConfigFactory.getProperty(ACCESS_URL, DEFAULT_ACCESS_URL) + fileName;
  }
}
