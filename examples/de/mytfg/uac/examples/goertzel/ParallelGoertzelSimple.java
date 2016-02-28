package de.mytfg.uac.examples.goertzel;

import java.io.IOException;

import de.mytfg.uac.wave.stream.GoertzelParrallelized;
import de.mytfg.uac.wave.stream.GoertzelManager;
import de.mytfg.uac.wave.stream.Goertzel;
import de.mytfg.uac.wave.stream.InputWaveSine;

public class ParallelGoertzelSimple {
  
  public static void main(String[] args) throws IOException {
    int samplingrate = 1000;
    int frequency = 100;
    InputWaveSine sine = new InputWaveSine(frequency, 0, samplingrate);
    
    int length = (samplingrate / frequency) * 3;
    
    
    for(int i = 100; i < 110; i += 10) {
      sine.reset();
      GoertzelManager manager = new GoertzelManager(sine, samplingrate, length);
      GoertzelParrallelized g = new GoertzelParrallelized(i, 0);
      manager.add(g);
      for(int j = 0; j < length; j++) {
        manager.processSample();
      }
      System.out.print(i + " | " + g.getMagnitude());
      
      sine.reset();
      Goertzel o = new Goertzel(sine, samplingrate);
      o.doBlock(length, i);
      System.out.println(" | " + o.getMagnitude());
    }
  }

}
