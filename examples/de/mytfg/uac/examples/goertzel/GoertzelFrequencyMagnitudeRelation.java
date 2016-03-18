package de.mytfg.uac.examples.goertzel;

import java.io.IOException;

import de.mytfg.uac.wave.stream.Goertzel;
import de.mytfg.uac.wave.stream.InputWaveSine;

public class GoertzelFrequencyMagnitudeRelation {

  public static void main(String[] args) throws IOException {
    
    int samplingrate = 2500;
    int length = 30;
    
    for(int i = 100; i <= 1000; i += 100) {
      InputWaveSine sine = new InputWaveSine(i, 0, samplingrate);
      Goertzel g = new Goertzel(sine, samplingrate);
      g.doBlock(length, i);
      double mag = g.getMagnitude();
      System.out.println(i + "\t" + mag);
    }
    
  }
  
}
