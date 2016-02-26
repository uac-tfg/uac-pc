package de.mytfg.uac.signal;

import java.io.IOException;
import java.io.OutputStream;

import de.mytfg.uac.util.AverageTimer;
import de.mytfg.uac.util.ByteUtil;
import de.mytfg.uac.wave.stream.InputWaveSine;
import de.mytfg.uac.wave.stream.OutputWave;

public class SignalOutputStream extends OutputStream {

  private SignalConfig config;
  private OutputWave out;

  private InputWaveSine sine;
  private int samplesPerBit;

  public SignalOutputStream(OutputWave out, SignalConfig config) {
    this.out = out;
    this.config = config;

    int samplingrate = config.getInt("samplingrate");
    int mainFrequency = config.getInt("mainfrequency");
    sine = new InputWaveSine(mainFrequency, 0, samplingrate);
    samplesPerBit = samplingrate / getBitFrequency();
  }

  @Override
  public void write(int ib) throws IOException {
    AverageTimer.getTimer("SignalOutputStream").begin();
    byte b = (byte) ib;
    for(int i = 0; i < 8; i++) {
      byte bit = ByteUtil.getBit(b, i);
      if(bit == 1) {
        sine.reset();
        for(int j = 0; j < samplesPerBit; j++) {
          double val = sine.readSample();
          out.writeSample(val);
        }
      } else {
        for(int j = 0; j < samplesPerBit; j++) {
          out.writeSample(0);
        }
      }
    }
    AverageTimer.getTimer("SignalOutputStream").end();
  }
  
  public void synchronize() throws IOException {
    write(ByteUtil.toByteArray("10101010"));
  }

  public int getBitFrequency() {
    return config.getInt("mainfrequency") / config.getInt("periodsperbit");
  }

}
