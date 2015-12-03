package de.mytfg.uac.signal;

import java.io.IOException;
import java.io.OutputStream;

import de.mytfg.uac.util.ByteUtil;
import de.mytfg.uac.wave.stream.InputWaveSine;
import de.mytfg.uac.wave.stream.OutputWave;

public class SignalOutputStream extends OutputStream {

  private SignalConfig config;
  private OutputWave out;

  private InputWaveSine[][] waveDataGenerators;
  private int samplesPerBit;

  public SignalOutputStream(OutputWave out, SignalConfig config) {
    this.out = out;
    this.config = config;

    int samplingrate = config.getInt("samplingrate");
    int fhssCount = config.getInt("fhss.count");
    int fhssWidth = config.getInt("fhss.width");
    int dsssCount = config.getInt("dsss.count");
    int dsssWidth = config.getInt("dsss.width");
    int mainFrequency = config.getInt("mainfrequency");
    int lowestFrequency = mainFrequency - (fhssCount * fhssWidth + dsssCount * dsssWidth) / 2;
//    int bitFrequency = lowestFrequency / config.getInt("periodsperbit");
    waveDataGenerators = new InputWaveSine[fhssCount][dsssCount];
    for (int i = 0; i < fhssCount; i++) {
      for (int j = 0; j < dsssCount; j++) {
        int frequency = fhssWidth * i + dsssWidth * j + lowestFrequency;
        waveDataGenerators[i][j] = new InputWaveSine(frequency, 0, samplingrate);
      }
    }
    samplesPerBit = samplingrate / getBitFrequency();
  }

  @Override
  public void write(int ib) throws IOException {
    byte b = (byte) ib;
    int frequency = config.getInt("mainfrequency");
    int samplerate = config.getInt("samplingrate");
    for(int i = 0; i < 8; i++) {
      byte bit = ByteUtil.getBit(b, i);
      if(bit == 1) {
        InputWaveSine sine = waveDataGenerators[0][0];
        sine.reset();
        for(int j = 0; j < samplesPerBit; j++) {
          out.writeSample(sine.readSample());
        }
      } else {
        for(int j = 0; j < samplesPerBit; j++) {
          out.writeSample(0);
        }
      }
    }
  }

  public int getBitFrequency() {
    return config.getInt("mainfrequency") / config.getInt("periodsperbit");
  }

}
