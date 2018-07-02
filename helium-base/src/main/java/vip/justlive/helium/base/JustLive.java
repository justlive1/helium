package vip.justlive.helium.base;

import io.vertx.core.Launcher;
import io.vertx.core.Vertx;

/**
 * @author wubo
 */
public final class JustLive extends Launcher {

  private static Vertx vertx;

  private JustLive() {
  }

  /**
   * @return vertx
   */
  public static Vertx vertx() {
    return vertx;
  }

  /**
   * Main entry point.
   *
   * @param args the user command line arguments.
   */
  public static void main(String[] args) {
    new Launcher().dispatch(args);
  }

  @Override
  public void afterStartingVertx(Vertx vertx) {
    JustLive.vertx = vertx;
  }
}
