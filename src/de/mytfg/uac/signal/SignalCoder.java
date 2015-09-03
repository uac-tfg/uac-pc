package de.mytfg.uac.signal;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Random;

import de.mytfg.uac.util.ByteUtil;
import de.mytfg.uac.wave.Wave;
import de.mytfg.uac.wave.WaveConfig;
import de.mytfg.uac.wave.generator.WaveDataGenerator;
import de.mytfg.uac.wave.generator.WaveDataGeneratorSineAmplitude;
import de.mytfg.uac.wave.generator.WaveDataGeneratorSinePhase;

public class SignalCoder {

  private SignalConfig config;

  private WaveDataGenerator[][] waveDataGenerators;
  private long hopRandomSeed;

  public SignalCoder(SignalConfig config) {
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

        if (sentBits % config.getInt("fhss.bitsperhop") == 0) {
          hop = hopRandom.nextInt(waveDataGenerators.length);
        }

//        System.out.println("Bit #" + currentPos + " " + ByteUtil.getBit(data, currentPos)
//            + " on hop " + hop + " from " + i);

        sentBits++;
      }
      byte b = ByteUtil.getBit(data, currentPos);
      double val = waveDataGenerators[hop][0].generateSample(w, b, i, i, numFrames - i);
      buffer[pointer] = val;
    }

    return w;
  }

  public byte[] decode(Wave wave, int bytes) {
    byte[] data = new byte[bytes];
    int samplingRate = config.getInt("samplingrate");
    long numFrames =
        (long) ((samplingRate / (double) waveDataGenerators[0][0].getBitFrequency()) * (data.length * 8));
    int hop = 0;
    Random hopRandom = new Random(hopRandomSeed);
    for (int currentBitPos = 0; currentBitPos < bytes * 8; currentBitPos++) {

      if (currentBitPos % config.getInt("fhss.bitsperhop") == 0) {
        hop = hopRandom.nextInt(waveDataGenerators.length);
      }

      long fromFrame = (long) (((double) samplingRate / getBitFrequency()) * currentBitPos);
      long lengthFrames =
          Math.min(numFrames - fromFrame, (long) ((double) samplingRate / getBitFrequency()) + 1);

      if (config.getString("modulation").equals("onoff")) {
        int frequency =
            ((WaveDataGeneratorSineAmplitude) waveDataGenerators[hop][0]).getFrequency();
        double magnitude = wave.getFrequencyMagnitude(frequency, fromFrame, lengthFrames);
//        System.out.println("Bit #" + (currentBitPos - 1) + " " + fromFrame + " for " + lengthFrames
//            + " frames on " + frequency + " (" + hop + "):" + magnitude);
        if (magnitude > config.getInt("treshhold")) {
          ByteUtil.setBit(data, currentBitPos, (byte) 1);
        } else {
          ByteUtil.setBit(data, currentBitPos, (byte) 0);
        }
        // } else if (config.getString("modulation").equals("phase")) {
        // // TODO
        // int frequency = ((WaveDataGeneratorSinePhase) waveDataGenerators[hop][0]).getFrequency();
        // double magnitude =
        // wave.getFrequencyMagnitude(frequency, lastFramePos, currentFramePos - lastFramePos);
        // double phase = wave.getPhaseShift(frequency, lastFramePos, currentFramePos -
        // lastFramePos);
        // // System.out.println(lastFramePos + " to " + currentFramePos + " on " + frequency + " ("
        // +
        // // hop + "):" + magnitude);
        // if (magnitude > config.getInt("treshhold")) {
        // if (phase > 0.25 && phase < 0.75) {
        // ByteUtil.setBit(data, currentBitPos - 1, (byte) 1);
        // } else {
        // ByteUtil.setBit(data, currentBitPos - 1, (byte) 0);
        // }
        // }
      } // TODO
    }
    return data;
  }

  public int getBitFrequency() {
    return waveDataGenerators[0][0].getBitFrequency();
  }

}
