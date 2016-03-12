package de.mytfg.uac.examples.teensy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

import de.mytfg.uac.signal.SignalConfig;
import de.mytfg.uac.signal.SignalOutputStream;
import de.mytfg.uac.util.ByteUtil;
import de.mytfg.uac.wave.stream.OutputWaveSpeaker;

public class TeensyCharactersTest {

  public static void main(String[] args) throws IOException {
    SignalConfig config = new SignalConfig();
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
    
    OutputWaveSpeaker speaker = new OutputWaveSpeaker(config.getInt("samplingrate"));
    SignalOutputStream out = new SignalOutputStream(speaker, config);
    
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    while(true) {
      String line = in.readLine();
      if(line.equals("/stop")) {
        break;
      }
      out.synchronize();
      out.write((byte) line.length());
      for(int i = 0; i < line.length(); i++) {
        char c = line.charAt(i);
        byte b = (byte) c;
        out.write(b);
      }
    }
    
    in.close();
    out.close();
  }
  
}
