package de.mytfg.uac.wave.stream;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import de.mytfg.uac.util.SimpleAudioConversion;

public class InputWaveRecorder extends InputWave {

  private static final int BYTES = 3;

  private AudioInputStream in;
  private byte[] buffer;
  private float[] samples;
  private AudioFormat format;

  public InputWaveRecorder(int samplingRate) {
    AudioFormat format = new AudioFormat(samplingRate, BYTES * 8, 1, true, false);
    try {
      TargetDataLine line = AudioSystem.getTargetDataLine(format);
      in = new AudioInputStream(line);
//      in = AudioSystem.getAudioInputStream(new File("c.wav"));
    } catch (LineUnavailableException e) {
      throw new RuntimeException("Unable to open line for recording!", e);
    }
    format = in.getFormat();
    buffer = new byte[format.getSampleSizeInBits() / 8];
    samples = new float[1];
  }

  @Override
  public double readSample() throws IOException {
    int blen = 0;
    while(blen != buffer.length) {
      blen = in.read(buffer);
    }
    int slen = SimpleAudioConversion.unpack(buffer, samples, blen, in.getFormat());
    if(slen != 1) {
      throw new EOFException();
    }
    return samples[0];
  }

  public void close() throws IOException {
    in.close();
  }

}
