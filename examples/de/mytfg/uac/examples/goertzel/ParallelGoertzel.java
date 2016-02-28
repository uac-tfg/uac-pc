package de.mytfg.uac.examples.goertzel;

import java.io.IOException;
import java.util.Arrays;

import de.mytfg.uac.wave.stream.GoertzelParrallelized;
import de.mytfg.uac.wave.stream.GoertzelManager;
import de.mytfg.uac.wave.stream.InputWaveDouble;
import de.mytfg.uac.wave.stream.InputWaveSine;

public class ParallelGoertzel {

  public static void main(String[] args) throws IOException {
    int samplingrate = 2500;
    int aF = 250;
    int bF = 900;
    InputWaveSine a = new InputWaveSine(aF, 0, samplingrate);
    InputWaveSine b = new InputWaveSine(bF, 0, samplingrate);

    int samplesPerBit = (samplingrate / aF) * 3;

    double[] samples = new double[samplesPerBit * 12];

    // trailing 0
    for (int i = 0; i < samplesPerBit * 2; i++) {
      samples[i] = 0;
    }
    // signal
    for (int i = 0; i < 8 / 2; i++) {
      a.reset();
      for (int j = 0; j < samplesPerBit; j++) {
        samples[samplesPerBit * (2 + i * 2) + j] = a.readSample();
      }
      b.reset();
      for (int j = 0; j < samplesPerBit; j++) {
        samples[samplesPerBit * (2 + i * 2 + 1) + j] = b.readSample();
//        samples[samplesPerBit * (2 + i * 2 + 1) + j] = 0;
      }
    }
    // 0 afterwards
    for (int i = 0; i < samplesPerBit * 2; i++) {
      samples[samplesPerBit * (2 + 8) + i] = 0;
    }

    Arrays.stream(samples).forEachOrdered((s) -> System.out.println(s));

    InputWaveDouble in = new InputWaveDouble(samples);
    GoertzelManager manager = new GoertzelManager(in, samplingrate, samplesPerBit);
    GoertzelParrallelized ag1 = new GoertzelParrallelized(aF, 0);
    GoertzelParrallelized ag2 = new GoertzelParrallelized(aF, samplesPerBit / 4);
    GoertzelParrallelized ag3 = new GoertzelParrallelized(aF, (samplesPerBit / 4) * 2);
    GoertzelParrallelized ag4 = new GoertzelParrallelized(aF, (samplesPerBit / 4) * 3);
    GoertzelParrallelized bg1 = new GoertzelParrallelized(bF, 0);
    GoertzelParrallelized bg2 = new GoertzelParrallelized(bF, samplesPerBit / 4);
    GoertzelParrallelized bg3 = new GoertzelParrallelized(bF, (samplesPerBit / 4) * 2);
    GoertzelParrallelized bg4 = new GoertzelParrallelized(bF, (samplesPerBit / 4) * 3);
    manager.add(ag1, ag2, ag3, ag4, bg1, bg2, bg3, bg4);
    
    for(int i = 0; i < samples.length; i++) {
      manager.processSample();
      if(i % samplesPerBit == 0) {
        System.out.println("==> " + (i / samplesPerBit));
        System.out.println("0.0: ");
        print(ag1, ag2, ag3, ag4, bg1, bg2, bg3, bg4);
      }
      if(i % samplesPerBit == samplesPerBit / 4) {
        System.out.println("0.25: ");
        print(ag1, ag2, ag3, ag4, bg1, bg2, bg3, bg4);
      }
      if(i % samplesPerBit == (samplesPerBit / 4) * 2) {
        System.out.println("0.5: ");
        print(ag1, ag2, ag3, ag4, bg1, bg2, bg3, bg4);
      }
      if(i % samplesPerBit == (samplesPerBit / 4) * 3) {
        System.out.println("0.75: ");
        print(ag1, ag2, ag3, ag4, bg1, bg2, bg3, bg4);
      }
    }
  }
  
  private static void print(GoertzelParrallelized... goertzels) {
    for(GoertzelParrallelized g : goertzels) {
      System.out.println(g.getFrequency() + " @ " + g.getOffset() + ": " + g.getMagnitude());
    }
  }

}
