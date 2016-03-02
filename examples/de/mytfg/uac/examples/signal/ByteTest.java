package de.mytfg.uac.examples.signal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

import de.mytfg.uac.signal.SignalConfig;
import de.mytfg.uac.signal.SignalInputStream;
import de.mytfg.uac.signal.SignalOutputStream;
import de.mytfg.uac.util.ByteUtil;
import de.mytfg.uac.wave.stream.InputWaveRecorder;
import de.mytfg.uac.wave.stream.OutputWaveSpeaker;

public class ByteTest implements Runnable {
  
  private static final int DATA_LENGTH = 4;
  
  private SignalConfig config;
  private Thread thread;
  private Random random;
  
  private OutputWaveSpeaker speaker;
  private SignalOutputStream out;
  
  public ByteTest() {
    config = new SignalConfig();
    config.put("samplingrate", 2500);
    config.put("periodsperbit", 3);
    
//    config.put("modulation", "am");
//    config.put("mainfrequency", 250);
    config.put("threshold", 0.1d);
    
    config.put("modulation", "fm");
    config.put("frequency.high", 250);
    config.put("frequency.low", 350);
    
    thread = new Thread(this);
    thread.start();
    
    random = new Random();
    
    speaker = new OutputWaveSpeaker(config.getInt("samplingrate"));
    out = new SignalOutputStream(speaker, config);
  }

  @Override
  public void run() {
    InputWaveRecorder recorder = new InputWaveRecorder(config.getInt("samplingrate"));
    SignalInputStream in = new SignalInputStream(recorder, config);
    
    while(true) {
      try {
        in.synchronize();
        byte[] b = new byte[DATA_LENGTH];
        in.read(b);
        System.out.println(ByteUtil.toBitString(b));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
  
  public void send() throws IOException {
    byte[] data = new byte[DATA_LENGTH];
    random.nextBytes(data);
    System.out.println(ByteUtil.toBitString(data));
    out.synchronize();
    out.write(data);
  }
  
  public static void main(String[] args) throws IOException {
    ByteTest test = new ByteTest();
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    while(reader.readLine() == null || true) {
      test.send();
    }
  }

}
