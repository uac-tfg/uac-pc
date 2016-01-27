package de.mytfg.uac.wave.stream;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import de.mytfg.uac.util.AverageTimer;
import de.mytfg.uac.util.SimpleAudioConversion;

public class OutputWaveSpeaker extends OutputWave {

  private static final int BYTES = 2;
  private static final int BUFFER_SIZE = 128;
  
  private SourceDataLine line;
  private byte[] buffer;
  private float[] samples;
  int pointer = 0;
  
  public OutputWaveSpeaker(int samplingRate) {
    AudioFormat format = new AudioFormat(samplingRate, BYTES * 8, 1, true, false);
    try {
      line = AudioSystem.getSourceDataLine(format);
      line.open();
    } catch (LineUnavailableException e) {
      e.printStackTrace();
    }
    buffer = new byte[(format.getSampleSizeInBits() / 8) * BUFFER_SIZE];
    samples = new float[BUFFER_SIZE];
  }

  @Override
  public void writeSample(double sample) throws IOException {
    AverageTimer.getTimer("Speaker").begin();
    samples[pointer] = (float) sample;
    pointer++;
    if(pointer == BUFFER_SIZE) {
      flush();
    }
    AverageTimer.getTimer("Speaker").end();
  }
  
  public void flush() {
    if(!line.isActive()) {
      line.start();
    }
    AverageTimer.getTimer("Conversion").begin();
    SimpleAudioConversion.pack(samples, buffer, samples.length, line.getFormat());
    AverageTimer.getTimer("Conversion").end();
    line.write(buffer, 0, buffer.length);
    pointer = 0;
  }
  
  public void close() {
    line.close();
  }

}
