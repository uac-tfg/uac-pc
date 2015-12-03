package de.mytfg.uac.wave.stream;

import java.io.IOException;

public class InputWaveOversampling extends InputWave {
  
  private InputWave in;
  private int oversampling;
  
  public InputWaveOversampling(InputWave in, int oversampling) {
    this.in = in;
    this.oversampling = oversampling;
  }

  @Override
  public double readSample() throws IOException {
    double val = in.readSample();
    for(int i = 0; i < oversampling - 1; i++) {
      in.readSample();
    }
    return val;
  }
  
  public void skip(int frames) throws IOException {
    for(int i = 0; i < frames; i++) {
      in.readSample();
    }
  }

  public int getOversampling() {
    return oversampling;
  }

  public void setOversampling(int oversampling) {
    this.oversampling = oversampling;
  }

}
