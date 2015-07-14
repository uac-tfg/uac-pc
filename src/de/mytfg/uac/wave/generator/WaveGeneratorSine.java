package de.mytfg.uac.wave.generator;

import de.mytfg.uac.wave.Wave;

public class WaveGeneratorSine extends WaveGenerator {

  private final int frequency;
  
  public WaveGeneratorSine(int frequency) {
    this.frequency = frequency;
  }
  
  @Override
  protected double generateSample(Wave wave, long abs, long rel, long left) {
    return Math.sin(frequency * 2 * Math.PI * abs * (1d / wave.getSampleRate()));
  }

}
