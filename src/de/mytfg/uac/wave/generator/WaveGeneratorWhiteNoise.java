package de.mytfg.uac.wave.generator;

import java.util.Random;

import de.mytfg.uac.wave.Wave;

public class WaveGeneratorWhiteNoise extends WaveGenerator {

  private Random random;
  
  public WaveGeneratorWhiteNoise(long seed) {
    random = new Random(seed);
  }
  
  public WaveGeneratorWhiteNoise() {
    random = new Random();
  }
  
  @Override
  protected double generateSample(Wave wave, long abs, long rel, long left) {
    return random.nextDouble() * 2 - 1;
  }

}
