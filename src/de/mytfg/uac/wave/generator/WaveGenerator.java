package de.mytfg.uac.wave.generator;

import de.mytfg.uac.wave.Wave;

public abstract class WaveGenerator {

  /**
   * Generates a wave. Overwrites the wave in the specified part.
   * 
   * @param wave the wave to write to
   * @param from start of data
   * @param length length of data
   * @return the wave given for chaining
   */
  public Wave generate(Wave wave, long from, long length) {
    double[] buffer = null;
    int pointer = Wave.BUFFER_SIZE;
    long to = from + length;

    for (long i = from; i < to; i++, pointer++) {
      if (pointer == buffer.length) {
        if (buffer != null) {
          wave.setFrames(i - buffer.length, buffer);
        }

        int size = (int) Math.min(Wave.BUFFER_SIZE, to - i);
        buffer = new double[size];
      }
      double val = generateSample(wave, i, i - from, to - i);
      buffer[pointer] = val;
    }
    return wave;
  }

  protected abstract double generateSample(Wave wave, long abs, long rel, long left);

}
