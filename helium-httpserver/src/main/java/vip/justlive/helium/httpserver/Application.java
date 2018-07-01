package vip.justlive.helium.httpserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 程序入口
 *
 * @author wubo
 */
@EnableAutoConfiguration
@SpringBootApplication(scanBasePackages = "vip.justlive")
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
