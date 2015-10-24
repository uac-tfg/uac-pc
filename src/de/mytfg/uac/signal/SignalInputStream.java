package de.mytfg.uac.signal;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import de.mytfg.uac.util.ByteUtil;
import de.mytfg.uac.wave.Wave;
import de.mytfg.uac.wave.generator.WaveDataGenerator;
import de.mytfg.uac.wave.generator.WaveDataGeneratorSineAmplitude;
import de.mytfg.uac.wave.generator.WaveDataGeneratorSinePhase;

public class SignalInputStream extends InputStream {

  private SignalConfig config;

  private WaveDataGenerator[][] waveDataGenerators;
  private long hopRandomSeed;
  private int samplesPerBit;

  public SignalInputStream(SignalConfig config) {
    this.config = config;
    samplesPerBit = config.getInt("samplingrate") / getBitFrequency();
    hopRandomSeed = config.getLong("fhss.seed");
  }

  @Override
  public int read() throws IOException {
    
    return 0;
  }

  public byte[] decode(Wave wave, int bytes, long start) {
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

      long fromFrame = start + (long) (((double) samplingRate / getBitFrequency()) * currentBitPos);
      long lengthFrames =
          Math.min(numFrames - fromFrame, (long) ((double) samplingRate / getBitFrequency()) + 1);

      if (config.getString("modulation").equals("onoff")) {
        int frequency =
            ((WaveDataGeneratorSineAmplitude) waveDataGenerators[hop][0]).getFrequency();
        double magnitude = wave.getFrequencyMagnitude(frequency, fromFrame, lengthFrames);
        System.out.println("Bit #" + (currentBitPos - 1) + " " + fromFrame + " for " + lengthFrames
            + " frames on " + frequency + " (" + hop + "):" + magnitude);
        if (magnitude > config.getDouble("treshhold")) {
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
      } else if(config.get("modulation").equals("phase")) {
        int frequency =
            ((WaveDataGeneratorSinePhase) waveDataGenerators[hop][0]).getFrequency();
        double phase = wave.getPhaseShift(frequency, fromFrame, lengthFrames);
        System.out.println("Bit #" + currentBitPos + " " + fromFrame + " for " + lengthFrames
            + " frames on " + frequency + " (" + hop + "):" + phase);
        if (phase > 0.25 && phase < 0.75) {
          ByteUtil.setBit(data, currentBitPos, (byte) 1);
        } else {
          ByteUtil.setBit(data, currentBitPos, (byte) 0);
        }
      }
    }
    return data;
  }

  public int getBitFrequency() {
    return waveDataGenerators[0][0].getBitFrequency();
  }

}
