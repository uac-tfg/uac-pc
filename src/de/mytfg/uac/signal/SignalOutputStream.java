package de.mytfg.uac.signal;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

import de.mytfg.uac.util.ByteUtil;
import de.mytfg.uac.wave.Wave;
import de.mytfg.uac.wave.WaveConfig;
import de.mytfg.uac.wave.generator.WaveDataGenerator;
import de.mytfg.uac.wave.generator.WaveDataGeneratorSineAmplitude;
import de.mytfg.uac.wave.generator.WaveDataGeneratorSinePhase;

public class SignalOutputStream extends DataOutputStream {

  private SignalConfig config;

  private WaveDataGenerator[][] waveDataGenerators;
  private long hopRandomSeed;
  private int framesPerBit;
  private int samplesPerBit;

  public SignalOutputStream(OutputStream out, SignalConfig config) {
    super(out);
    this.config = config;

    int fhssCount = config.getInt("fhss.count");
    int fhssWidth = config.getInt("fhss.width");
    int dsssCount = config.getInt("dsss.count");
    int dsssWidth = config.getInt("dsss.width");
    int mainFrequency = config.getInt("mainfrequency");
    int lowestFrequency = mainFrequency - (fhssCount * fhssWidth + dsssCount * dsssWidth) / 2;
    int bitFrequency = lowestFrequency / config.getInt("periodsperbit");
    waveDataGenerators = new WaveDataGenerator[fhssCount][dsssCount];
    for (int i = 0; i < fhssCount; i++) {
      for (int j = 0; j < dsssCount; j++) {
        int frequency = fhssWidth * i + dsssWidth * j + lowestFrequency;
        WaveDataGenerator generator = null;
        if (config.getString("modulation").equals("phase")) {
          generator = new WaveDataGeneratorSinePhase(frequency, bitFrequency);
        } else if (config.getString("modulation").equals("onoff")) {
          generator = new WaveDataGeneratorSineAmplitude(frequency, bitFrequency);
        } // TODO
        waveDataGenerators[i][j] = generator;
      }
    }
    hopRandomSeed = config.getLong("fhss.seed");
    samplesPerBit = config.getInt("samplingrate") / getBitFrequency();
  }

  @Override
  public void write(int ib) throws IOException {
    byte b = (byte) ib;
    int frequency = config.getInt("mainfrequency");
    int samplerate = config.getInt("samplerate");
    for(int i = 0; i < 8; i++) {
      byte bit = ByteUtil.getBit(b, i);
      if(bit == 0) {
        for(int j = 0; j < samplesPerBit; j++) {
          double period = frequency * 2 * Math.PI;
          double time = j * (1d / samplerate);
          double val = Math.sin(period * time);
          writeDouble(val);
        }
      }
    }
  }

  public Wave encode(byte[] data) throws IOException {
    int samplingRate = config.getInt("samplingrate");
    long numFrames =
        (long) ((samplingRate / (double) waveDataGenerators[0][0].getBitFrequency()) * (data.length * 8));
    WaveConfig waveConfig = new WaveConfig(16, samplingRate, numFrames);
    Wave w = new Wave(File.createTempFile("signal", ".wav"), waveConfig);

    Random hopRandom = new Random(hopRandomSeed);
    int hop = 0;
    int sentBits = 0;
    long lastPos = -1;
    double[] buffer = null;
    int pointer = 0;
    long lastPosFrame = 0;
    for (long i = 0; i <= numFrames; i++, pointer++) {
      if (buffer == null || pointer == buffer.length) {
        if (buffer != null) {
          w.setFrames(i - buffer.length, buffer);
        }
        if (i == numFrames) {
          break;
        }

        int length = (int) Math.min(w.getNumFrames() - i, Wave.BUFFER_SIZE);
        buffer = new double[length];
        pointer = 0;
      }

      double currentPosDouble = (i / (double) samplingRate) * getBitFrequency();
      long currentPos = (long) (currentPosDouble);
      if (currentPos > lastPos) {
        lastPos = currentPos;
        lastPosFrame = i;

        if (sentBits % config.getInt("fhss.bitsperhop") == 0) {
          hop = hopRandom.nextInt(waveDataGenerators.length);
        }

        System.out.println("Bit #" + currentPos + " " + ByteUtil.getBit(data, currentPos)
            + " on hop " + hop + " from " + i);

        sentBits++;
      }
      byte b = ByteUtil.getBit(data, currentPos);
      long relBitPos = i - lastPosFrame;
      double val = waveDataGenerators[hop][0].generateSample(w, b, relBitPos, relBitPos, numFrames - i);
      buffer[pointer] = val;
    }

    return w;
  }

  public int getBitFrequency() {
    return waveDataGenerators[0][0].getBitFrequency();
  }

}
