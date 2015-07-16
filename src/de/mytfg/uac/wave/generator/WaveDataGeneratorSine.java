package de.mytfg.uac.wave.generator;

import de.mytfg.uac.wave.Wave;

public class WaveDataGeneratorSine extends WaveDataGenerator {

  private WaveGeneratorSine simple;
  private int bitFrequency;
  
  public WaveDataGeneratorSine(int frequency) {
    simple = new WaveGeneratorSine(frequency);
    bitFrequency = frequency / 2;
  }
  
  @Override
  protected double generateSample(Wave wave, byte bit, long abs, long rel, long left) {
    return simple.generateSample(wave, abs, rel, left) * bit;
  }

  @Override
  public int getBitFrequency() {
    return bitFrequency;
  }

}
