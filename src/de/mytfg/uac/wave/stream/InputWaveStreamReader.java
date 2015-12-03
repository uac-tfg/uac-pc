package de.mytfg.uac.wave.stream;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

import de.mytfg.uac.util.ByteUtil;

public class InputWaveStreamReader extends InputWave {

  private InputStream in;

  public InputWaveStreamReader(InputStream in) {
    this.in = in;
  }
  
  public double readSample() throws IOException {
    byte[] read = new byte[8];
    if(in.read(read) != read.length) {
      throw new EOFException();
    };
    double val = ByteUtil.toDouble(read);
    return val;
  }

}
