package de.mytfg.uac.signal;

import java.io.IOException;
import java.io.InputStream;

import de.mytfg.uac.util.ByteUtil;
import de.mytfg.uac.wave.stream.GoertzelManager;
import de.mytfg.uac.wave.stream.InputWave;
import de.mytfg.uac.wave.stream.InputWaveDouble;

public class SignalInputStream extends InputStream {
  
  private InputWave in;
  private SignalConfig config;
  private GoertzelManager goertzel;
  
  private int samplesPerBit;
  private int samplingrate;
  private double threshold;

  public SignalInputStream(InputWave in, SignalConfig config) {
    this.in = in;
    this.config = config;
    samplingrate = config.getInt("samplingrate");
    threshold = config.getDouble("threshold");
    samplesPerBit = samplingrate / getBitFrequency();
    goertzel = new GoertzelManager(in, samplingrate);
  }

  @Override
  public int read() throws IOException {
    byte data = 0;
    int frequency = config.getInt("mainfrequency");
    for(int i = 0; i < 8; i++) {
      boolean symbol = readSymbol(frequency);
      if (symbol) {
        data = ByteUtil.setBit(data, i, (byte) 1);
      }
    }
    return data;
  }
  
  public void synchronize() throws IOException {
    double[] samples = new double[samplesPerBit * 2];
    int maxOffset = -1;
    double maxMagnitude = 0;
    int targetFrequency = config.getInt("mainfrequency");
    
    while(maxOffset == -1) {
      in.readSample(samples);
      InputWaveDouble inBuffer = new InputWaveDouble(samples);
      for(int i = 0; i < samplesPerBit; i++) {
        inBuffer.skip(i);
        GoertzelManager g = new GoertzelManager(inBuffer, samplingrate);
        g.doBlock(samplesPerBit, targetFrequency);
        double mag = g.getMagnitude();
        if(mag > threshold && mag > maxMagnitude) {
          maxOffset = i;
          maxMagnitude = mag;
        }
        inBuffer.reset();
      }
    }
    in.skip(maxOffset + samplesPerBit * 6);
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
    goertzel.doBlock(samplesPerBit, frequency);
    double magnitude = goertzel.getMagnitude();
    return magnitude > threshold;
  }

  public int getBitFrequency() {
    return config.getInt("mainfrequency") / config.getInt("periodsperbit");
  }

}
