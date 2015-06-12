package de.mytfg.uac.wave;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import de.mytfg.uac.wave.wav.RandomAccessWavFile;
import de.mytfg.uac.wave.wav.WavFileException;

/**
 * Represents a digitally sampled wave. Uses a file as a buffer to save the samples to in .wav
 * format.
 * 
 * @author Tilman Hoffbauer
 */
public class Wave {

  private static final int BUFFER_SIZE = 1024;

  private RandomAccessWavFile wav;

  /**
   * Creates a new wave object.
   * 
   * @param file the file to use as a buffer
   * @param config the config to use
   */
  public Wave(File file, WaveConfig config) {
    try {
      this.wav =
          RandomAccessWavFile.newWavFile(file, config.getNumChannels(), config.getNumFrames(),
              config.getValidBits(), config.getSampleRate());
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
   */
  public void addWave(Wave wave, long toStart, long fromStart, long length) {
    double[] fromBuffer = null;
    double[] toBuffer = null;
    int pointer = BUFFER_SIZE;
    for (long i = 0; i < length; i++, pointer++) {
      if (pointer >= BUFFER_SIZE) {
        if (fromBuffer != null && toBuffer != null) {
          setFrames(i + toStart - toBuffer.length, toBuffer);
        }

        int size = (int) Math.min(BUFFER_SIZE, length - i);
        fromBuffer = wave.getFrames(i + fromStart, size);
        toBuffer = this.getFrames(i + toStart, size);
        pointer = 0;
      }
      toBuffer[pointer] = toBuffer[pointer] + fromBuffer[pointer];
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

}
