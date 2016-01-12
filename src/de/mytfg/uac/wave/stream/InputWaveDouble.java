package de.mytfg.uac.wave.stream;

public class InputWaveDouble extends InputWave {
  
  private double[] samples;
  private int i = 0;
  
  public InputWaveDouble(double[] samples) {
    super();
    this.samples = samples;
  }

  @Override
  public double readSample() {
    double val = samples[i];
    i++;
    return val;
  }
  
  public void reset() {
    i = 0;
  }
  
  public int getIndex() {
    return i;
  }

}
