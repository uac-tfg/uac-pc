package de.mytfg.uac.examples.signal;

import java.io.DataOutputStream;
import java.io.IOException;

import de.mytfg.uac.signal.SignalConfig;
import de.mytfg.uac.signal.SignalOutputStream;
import de.mytfg.uac.wave.stream.OutputWaveSpeaker;

public class SimpleSend {
  public static void main(String[] args) {
    SignalConfig config = new SignalConfig();
    config.put("samplingrate", 2500);
    config.put("periodsperbit", 4);
    config.put("modulation", "fm");
    config.put("frequency.high", 350);
    config.put("frequency.low", 250);
    config.put("syncbits", "10011001110011010110000111001100");
    try {
      OutputWaveSpeaker speaker = new OutputWaveSpeaker(config.getInt("samplingrate"));
      SignalOutputStream signalOut = new SignalOutputStream(speaker, config);
      DataOutputStream out = new DataOutputStream(signalOut);
      signalOut.synchronize();
      out.writeUTF("Hello World!");
      out.close();
      speaker.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
