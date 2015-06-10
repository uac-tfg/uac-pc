package de.mytfg.uac.wave;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import de.mytfg.uac.wave.wav.RandomAccessWavFile;
import de.mytfg.uac.wave.wav.WavFileException;

public class Wave {

  private static final int BUFFER_SIZE = 1024;

  private RandomAccessWavFile wav;

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

  public Wave(File file) {
    try {
      this.wav = RandomAccessWavFile.openWavFile(file);
    } catch (IOException | WavFileException e) {
      throw new RuntimeException(e);
    }
  }

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

  public void setFrames(long from, double[] samples) {
    Objects.requireNonNull(samples);
    int numFrames = samples.length;
    if (from < 0 || from + numFrames > getNumFrames()) {
      System.out.println(numFrames + " | " + from);
      throw new IndexOutOfBoundsException(String.valueOf(from));
    }

    double[][] buffer = new double[][] {samples};

    try {
      wav.seek(from, 0);
      wav.writeFrames(buffer, numFrames);
    } catch (IOException | WavFileException e) {
      throw new RuntimeException("Error while writing to underlying .wav!", e);
    }
  }

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

  public void clear() {
    int toWrite;
    long i;
    for (i = 0; i < getNumFrames(); i += toWrite) {
      toWrite = (int) Math.min(BUFFER_SIZE, getNumFrames() - i);
      double[] buffer = new double[toWrite];
      setFrames(i, buffer);
    }
  }

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
