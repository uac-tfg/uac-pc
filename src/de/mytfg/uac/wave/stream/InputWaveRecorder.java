package de.mytfg.uac.wave.stream;

import java.io.EOFException;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import de.mytfg.uac.util.SimpleAudioConversion;

public class InputWaveRecorder extends InputWave {

  private static final int BYTES = 2;

  private TargetDataLine line;
  private byte[] buffer;
  private float[] samples;

  public InputWaveRecorder(int samplingRate) {
    AudioFormat format = new AudioFormat(samplingRate, BYTES * 8, 1, true, false);
    try {
      line = AudioSystem.getTargetDataLine(format);
      line.open();
    } catch (LineUnavailableException e) {
      throw new RuntimeException("Unable to open line for recording!", e);
    }
    format = line.getFormat();
    buffer = new byte[format.getSampleSizeInBits() / 8];
    samples = new float[1];
  }

  @Override
  public double readSample() throws IOException {
    if(!line.isActive()) {
      line.start();
    }
    
    int blen = 0;
    while(blen != buffer.length) {
      blen = line.read(buffer, 0, buffer.length);
    }
    int slen = SimpleAudioConversion.unpack(buffer, samples, blen, line.getFormat());
    if(slen != 1) {
      throw new EOFException();
    }
    return samples[0];
  }

  public void close() throws IOException {
    line.close();
  }

}
