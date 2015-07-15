package de.mytfg.uac.wave.generator;

import de.mytfg.uac.util.ByteUtil;
import de.mytfg.uac.wave.Wave;

public abstract class WaveDataGenerator {
  
  /**
   * Generates a wave. Overwrites the wave in the specified part.
   * The scale factor is defaulted to 1.
   * 
   * @param wave the wave to write to
   * @param from start of data
   * @param length length of data
   * @return the wave given for chaining
   */
  public Wave generate(Wave wave, byte[] data, long from, long length) {
    return generate(wave, data, from, length, 1d);
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
  public Wave generate(Wave wave, byte[] data, long from, long length, double factor) {
    double[] sampleBuffer = null;
    int samplePointer = Wave.BUFFER_SIZE;
    long to = from + length;
    int bitFrequency = getBitFrequency();

    for (long i = from; i < to; i++, samplePointer++) {
      if (sampleBuffer == null || samplePointer == sampleBuffer.length) {
        if (sampleBuffer != null) {
          wave.setFrames(i - sampleBuffer.length, sampleBuffer);
        }

        int size = (int) Math.min(Wave.BUFFER_SIZE, to - i);
        sampleBuffer = new double[size];
        samplePointer = 0;
      }
      long rel = i - from;
      byte bit = ByteUtil.getBit(data, rel);
      double val = generateSample(wave, bit, i, i - from, to - i) * factor;
      sampleBuffer[samplePointer] = val;
    }
    return wave;
  }

  private static byte getBit(byte[] data, long pos) {
    int posByte = (int) (pos / 8);
    int posBit = (int) (pos % 8);
    byte valByte = data[posByte];
    byte valBit = (byte) (valByte >> (8 - (posBit + 1)) & 0x0001);
    return valBit;
  }

  protected abstract double generateSample(Wave wave, byte b, long abs, long rel, long left);
  
  public abstract int getBitFrequency();
  
}
