package de.mytfg.uac.wave.stream;

import java.io.IOException;

public abstract class OutputWave {
  
  public abstract void writeSample(double sample) throws IOException;

}
