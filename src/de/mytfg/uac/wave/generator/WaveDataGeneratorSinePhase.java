package de.mytfg.uac.wave.generator;

import de.mytfg.uac.wave.Wave;

public class WaveDataGeneratorSinePhase extends WaveDataGenerator {

  private WaveGeneratorSine sineA;
  private WaveGeneratorSine sineB;
  private int bitFrequency;
  
  public WaveDataGeneratorSinePhase(int frequency) {
    this.sineA = new WaveGeneratorSine(frequency, 0);
    this.sineB = new WaveGeneratorSine(frequency, 0.5d);
    this.bitFrequency = frequency / 2;
  }

  @Override
  protected double generateSample(Wave wave, byte b, long abs, long rel, long left) {
    double val;
    if(b == 0) {
      val = sineA.generateSample(wave, abs, rel, left);
    } else {
      val = sineB.generateSample(wave, abs, rel, left);
    }
    return val;
  }

  @Override
  public int getBitFrequency() {
    return bitFrequency;
  }

}
