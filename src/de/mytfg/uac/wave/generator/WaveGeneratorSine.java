package de.mytfg.uac.wave.generator;

import de.mytfg.uac.wave.Wave;

public class WaveGeneratorSine extends WaveGenerator {

  private final int frequency;
  private final double phase;

  /**
   * Constructs a new WaveGenerator for sines. The phase is defaulted to 0 (begin of a period).
   * 
   * @param frequency the frequency of the wanted sin
   */
  public WaveGeneratorSine(int frequency) {
    this(frequency, 0);
  }

  /**
   * Constructs a new WaveGenerator for sines.
   * 
   * @param frequency the frequency of the wanted sine
   * @param phase the phase decoded in a value from 0 to 1 where 0 is the begin of a period and 1 is
   *        the end of a period
   */
  public WaveGeneratorSine(int frequency, double phase) {
    this.frequency = frequency;
    this.phase = phase;
  }

  @Override
  protected double generateSample(Wave wave, long abs, long rel, long left) {
    double period = frequency * 2 * Math.PI;
    double time = rel * (1d / wave.getSampleRate());
    double shift = 2 * Math.PI * phase;
    double val = Math.sin(period * time + shift);
    return val;
  }

}
