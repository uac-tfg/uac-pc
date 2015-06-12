package de.mytfg.uac.wave.wav;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import de.mytfg.uac.wave.CombinedWave;
import de.mytfg.uac.wave.Wave;

public class LineHelper {

  private static final int BUFFER_SIZE = 128000;

  public static void play(File file) {
    AudioInputStream audioStream;
    try {
      audioStream = AudioSystem.getAudioInputStream(file);
    } catch (IOException | UnsupportedAudioFileException e) {
      throw new RuntimeException(
          "Unable to play audio file! Refer to inner exception for more information.", e);
    }

    AudioFormat audioFormat = audioStream.getFormat();
    DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
    SourceDataLine sourceLine = null;
    try {
      sourceLine = (SourceDataLine) AudioSystem.getLine(info);
      sourceLine.open(audioFormat);
    } catch (LineUnavailableException e) {
      throw new RuntimeException(
          "Unable to play audio file! Refer to inner exception for more information.", e);
    }

    sourceLine.start();

    int nBytesRead = 0;
    byte[] abData = new byte[BUFFER_SIZE];
    while (nBytesRead != -1) {
      try {
        nBytesRead = audioStream.read(abData, 0, abData.length);
      } catch (IOException e) {
        throw new RuntimeException(
            "Unable to play audio file! Refer to inner exception for more information.", e);
      }
      if (nBytesRead >= 0) {
        sourceLine.write(abData, 0, nBytesRead);
      }
    }

    sourceLine.drain();
    sourceLine.close();
  }

  public static void play(File file, int times) {
    for (int i = 0; i < times; i++) {
      play(file);
    }
  }
  
  public static void play(WavFile wavFile) {
    play(wavFile.getFile());
  }
  
  public static void play(Wave wave) {
    play(wave.getFile());
  }
  
  public static void play(CombinedWave combined) {
    Wave result = combined.combine();
    play(result);
    result.delete();
  }

  public static void capture(File file, final int duration, AudioFormat audioFormat) {
    try {
      // Obtain and open the line.
      DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);
      final TargetDataLine line;
      line = (TargetDataLine) AudioSystem.getLine(info);
      line.open(audioFormat);

      // open the output
      FileOutputStream out = new FileOutputStream(file);

      // start stopper
      Thread stopper = new Thread(new Runnable() {

        public void run() {
          try {
            Thread.sleep(duration);
          } catch (InterruptedException ex) {
            ex.printStackTrace();
          }
          line.stop();
        }
      });

      stopper.start();

      // begin recording
      line.start();
      AudioInputStream ais = new AudioInputStream(line);

      AudioSystem.write(ais, AudioFileFormat.Type.WAVE, file);

      // end recording
      line.close();
      out.close();
    } catch (LineUnavailableException | IOException e) {
      throw new RuntimeException(
          "Unable to capture audio file! Refer to inner exception for more information.", e);
    }
  }
}
