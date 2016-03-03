package de.mytfg.uac.signal;

import java.io.IOException;
import java.io.InputStream;

import de.mytfg.uac.util.ByteUtil;
import de.mytfg.uac.wave.stream.GoertzelManager;
import de.mytfg.uac.wave.stream.GoertzelParallelized;
import de.mytfg.uac.wave.stream.InputWave;

public class SignalInputStream extends InputStream {

  private SignalConfig config;
  private int samplesPerBit;
  private int samplingrate;
  private double threshold;
  private String modulation;
  private int bitFrequency;

  private int offset;
  private GoertzelParallelized[] goertzels;
  private GoertzelManager goertzelManager;

  public SignalInputStream(InputWave in, SignalConfig config) {
    this.config = config;
    samplingrate = config.getInt("samplingrate");
    modulation = config.getString("modulation");
    int periodsPerBit = config.getInt("periodsperbit");


    if (modulation.equals("am")) {
      int frequency = config.getInt("mainfrequency");
      threshold = config.getDouble("threshold");
      bitFrequency = frequency / periodsPerBit;
      samplesPerBit = samplingrate / bitFrequency;
    } else if (modulation.equals("fm")) {
      int high = config.getInt("frequency.high");
      int low = config.getInt("frequency.low");
      bitFrequency = Math.min(low, high) / periodsPerBit;
      samplesPerBit = samplingrate / bitFrequency;
    }

    goertzelManager = new GoertzelManager(in, samplingrate, samplesPerBit);
    if (modulation.equals("am")) {
      threshold = config.getDouble("threshold");
      int frequency = config.getInt("mainfrequency");
      goertzels = new GoertzelParallelized[samplesPerBit];
      for (int i = 0; i < samplesPerBit; i++) {
        goertzels[i] = new GoertzelParallelized(frequency, i);
      }
    } else if (modulation.equals("fm")) {
      int high = config.getInt("frequency.high");
      int low = config.getInt("frequency.low");
      goertzels = new GoertzelParallelized[samplesPerBit * 2];
      for (int i = 0; i < samplesPerBit; i++) {
        goertzels[i * 2] = new GoertzelParallelized(high, i);
        goertzels[i * 2 + 1] = new GoertzelParallelized(low, i);
      }
    } else {
      throw new IllegalArgumentException("Unknown modulation type!");
    }
    goertzelManager.add(goertzels);
  }

  @Override
  public int read() throws IOException {
    byte data = 0;
    for (int i = 0; i < 8; i++) {
      boolean symbol = readSymbol();
      if (symbol) {
        data = ByteUtil.setBit(data, i, (byte) 1);
      }
    }
    return data;
  }

  public void synchronize() throws IOException {
    for (GoertzelParallelized g : goertzels) {
      g.setEnabled(true);
    }

    if (modulation.equals("am")) {
      synchronizeAm();
    } else if (modulation.equals("fm")) {
      synchronizeFm();
    }

    for (GoertzelParallelized g : goertzels) {
      if (g.getOffset() != offset) {
        g.setEnabled(false);
      }
    }
  }

  private void synchronizeFm() throws IOException {
    String sBits = config.getString("syncbits");
    byte[] bits = new byte[sBits.length()];
    for(int i = 0; i < sBits.length(); i++) {
      if(sBits.charAt(i) == '1') {
        bits[i] = 1;
      }
    }

    int[] positions = new int[goertzels.length / 2];
    double[][] mags = new double[positions.length][2];
    
    int maxOffset = -1;
    int i = 0;

    while (true) {
      goertzelManager.processSample();
      int offset = goertzelManager.getOffset();
      GoertzelParallelized high = goertzels[offset * 2];
      GoertzelParallelized low = goertzels[offset * 2 + 1];
      double highMag = high.getMagnitude();
      double lowMag = low.getMagnitude();
      double diff = highMag - lowMag;
      if (diff > 0 && bits[positions[offset]] == 1) {
        positions[offset]++;
        mags[offset][0] += highMag;
      } else if (bits[positions[offset]] == 0) {
        positions[offset]++;
        mags[offset][1] += lowMag;
      } else {
        positions[offset] = 0;
        mags[offset][0] = 0;
        mags[offset][1] = 0;
      }
      if (positions[offset] == bits.length) {
        if(maxOffset == -1 || mags[offset][0] - mags[maxOffset][0] + mags[offset][1] - mags[maxOffset][1] > 0) { // TODO
          maxOffset = offset;
        }
        positions[offset] = 0;
        i++;
      }
      if(i != 0) {
        i++;
        if(i == samplesPerBit) {
          break;
        }
      }
    }
    offset = maxOffset;
  }
  private void synchronizeAm() throws IOException {
    GoertzelParallelized max = null;
    int i = 0;

    while (true) {
      goertzelManager.processSample();
      GoertzelParallelized g = null;
      g = goertzels[goertzelManager.getOffset()];
      double mag = g.getMagnitude();
      if (mag == -1 || (mag < threshold && i == 0)) {
        i = 0;
        continue;
      }
      if (max == null || max.getMagnitude() < g.getMagnitude()) {
        max = g;
      }
      i++;
      if (i >= samplesPerBit) {
        break;
      }
    }
    offset = max.getOffset();

    goertzelManager.processSamples(samplesPerBit * 7);
  }

  public void waitFor(byte b) throws IOException {
    int pos = 0;
    while (true) {
      boolean symbol = readSymbol();
      if (symbol && ByteUtil.getBit(b, pos) == 1) {
        pos++;
      } else if (!symbol && ByteUtil.getBit(b, pos) == 0) {
        pos++;
      } else {
        pos = 0;
      }
      if (pos == 8) {
        break;
      }
    }
  }

  public boolean readSymbol() throws IOException {
    goertzelManager.processSamples(samplesPerBit);
    if (modulation.equals("am")) {
      GoertzelParallelized goertzel = goertzels[offset];
      double mag = goertzel.getMagnitude();
      return mag > threshold;
    } else if (modulation.equals("fm")) {
      double highMag = goertzels[offset * 2].getMagnitude();
      double lowMag = goertzels[offset * 2 + 1].getMagnitude();
      double diff = highMag - lowMag;
      return diff > 0;
    }
    return false;
  }

  public int getBitFrequency() {
    return bitFrequency;
  }

}
