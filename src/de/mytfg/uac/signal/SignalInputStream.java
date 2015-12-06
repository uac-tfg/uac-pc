package de.mytfg.uac.signal;

import java.io.IOException;
import java.io.InputStream;

import de.mytfg.uac.util.ByteUtil;
import de.mytfg.uac.util.ComplexNumber;
import de.mytfg.uac.wave.stream.Goertzel;
import de.mytfg.uac.wave.stream.InputWave;

public class SignalInputStream extends InputStream {
  
  private InputWave in;
  private SignalConfig config;
  private Goertzel goertzel;
  
  private long hopRandomSeed;
  private int samplesPerBit;
  private int samplingrate;

  public SignalInputStream(InputWave in, SignalConfig config) {
    this.in = in;
    this.config = config;
    samplingrate = config.getInt("samplingrate");
    samplesPerBit = samplingrate / getBitFrequency();
    hopRandomSeed = config.getLong("fhss.seed");
    goertzel = new Goertzel(in, samplingrate);
  }

  @Override
  public int read() throws IOException {
    byte data = 0;
    double treshhold = config.getDouble("treshhold");
    for(int i = 0; i < 8; i++) {
      goertzel.doBlock(samplesPerBit, config.getInt("mainfrequency"));
      double magnitude = goertzel.getMagnitude();
      if (magnitude > treshhold) {
        data = ByteUtil.setBit(data, i, (byte) 1);
      }
    }
    return data;
  }
  
  public void waitFor(byte b) throws IOException {
    int pos = 0;
    double treshhold = config.getDouble("treshhold");
    while(true) {
      goertzel.doBlock(samplesPerBit, config.getInt("mainfrequency"));
      double magnitude = goertzel.getMagnitude();
      if(magnitude > treshhold && ByteUtil.getBit(b, pos) == 1) {
        pos++;
      } else if(magnitude < treshhold && ByteUtil.getBit(b, pos) == 0) {
        pos++;
      } else {
        pos = 0;
      }
      if(pos == 8) {
        break;
      }
    }
  }

  public int getBitFrequency() {
    return config.getInt("mainfrequency") / config.getInt("periodsperbit");
  }

}
