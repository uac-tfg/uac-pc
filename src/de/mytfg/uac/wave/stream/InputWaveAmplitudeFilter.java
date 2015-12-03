package de.mytfg.uac.wave.stream;

import java.io.IOException;

public class InputWaveAmplitudeFilter extends InputWave {
  
  private InputWave in;
  private double min;
  
  public InputWaveAmplitudeFilter(InputWave in, double min) {
    this.in = in;
    this.min = min;
  }

  @Override
  public double readSample() throws IOException {
    while(true) {
      double val = in.readSample();
      if(val >= min) {
        return val;
      }
    }
  }

  public double getMin() {
    return min;
  }

  public void setMin(double min) {
    this.min = min;
  }

  public InputWave getInputWave() {
    return in;
  }

}
