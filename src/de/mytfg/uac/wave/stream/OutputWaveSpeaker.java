package de.mytfg.uac.wave.stream;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import de.mytfg.uac.util.SimpleAudioConversion;

public class OutputWaveSpeaker extends OutputWave {

  private static final int BYTES = 2;
  
  private SourceDataLine line;
  private byte[] buffer;
  private float[] samples;
  
  public OutputWaveSpeaker(int samplingRate) {
    AudioFormat format = new AudioFormat(samplingRate, BYTES * 8, 1, true, false);
    try {
      line = AudioSystem.getSourceDataLine(format);
      line.open();
    } catch (LineUnavailableException e) {
      e.printStackTrace();
    }
    buffer = new byte[format.getSampleSizeInBits() / 8];
    samples = new float[1];
  }

  @Override
  public void writeSample(double sample) throws IOException {
    if(!line.isActive()) {
      line.start();
    }
    samples[0] = (float) sample;
    SimpleAudioConversion.pack(samples, buffer, samples.length, line.getFormat());
    line.write(buffer, 0, buffer.length);
  }
  
  public void close() {
    line.close();
  }

}
