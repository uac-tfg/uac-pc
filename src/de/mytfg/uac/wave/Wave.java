package de.mytfg.uac.wave;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import de.mytfg.uac.wave.wav.RandomAccessWavFile;
import de.mytfg.uac.wave.wav.WavFileException;

public class Wave {

  private RandomAccessWavFile wav;

  public Wave(File file, int numChannels, long numFrames, int validBits, int sampleRate) {
    try {
      this.wav = RandomAccessWavFile
              .newWavFile(file, numChannels, numFrames, validBits, sampleRate);
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
    if (from < 0 || from + length > getNumFrames())
      throw new IndexOutOfBoundsException();

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

}
