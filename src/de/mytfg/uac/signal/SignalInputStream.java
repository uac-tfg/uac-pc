package de.mytfg.uac.signal;

import java.io.IOException;
import java.io.InputStream;

import de.mytfg.uac.util.ByteUtil;
import de.mytfg.uac.util.ComplexNumber;
import de.mytfg.uac.wave.stream.InputWave;

public class SignalInputStream extends InputStream {
  
  private InputWave in;
  private SignalConfig config;
  
  private long hopRandomSeed;
  private int samplesPerBit;
  private int samplingrate;

  public SignalInputStream(InputWave in, SignalConfig config) {
    this.in = in;
    this.config = config;
    samplingrate = config.getInt("samplingrate");
    samplesPerBit = samplingrate / getBitFrequency();
    hopRandomSeed = config.getLong("fhss.seed");
  }

  @Override
  public int read() throws IOException {
    byte data = 0;
    double treshhold = config.getDouble("treshhold");
    for(int i = 0; i < 8; i++) {
      double magnitude = getFrequencyMagnitude(config.getInt("mainfrequency"), samplesPerBit);
      if (magnitude > treshhold) {
        data = ByteUtil.setBit(data, i, (byte) 1);
      }
    }
    return data;
  }
  
  private double getFrequencyMagnitude(int targetFrequency, int length) throws IOException {
    ComplexNumber c = goertzel(targetFrequency, length);
    return (c.getReal() * c.getReal() + c.getIma() * c.getIma());
  }
  
  private ComplexNumber goertzel(int targetFrequency, int length) throws IOException {
    double k = (((double) length * (double) targetFrequency) / (double) samplingrate);
    double omega =
        (2d * Math.PI * k) / (double) length;
    double sin = Math.sin(omega);
    double cos = Math.cos(omega);
    double a1 = 2.0 * cos;
    double d1 = 0;
    double d2 = 0;
    double d0;
    for (int i = 0; i < length; i++) {
      double sample = in.readSample();
      d0 = a1 * d1 - d2 + sample;
      d2 = d1;
      d1 = d0;
    }
    d0 = a1 * d1 - d2;
    d2 = d1;
    d1 = d0;
    ComplexNumber c = new ComplexNumber(d1 - d2 * cos, d2 * sin);
    return c;
  }

  public int getBitFrequency() {
    return config.getInt("mainfrequency") / config.getInt("periodsperbit");
  }

}
