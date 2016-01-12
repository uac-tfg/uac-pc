package de.mytfg.uac.wave.stream;

import java.io.IOException;

public abstract class InputWave {
  
  public abstract double readSample() throws IOException;
  
  public void readSample(double[] buffer) throws IOException {
    for(int i = 0; i < buffer.length; i++) {
      buffer[i] = readSample();
    }
  }
  
  public void skip(int length) throws IOException {
    for(int i = 0; i < length; i++) {
      readSample();
    }
  }

}
