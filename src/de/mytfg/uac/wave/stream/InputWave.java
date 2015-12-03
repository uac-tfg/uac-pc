package de.mytfg.uac.wave.stream;

import java.io.IOException;

public abstract class InputWave {
  
  public abstract double readSample() throws IOException;
  
  public void reset() {
    
  }

}
