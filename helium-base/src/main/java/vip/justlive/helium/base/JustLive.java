package vip.justlive.helium.base;

import io.vertx.core.Launcher;
import io.vertx.core.Vertx;

/**
 * just live
 *
 * @author wubo
 */
public final class JustLive extends Launcher {

  private static Vertx VERTX;

  private JustLive() {
  }

  /**
   * get single vertx
   *
   * @return vertx
   */
  public static Vertx vertx() {
    if(VERTX == null) {
      VERTX = Vertx.vertx();
    }
    return VERTX;
  }

  /**
   * Main entry point.
   *
   * @param args the user command line arguments.
   */
  public static void main(String[] args) {
    new JustLive().dispatch(args);
  }

  @Override
  public void afterStartingVertx(Vertx vertx) {
    JustLive.VERTX = vertx;
  }
}
