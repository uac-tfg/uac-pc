package de.mytfg.uac.wave.stream;

import java.io.IOException;
import java.util.Random;

public class InputWaveRandomNoise extends InputWave {
  
  private InputWave in;
  
  private double multiplier;
  private Random random;
  
  public InputWaveRandomNoise(InputWave in, double multiplier, long seed) {
    this.in = in;
    
    this.multiplier = multiplier;
    if(multiplier > 0) {
      this.random = seed == 0 ? new Random() : new Random(seed);
    }
  }

  @Override
  public double readSample() throws IOException {
    double val = in.readSample();
    if(multiplier > 0) {
      System.out.print(val + " => ");
      val = (val + random.nextGaussian() * multiplier) / (multiplier + 1);
      System.out.println(val);
    }
    return 0;
  }

}
