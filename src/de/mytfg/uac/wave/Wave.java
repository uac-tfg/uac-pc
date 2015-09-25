package de.mytfg.uac.wave;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import de.mytfg.uac.util.ComplexNumber;
import de.mytfg.uac.wave.wav.RandomAccessWavFile;
import de.mytfg.uac.wave.wav.WavFileException;

/**
 * Represents a digitally sampled wave. Uses a file as a buffer to save the samples to in .wav
 * format.
 * 
 * @author Tilman Hoffbauer
 */
public class Wave {

  public static final int BUFFER_SIZE = 1024;

  private RandomAccessWavFile wav;
  private WaveConfig config;

  /**
   * Creates a new wave object.
   * 
   * @param file the file to use as a buffer
   * @param config the config to use
   */
  public Wave(File file, WaveConfig config) {
    this.config = config;
    try {
      this.wav =
          RandomAccessWavFile.newWavFile(file, 1, config.getNumFrames(), config.getValidBits(),
              config.getSampleRate());
      clear(); // allocate file
    } catch (IOException | WavFileException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Opens an existing wave object.
   * 
   * @param file the wave's file
   */
  public Wave(File file) {
    try {
      this.wav = RandomAccessWavFile.openWavFile(file);
    } catch (IOException | WavFileException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Gets a set of frames and saves them in the array.
   * 
   * @param from
   * @param length
   * @return the frames
   */
  public double[] getFrames(long from, int length) {
    if (from < 0 || from + length > getNumFrames()) {
      throw new IndexOutOfBoundsException();
    }

    double[][] samples = new double[1][length];
    int read;
    try {
      wav.seek(from, 0);
      read = wav.readFrames(samples, length);
    } catch (IOException | WavFileException e) {
      throw new RuntimeException("Error while reading from underlying .wav!", e);
    }
    if (read != length)
      throw new RuntimeException("Reached end of file before all samples were read!");
    return samples[0];
  }

  /**
   * Sets the specified frames to the given values.
   * 
   * @param from
   * @param frames
   */
  public void setFrames(long from, double[] frames) {
    Objects.requireNonNull(frames);
    int numFrames = frames.length;
    if (from < 0 || from + numFrames > getNumFrames()) {
      System.out.println(numFrames + " | " + from);
      throw new IndexOutOfBoundsException(String.valueOf(from));
    }

    double[][] buffer = new double[][] {frames};

    try {
      wav.seek(from, 0);
      wav.writeFrames(buffer, numFrames);
    } catch (IOException | WavFileException e) {
      throw new RuntimeException("Error while writing to underlying .wav!", e);
    }
  }

  /**
   * Adds a wave to the local wave. In the process of adding, interference is simulated. This means
   * that every sample is added one by one. If the value gets higher than or lower than 1 or -1, the
   * frame can't be saved correct because of a overflow.
   * 
   * @param wave the wave to add
   * @param toStart the starting position in this wave
   * @param fromStart the starting position in the given wave
   * @param length the number of frames to add
   * @param a scale factor, set to 1 for no effect
   */
  public void addWave(Wave wave, long toStart, long fromStart, long length, double toFactor,
      double fromFactor) {
    double[] fromBuffer = null;
    double[] toBuffer = null;
    int pointer = BUFFER_SIZE;
    for (long i = 0; i <= length; i++, pointer++) {
      if (fromBuffer == null || toBuffer == null || pointer >= fromBuffer.length) {
        if (fromBuffer != null && toBuffer != null) {
          setFrames(i + toStart - toBuffer.length, toBuffer);
        }

        if (i == length) {
          break;
        }

        int size = (int) Math.min(BUFFER_SIZE, length - i);
        fromBuffer = wave.getFrames(i + fromStart, size);
        toBuffer = this.getFrames(i + toStart, size);
        pointer = 0;
      }
      toBuffer[pointer] = toBuffer[pointer] * toFactor + fromBuffer[pointer] * fromFactor;
    }
  }

  public void addWave(Wave wave, long toStart, long fromStart, long length, double fromFactor) {
    addWave(wave, toStart, fromStart, length, 1, fromFactor);
  }

  /**
   * Gets the range of the wave, that is the minimum and maximum amplitude.
   * 
   * @return the range, first element is the minimum, second the maximum
   */
  public double[] getRange() {
    double[] range = new double[] {0, 0};
    double[] buffer = null;
    int pointer = BUFFER_SIZE;
    for (long i = 0; i < getNumFrames(); i++, pointer++) {
      if (pointer == BUFFER_SIZE) {
        int size = (int) Math.min(BUFFER_SIZE, getNumFrames() - i);
        buffer = getFrames(i, size);
        pointer = 0;
      }
      double val = buffer[pointer];
      if (val < range[0]) {
        range[0] = val;
      }
      if (val > range[1]) {
        range[1] = val;
      }
    }
    return range;
  }

  /**
   * Gets the magnitude of a specific frequency in the signal.
   * 
   * @return the real magnitude of the given frequency in the signal
   */
  public double getFrequencyMagnitude(int targetFrequency, long from, long length) {
    ComplexNumber c = new ComplexNumber();
    c = this.initGoertzel(targetFrequency, from, length);
    return (c.getReal() * c.getReal() + c.getIma() * c.getIma());
  }

  public double getFrequencyMagnitude(int targetFrequency) {
    long from = 0;
    long length = this.getNumFrames();
    return this.getFrequencyMagnitude(targetFrequency, from, length);
  }

  /**
   * Uses Goertzel Algorithm for giving the phase shift of a specific frequency in the signal
   * 
   * @param targetFrequency
   * @return the phaseshift in values 0-1
   */
  public double getPhaseShift(int targetFrequency, long from, long length) {
    ComplexNumber c = new ComplexNumber();
    c = this.initGoertzel(targetFrequency, from, length);
    double r = Math.atan2(c.getIma(), c.getReal()); // r ∈ (-π, +π)
    r += Math.PI; // r ∈ (0, +2π)
    r = (r / Math.PI / 2d); // r ∈ (0, +1)
    return 1d - r; // r ∈ (+1, 0)
  }

  public double getPhaseShift(int targetFrequency) {
    long from = 0;
    long length = this.getNumFrames();
    return this.getPhaseShift(targetFrequency, from, length);
  }

  /**
   * Uses Goertzel Algorithm on the class
   * 
   * @param targetFrequency
   * @return a complex number, containing real. and imag. part
   */
  public ComplexNumber initGoertzel(int targetFrequency, long from, long length) {
    if (from + length > this.getNumFrames() || from < 0 || length <= 0) {
      throw new IndexOutOfBoundsException("from + length is out of range!");
    }
    double k = (((double) length * (double) targetFrequency) / (double) this.getSampleRate());
    double omega =
        (2d * Math.PI * k) / (double) length;
    double sin = Math.sin(omega);
    double cos = Math.cos(omega);
    double a1 = 2.0 * cos;
    double d1 = 0;
    double d2 = 0;
    double d0;
    int pointer = BUFFER_SIZE;
    double[] buffer = null;
    for (long i = from; i < (from + length); i++, pointer++) {
      if (pointer == BUFFER_SIZE) {
        int size = (int) Math.min(BUFFER_SIZE, from + length - i);
        buffer = getFrames(i, size);
        pointer = 0;
      }
      d0 = a1 * d1 - d2 + buffer[pointer];
      d2 = d1;
      d1 = d0;
    }
    d0 = a1 * d1 - d2;
    d2 = d1;
    d1 = d0;
    ComplexNumber c = new ComplexNumber(d1 - d2 * cos, d2 * sin);
    return c;
  }

  public void scale(double factor) {
    double[] buffer = null;
    int pointer = BUFFER_SIZE;
    for (long i = 0; i <= getNumFrames(); i++, pointer++) {
      if (buffer == null || pointer >= buffer.length) {
        if (buffer != null) {
          setFrames(i - buffer.length, buffer);
        }

        if (i == getNumFrames()) {
          break;
        }

        int size = (int) Math.min(BUFFER_SIZE, getNumFrames() - i);
        buffer = this.getFrames(i, size);
        pointer = 0;
      }
      buffer[pointer] = buffer[pointer] * factor;
    }
  }

  /**
   * Clears the wave, i.e. overwrites the frames with zeros.
   */
  public void clear() {
    int toWrite;
    long i;
    for (i = 0; i < getNumFrames(); i += toWrite) {
      toWrite = (int) Math.min(BUFFER_SIZE, getNumFrames() - i);
      double[] buffer = new double[toWrite];
      setFrames(i, buffer);
    }
  }

  /**
   * Closes the underlying buffer file.
   */
  public void close() {
    try {
      wav.close();
    } catch (IOException e) {
      throw new RuntimeException("Error while writing to underlying .wav!", e);
    }
  }

  /**
   * Deletes the wave
   */
  public void delete() {
    close();
    getFile().delete();
  }

  public long getSampleRate() {
    return wav.getSampleRate();
  }

  public long getNumFrames() {
    return wav.getNumFrames();
  }

  public double getDuration() {
    return (1.0 / wav.getSampleRate()) * wav.getNumFrames();
  }

  public int getValidBits() {
    return wav.getValidBits();
  }

  public long getFramesRemaining() {
    return wav.getFramesRemaining();
  }

  public long getCurrentFrame() {
    return wav.getNumFrames() - wav.getFramesRemaining();
  }

  public File getFile() {
    return wav.getFile();
  }

  public WaveConfig getConfig() {
    return config;
  }

}
