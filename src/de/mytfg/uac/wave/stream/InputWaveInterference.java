package de.mytfg.uac.wave.stream;

import java.io.IOException;

public class InputWaveInterference extends InputWave {
  
  private InputWave[] waves;
  private double[] scales;
  
  public InputWaveInterference(InputWave waveA, double scaleA, InputWave waveB, double scaleB) {
    this(new InputWave[] {waveA, waveB}, new double[] {scaleA, scaleB});
  }
  
  public InputWaveInterference(InputWave[] waves, double[] scales) {
    this.waves = waves;
    this.scales = scales;
  }

  @Override
  public double readSample() throws IOException {
    double val = 0;
    for(int i = 0; i < waves.length; i++) {
      val += scales[i] * waves[i].readSample();
    }
    return val;
  }

  public double getScale(int i) {
    return scales[i];
  }

  public void setScale(int i, double scale) {
    this.scales[i] = scale;
  }

  public InputWave getWave(int i) {
    return waves[i];
  }

}
