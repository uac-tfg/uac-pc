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
      this.wav = RandomAccessWavFile.newWavFile(file, config.getNumChannels(), 
          config.getNumFrames(), config.getValidBits(), config.getSampleRate());
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

  public double[][] getFrames(long from, int length) {
    if (from < 0 || from + length > getNumFrames()) {
      throw new IndexOutOfBoundsException();
    }

    double[][] samples = new double[getNumChannels()][length];
    int read;
    try {
      wav.seek(from, 0);
      read = wav.readFrames(samples, length);
    } catch (IOException | WavFileException e) {
      throw new RuntimeException("Error while reading from underlying .wav!", e);
    }
    if (read != length)
      throw new RuntimeException("Reached end of file before all samples were read!");
    return samples;
  }

  public double[] getSamples(int channel, long from, int length) {
    if (channel < 0 || channel > this.getNumChannels()) {
      throw new IndexOutOfBoundsException(String.valueOf(channel));
    }
    return getFrames(from, length)[channel];
  }

  public void setFrames(long from, double[][] samples) {
    Objects.requireNonNull(samples);
    if (samples.length != getNumChannels())
      throw new IllegalArgumentException("Given sample does not have the same amount of channels!");
    int numFrames = samples[0].length;
    if (from < 0 || from + numFrames > getNumFrames())
      throw new IndexOutOfBoundsException(String.valueOf(from));

    try {
      wav.seek(from, 0);
      wav.writeFrames(samples, numFrames);
    } catch (IOException | WavFileException e) {
      throw new RuntimeException("Error while writing to underlying .wav!", e);
    }
  }
  
  public void setSamples(int channel, long from, double[] samples) {
    double[][] d = getFrames(from, samples.length);
    d[channel] = samples;
    setFrames(from, d);
  }
  
  public void setSamples(long from, double[] samples) {
    setSamples(0, from, samples);
  }

  public void addWave(int fromChannel, int toChannel, Wave wave, long fromStart, long toStart, 
      long length) {
    if (toChannel < 0 || toChannel > this.getNumChannels()) {
      throw new IndexOutOfBoundsException(String.valueOf(toChannel));
    }
    if (fromChannel < 0 || fromChannel > wave.getNumChannels()) {
      throw new IndexOutOfBoundsException(String.valueOf(fromChannel));
    }
    double[] fromBuffer = null;
    double[] toBuffer = null;
    int pointer = BUFFER_SIZE;
    for (long i = 0; i < length; i++, pointer++) {
      if (pointer == BUFFER_SIZE) {
        setSamples(toChannel, i + toStart, toBuffer);
        
        int size = (int) Math.min(BUFFER_SIZE, length - i);
        fromBuffer = wave.getSamples(fromChannel, i + fromStart, size);
        toBuffer = this.getSamples(toChannel, i + toStart, size);
      }
      toBuffer[pointer] = toBuffer[pointer] + fromBuffer[pointer];
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

  public int getNumChannels() {
    return wav.getNumChannels();
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
