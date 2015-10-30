package de.mytfg.uac.wave.stream;

import java.io.EOFException;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import de.mytfg.uac.util.ByteUtil;

public class InputWaveRecorder extends InputWave {
  
  private TargetDataLine line;
  private byte[] buffer;
  
  public InputWaveRecorder(int samplingRate) {
    AudioFormat format = new AudioFormat(samplingRate, Double.BYTES * 8, 1, true, false);
    try {
      line = AudioSystem.getTargetDataLine(format);
      line.open();
    } catch (LineUnavailableException e) {
      throw new RuntimeException("Unable to open line for recording!", e);
    }
    buffer = new byte[Double.BYTES];
  }

  @Override
  public double readSample() throws IOException {
    if(!line.isOpen()) {
      line.start();
    }
    int read = line.read(buffer, 0, buffer.length);
    if(read != buffer.length) {
      throw new EOFException();
    }
    return ByteUtil.toDouble(buffer);
  }
  
  public void close() {
    line.close();
  }
  
}
