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

//  public Wave encode(byte[] data) throws IOException {
//    int samplingRate = config.getInt("samplingrate");
//    long numFrames =
//        (long) ((samplingRate / (double) waveDataGenerators[0][0].getBitFrequency()) * (data.length * 8));
//    WaveConfig waveConfig = new WaveConfig(16, samplingRate, numFrames);
//    Wave w = new Wave(File.createTempFile("signal", ".wav"), waveConfig);
//
//    Random hopRandom = new Random(hopRandomSeed);
//    int hop = 0;
//    int sentBits = 0;
//    long lastPos = -1;
//    double[] buffer = null;
//    int pointer = 0;
//    long lastPosFrame = 0;
//    for (long i = 0; i <= numFrames; i++, pointer++) {
//      if (buffer == null || pointer == buffer.length) {
//        if (buffer != null) {
//          w.setFrames(i - buffer.length, buffer);
//        }
//        if (i == numFrames) {
//          break;
//        }
//
//        int length = (int) Math.min(w.getNumFrames() - i, Wave.BUFFER_SIZE);
//        buffer = new double[length];
//        pointer = 0;
//      }
//
//      double currentPosDouble = (i / (double) samplingRate) * getBitFrequency();
//      long currentPos = (long) (currentPosDouble);
//      if (currentPos > lastPos) {
//        lastPos = currentPos;
//        lastPosFrame = i;
//
//        if (sentBits % config.getInt("fhss.bitsperhop") == 0) {
//          hop = hopRandom.nextInt(waveDataGenerators.length);
//        }
//
//        System.out.println("Bit #" + currentPos + " " + ByteUtil.getBit(data, currentPos)
//            + " on hop " + hop + " from " + i);
//
//        sentBits++;
//      }
//      byte b = ByteUtil.getBit(data, currentPos);
//      long relBitPos = i - lastPosFrame;
//      double val = waveDataGenerators[hop][0].generateSample(w, b, relBitPos, relBitPos, numFrames - i);
//      buffer[pointer] = val;
//    }
//
//    return w;
//  }

  public int getBitFrequency() {
    return config.getInt("mainfrequency") / config.getInt("periodsperbit");
  }

}
