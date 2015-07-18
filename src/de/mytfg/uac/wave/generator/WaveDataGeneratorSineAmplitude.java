package de.mytfg.uac.wave.generator;

import de.mytfg.uac.wave.Wave;

public class WaveDataGeneratorSineAmplitude extends WaveDataGenerator {

  private WaveGeneratorSine simple;
  private int bitFrequency;
  
  public WaveDataGeneratorSineAmplitude(int frequency, int bitFrequency) {
    simple = new WaveGeneratorSine(frequency);
    this.bitFrequency = bitFrequency;
  }
  
  public WaveDataGeneratorSineAmplitude(int frequency) {
    this(frequency, frequency / 2);
  }
  
  @Override
  public double generateSample(Wave wave, byte bit, long abs, long rel, long left) {
    return simple.generateSample(wave, abs, rel, left) * bit;
  }

  @Override
  public int getBitFrequency() {
    return bitFrequency;
  }
  
  public int getFrequency() {
    return simple.getFrequency();
  }

}
