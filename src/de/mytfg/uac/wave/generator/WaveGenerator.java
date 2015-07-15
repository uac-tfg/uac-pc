package de.mytfg.uac.wave.generator;

import de.mytfg.uac.wave.Wave;

public abstract class WaveGenerator {

  /**
   * Generates a wave. Overwrites the wave in the specified part.
   * The scale factor is defaulted to 1.
   * 
   * @param wave the wave to write to
   * @param from start of data
   * @param length length of data
   * @return the wave given for chaining
   */
  public Wave generate(Wave wave, long from, long length) {
    return generate(wave, from, length, 1d);
  }
  
  /**
   * Generates a wave. Overwrites the wave in the specified part.
   * 
   * @param wave the wave to write to
   * @param from start of data
   * @param length length of data
   * @param factor the scale factor
   * @return the wave given for chaining
   */
  public Wave generate(Wave wave, long from, long length, double factor) {
    double[] buffer = null;
    int pointer = Wave.BUFFER_SIZE;
    long to = from + length;

    for (long i = from; i < to; i++, pointer++) {
      if (buffer == null || pointer == buffer.length) {
        if (buffer != null) {
          wave.setFrames(i - buffer.length, buffer);
        }

        int size = (int) Math.min(Wave.BUFFER_SIZE, to - i);
        buffer = new double[size];
        pointer = 0;
      }
      double val = generateSample(wave, i, i - from, to - i) * factor;
      buffer[pointer] = val;
    }
    return wave;
  }

  protected abstract double generateSample(Wave wave, long abs, long rel, long left);

}
