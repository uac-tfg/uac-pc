package de.mytfg.uac.examples.signal;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import de.mytfg.uac.signal.SignalConfig;
import de.mytfg.uac.signal.SignalInputStream;
import de.mytfg.uac.signal.SignalOutputStream;
import de.mytfg.uac.util.ByteUtil;
import de.mytfg.uac.wave.stream.InputWaveRecorder;
import de.mytfg.uac.wave.stream.OutputWaveSpeaker;

public class LongTest {
  
  private static byte[] received = null;
  
  private static final int TEST_DATA_LENGTH = 4;

  private SignalConfig config;
  
  private int bitsCounter = 0;
  private int errorHighCounter = 0;
  private int errorLowCounter = 0;
  private int errorFailCounter = 0;

  public LongTest() {
    config = new SignalConfig();
    config.put("samplingrate", 2500);
    config.put("periodsperbit", 4);
    
//    config.put("modulation", "am");
//    config.put("mainfrequency", 250);
//    config.put("threshold", 100d);
    
    config.put("modulation", "fm");
    config.put("frequency.high", 350);
    config.put("frequency.low", 250);
    config.put("syncbits", "10011001110011010110000111001100");
//    config.put("syncbits", "11110000111100001111000011110000");
  }
  
  public void test(int count) {
    for(int i = 0; i < count; i++) {
      System.out.println(i);
      test();
      System.out.println();
    }
  }
  
  public void test() {
    received = new byte[TEST_DATA_LENGTH];
    
    int samplingrate = config.getInt("samplingrate");
    OutputWaveSpeaker speaker = new OutputWaveSpeaker(samplingrate);
    SignalOutputStream out = new SignalOutputStream(speaker, config);
    
    final byte[] data = new byte[TEST_DATA_LENGTH];
    new Random().nextBytes(data);

    String dataString = ByteUtil.toBitString(data);
    System.out.println(dataString);
    
    Thread receiver = new Thread("Receiver") {
      @Override
      public void run() {
        try {
          InputWaveRecorder recorder = new InputWaveRecorder(samplingrate);
          SignalInputStream in = new SignalInputStream(recorder, config);
          in.synchronize();
          in.read(received);
          in.close();
          recorder.close();
        } catch (IOException e) {
          System.out.println("Receiver exception: " + e.getMessage() + " (" + e.getClass().getSimpleName() + ")");
          Arrays.fill(received, (byte) 0);
        }
      }
    };
    try {
      receiver.start();
      Thread.sleep(100);
      out.synchronize();
      out.write(data);
      Thread.sleep(1000);
      receiver.stop();
      out.close();
      speaker.close();
    } catch(IOException | InterruptedException e) {
      errorFailCounter++;
      return;
    }
    String receivedString = ByteUtil.toBitString(received);
    System.out.println(receivedString);
    if(receivedString.indexOf('1') == -1) {
      errorFailCounter++;
      return;
    }
    bitsCounter += TEST_DATA_LENGTH * 8;
    for(int i = 0; i < dataString.length(); i++) {
      char d = dataString.charAt(i);
      if(d != receivedString.charAt(i)) {
        if(d == '1') {
          errorHighCounter++;
        } else {
          errorLowCounter++;
        }
      }
    }
  }
  
  public static void main(String[] args) {
//    try {
//      Thread.sleep(1000 * 60 * 3);
//    } catch (InterruptedException e) {
//      e.printStackTrace();
//    }
    LongTest test = new LongTest();
    int count = 100;
    long then = System.currentTimeMillis();
    test.test(count);
    long time = System.currentTimeMillis() - then;
    System.out.println("Time: " + time + "ms");
    System.out.println("Sent bits count:     " + test.getBitsCounter());
    System.out.println("Errors on high bits: " + test.getErrorHighCounter() + " " + test.getErrorHighCounter() / (double) test.getBitsCounter());
    System.out.println("Errors on low bits:  " + test.getErrorLowCounter() + " " + test.getErrorLowCounter() / (double) test.getBitsCounter());
    System.out.println("Errors in total:     " + test.getErrorCounter() + " " + test.getErrorCounter() / (double) test.getBitsCounter());
    System.out.println("Fail counter:        " + test.getErrorFailCounter() + " " + test.getErrorFailCounter() / (double) count);
  }

  public int getBitsCounter() {
    return bitsCounter;
  }

  public int getErrorHighCounter() {
    return errorHighCounter;
  }

  public int getErrorLowCounter() {
    return errorLowCounter;
  }

  public int getErrorFailCounter() {
    return errorFailCounter;
  }
  
  public int getErrorCounter() {
    return errorLowCounter + errorHighCounter;
  }
  
}
