package de.mytfg.uac.signal;

import java.io.IOException;
import java.io.InputStream;

import de.mytfg.uac.util.ByteUtil;
import de.mytfg.uac.wave.stream.GoertzelManager;
import de.mytfg.uac.wave.stream.GoertzelParallelized;
import de.mytfg.uac.wave.stream.InputWave;

public class SignalInputStream extends InputStream {
  
  private SignalConfig config;
  private GoertzelParallelized goertzel;
  private GoertzelParallelized[] goertzels;
  private GoertzelManager goertzelManager;
  
  private int samplesPerBit;
  private int samplingrate;
  private double threshold;
  private int frequency;

  public SignalInputStream(InputWave in, SignalConfig config) {
    this.config = config;
    samplingrate = config.getInt("samplingrate");
    threshold = config.getDouble("threshold");
    int frequency = config.getInt("mainfrequency");
    samplesPerBit = samplingrate / getBitFrequency();
    goertzelManager = new GoertzelManager(in, samplingrate, samplesPerBit);
    goertzels = new GoertzelParallelized[samplesPerBit];
    for(int i = 0; i < samplesPerBit; i++) {
      goertzels[i] = new GoertzelParallelized(frequency, i);
    }
    goertzelManager.add(goertzels);
  }

  @Override
  public int read() throws IOException {
    byte data = 0;
    for(int i = 0; i < 8; i++) {
      boolean symbol = readSymbol(frequency);
      if (symbol) {
        data = ByteUtil.setBit(data, i, (byte) 1);
      }
    }
    return data;
  }
  
  public void synchronize() throws IOException {
    for(GoertzelParallelized g : goertzels) {
      g.setEnabled(true);
    }
    
    GoertzelParallelized max = null;
    int i = 0;
    
    while(true) {
      goertzelManager.processSample();
      GoertzelParallelized g = goertzels[goertzelManager.getOffset()];
      double mag = g.getMagnitude();
//      System.out.print(i != 0 ? (i + "\n") : "");
      if(mag == -1 || mag < threshold) {
        i = 0;
        continue;
      }
//      System.out.print("High @ " + g.getOffset() + ":\t" + g.getMagnitude());
      if(max == null || max.getMagnitude() < g.getMagnitude()) {
//        System.out.print(" MAX");
        max = g;
      }
//      System.out.println();
      i++;
      if(i >= samplesPerBit * (1.125)) {
//        System.out.println("Done at " + i);
        break;
      }
    }
    goertzel = max;
    System.out.println("SELECT " + goertzel.getOffset() + ":\t" + goertzel.getMagnitude());
    
    for(GoertzelParallelized g : goertzels) {
      if(g.getOffset() != max.getOffset()) {
        g.setEnabled(false);
      }
    }
    
    goertzelManager.processSamples(samplesPerBit * 7);
  }
  
  public void waitFor(byte b) throws IOException {
    int pos = 0;
    while(true) {
      int frequency = config.getInt("mainfrequency");
      boolean symbol = readSymbol(frequency);
      if(symbol && ByteUtil.getBit(b, pos) == 1) {
        pos++;
      } else if(!symbol && ByteUtil.getBit(b, pos) == 0) {
        pos++;
      } else {
        pos = 0;
      }
      if(pos == 8) {
        break;
      }
    }
  }
  
  public boolean readSymbol(int frequency) throws IOException {
    goertzelManager.processSamples(samplesPerBit);
    double magnitude = goertzel.getMagnitude();
    return magnitude > threshold;
  }

  public int getBitFrequency() {
    return config.getInt("mainfrequency") / config.getInt("periodsperbit");
  }

}
