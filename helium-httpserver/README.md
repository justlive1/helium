# helium-httpserver

http服务

### 功能
  - 前置http的sso单点接口
  - 集群im的slb接口
  - 小文件上传、下载接
  - websocket (简易版)
  
### 实现
  用vertx简单实现
  
  
### 开发

idea/eclipse 运行
- 增加Application类型的启动配置
- Main class：vip.justlive.common.web.vertx.JustLive
- Program args: run vip.justlive.common.web.vertx.core.MainVerticle


build
- mac: gradlew build
- win: gradlew.bat build

