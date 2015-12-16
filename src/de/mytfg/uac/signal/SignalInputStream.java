package de.mytfg.uac.signal;

import java.io.IOException;
import java.io.InputStream;

import de.mytfg.uac.util.ByteUtil;
import de.mytfg.uac.util.ComplexNumber;
import de.mytfg.uac.wave.stream.Goertzel;
import de.mytfg.uac.wave.stream.InputWave;

public class SignalInputStream extends InputStream {
  
  private InputWave in;
  private SignalConfig config;
  private Goertzel goertzel;
  
  private long hopRandomSeed;
  private int samplesPerBit;
  private int samplingrate;
  private double treshhold;

  public SignalInputStream(InputWave in, SignalConfig config) {
    this.in = in;
    this.config = config;
    samplingrate = config.getInt("samplingrate");
    treshhold = config.getDouble("treshhold");
    samplesPerBit = samplingrate / getBitFrequency();
    hopRandomSeed = config.getLong("fhss.seed");
    goertzel = new Goertzel(in, samplingrate);
  }

  @Override
  public int read() throws IOException {
    byte data = 0;
    int frequency = config.getInt("mainfrequency");
    for(int i = 0; i < 8; i++) {
//      goertzel.doBlock(samplesPerBit, config.getInt("mainfrequency"));
//      double magnitude = goertzel.getMagnitude();
      boolean symbol = readSymbol(frequency);
      if (symbol) {
        data = ByteUtil.setBit(data, i, (byte) 1);
      }
    }
    return data;
  }
  
  public void waitFor(byte b) throws IOException {
//    int pos = 0;
//    double treshhold = config.getDouble("treshhold");
//    while(true) {
//      int frequency = config.getInt("mainfrequency");
//      goertzel.doBlock(samplesPerBit, frequency);
//      double magnitude = goertzel.getMagnitude();
////      goertzel.doBlock(samplesPerBit / 2, frequency);
////      double mag1 = goertzel.getMagnitude();
////      goertzel.doBlock(samplesPerBit / 2 + (samplesPerBit % 2 == 1 ? 1 : 0), frequency);
////      double mag2 = goertzel.getMagnitude();
////      double magnitude = (mag1 + mag2) / 2;
//      if(magnitude > treshhold && ByteUtil.getBit(b, pos) == 1) {
//        pos++;
////        System.out.println(pos + " " + ByteUtil.getBit(b, pos) + " " + mag1 + "\t" + mag2 + "\t" + (mag1 - mag2));
//      } else if(magnitude < treshhold && ByteUtil.getBit(b, pos) == 0) {
//        pos++;
////        System.out.println(pos + " " + ByteUtil.getBit(b, pos) + " " + mag1 + "\t" + mag2 + "\t" + (mag1 - mag2));
//      } else {
//        pos = 0;
//      }
//      if(pos == 8) {
//        break;
//      }
//    }
    int pos = 0;
    while(true) {
      int frequency = config.getInt("mainfrequency");
      boolean symbol = readSymbol(frequency);
      if(symbol && ByteUtil.getBit(b, pos) == 1) {
        pos++;
      } else if(!symbol && ByteUtil.getBit(b, pos) == 0) {
        pos++;
      } else {
        pos = 0;
      }
      if(pos == 8) {
        break;
      }
    }
  }
  
  public boolean readSymbol(int frequency) throws IOException {
    goertzel.doBlock(samplesPerBit, frequency);
    double magnitude = goertzel.getMagnitude();
    return magnitude > treshhold;
  }

  public int getBitFrequency() {
    return config.getInt("mainfrequency") / config.getInt("periodsperbit");
  }

}
