package de.mytfg.uac.wave.stream;

import java.io.IOException;

public class InputWaveSine extends InputWave {

  private final int frequency;
  private double phase;
  private final int samplingrate;
  
  private long i = 0;
  
  public InputWaveSine(int frequency, double phase, int samplingrate) {
    this.frequency = frequency;
    this.phase = phase;
    this.samplingrate = samplingrate;
  }
  
  @Override
  public double readSample() throws IOException {
    double period = frequency * 2 * Math.PI;
    double time = i * (1d / samplingrate);
    double shift = 2 * Math.PI * phase;
    double val = Math.sin(period * time + shift);
    i++;
    return val;
  }
  
  public void reset() {
    i = 0;
  }

  public double getPhase() {
    return phase;
  }

  public void setPhase(double phase) {
    this.phase = phase;
  }

  public int getFrequency() {
    return frequency;
  }

  public int getSamplingrate() {
    return samplingrate;
  }

}
