package de.mytfg.uac.wave.stream;

import java.io.IOException;
import java.io.OutputStream;

import de.mytfg.uac.util.ByteUtil;

public class OutputWaveStreamWriter extends OutputWave {
  
  private OutputStream out;

  public OutputWaveStreamWriter(OutputStream out) {
    this.out = out;
  }
  
  public void writeSample(double sample) throws IOException {
    out.write(ByteUtil.toByteArray(sample));
  }

}
