package de.mytfg.uac.wave.stream;

import java.io.IOException;

public abstract class InputWave {
  
  public abstract double readSample() throws IOException;
  
  public void readSample(double[] buffer) throws IOException {
    for(int i = 0; i < buffer.length; i++) {
      buffer[i] = readSample();
    }
  }
  
  public void skip(long l) throws IOException {
    for(long i = 0; i < l; i++) {
      readSample();
    }
  }

}
