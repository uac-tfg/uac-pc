package de.mytfg.uac.signal;

import java.io.IOException;
import java.io.OutputStream;

import de.mytfg.uac.util.AverageTimer;
import de.mytfg.uac.util.ByteUtil;
import de.mytfg.uac.wave.stream.InputWaveSine;
import de.mytfg.uac.wave.stream.OutputWave;

public class SignalOutputStream extends OutputStream {

  private SignalConfig config;
  private int samplesPerBit;
  private String modulation;
  private int bitFrequency;

  private OutputWave out;
  private InputWaveSine[] sines;

  public SignalOutputStream(OutputWave out, SignalConfig config) {
    this.out = out;
    this.config = config;
    int samplingrate = config.getInt("samplingrate");
    modulation = config.getString("modulation");
    int periodsPerBit = config.getInt("periodsperbit");

    if (modulation.equals("am")) {
      int frequency = config.getInt("mainfrequency");
      sines = new InputWaveSine[] {new InputWaveSine(frequency, 0, samplingrate)};
      bitFrequency = frequency / periodsPerBit;
      samplesPerBit = samplingrate / bitFrequency;
    } else if (modulation.equals("fm")) {
      int high = config.getInt("frequency.high");
      int low = config.getInt("frequency.low");
      sines = new InputWaveSine[] {
        new InputWaveSine(high, 0, samplingrate),  
        new InputWaveSine(low, 0, samplingrate)
      };
      bitFrequency = Math.min(high, low) / periodsPerBit;
      samplesPerBit = samplingrate / bitFrequency;
    } else {
      throw new IllegalArgumentException("Unknown modulation type!");
    }
  }

  @Override
  public void write(int ib) throws IOException {
    AverageTimer.getTimer("SignalOutputStream").begin();
    byte b = (byte) ib;
    for (int i = 0; i < 8; i++) {
      byte bit = ByteUtil.getBit(b, i);
      if(modulation.equals("am")) {
        if (bit == 1) {
          sendSine(sines[0]);
        } else {
          for (int j = 0; j < samplesPerBit; j++) {
            out.writeSample(0);
          }
        }
      } else if(modulation.equals("fm")) {
        if (bit == 1) {
          sendSine(sines[0]);
        } else {
          sendSine(sines[1]);
        }
      }
      
    }
    AverageTimer.getTimer("SignalOutputStream").end();
  }
  
  private void sendSine(InputWaveSine in) throws IOException {
    in.reset();
    for (int i = 0; i < samplesPerBit; i++) {
      double val = in.readSample();
      out.writeSample(val);
    }
  }

  public void synchronize() throws IOException {
    write(ByteUtil.toByteArray("10101010"));
  }

}
