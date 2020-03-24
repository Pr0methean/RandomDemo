package io.github.pr0methean.medium;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import javax.imageio.ImageIO;

public class RandomDemo {

  private static final int WIDTH = 512;
  private static final int HEIGHT = 512;
  private static final int BLACK = 0;
  private static final int WHITE = 0xFFFFFF;

  public static void main(String[] args) throws IOException, InterruptedException {
    final BufferedImage[] out = new BufferedImage[Integer.SIZE];
    for (int i = 0; i < Integer.SIZE; i++) {
      out[i] = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_BYTE_BINARY);
    }
    Random prng = ThreadLocalRandom.current();
    Thread[] workers = new Thread[WIDTH];
    for (int y = 0; y < HEIGHT; y++) {
      final int threadY = y;
      workers[y] = new Thread() {
        @Override public void run() {
          for (int x = 0; x < WIDTH; x++) {
            int random = ThreadLocalRandom.current().nextInt();
            for (int bit = 0; bit < Integer.SIZE; bit++) {
              boolean isSet = ((random >>> bit) & 1) == 1;
              out[bit].setRGB(x, threadY, isSet ? BLACK : WHITE);
            }
          }
        }
      };
      workers[y].start();
    }
    for (Thread worker : workers) {
      worker.join();
    }
    for (int i = 0; i < Integer.SIZE; i++) {
      ImageIO.write(out[i], "PNG", new File(String.format("bit%02d.png", i)));
    }
  }
}
