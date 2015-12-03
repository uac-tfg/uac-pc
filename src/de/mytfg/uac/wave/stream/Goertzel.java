package de.mytfg.uac.wave.stream;

import java.io.IOException;

import de.mytfg.uac.util.ComplexNumber;


public class Goertzel {

  private InputWave in;
  private ComplexNumber complex;

  public Goertzel(InputWave in, int frequency) {
    this.in = in;
  }

  public void doBlock(int length, int targetFrequency, int samplingrate) throws IOException {
    double k = (((double) length * (double) targetFrequency) / (double) samplingrate);
    double omega = (2d * Math.PI * k) / (double) length;
    double sin = Math.sin(omega);
    double cos = Math.cos(omega);
    double a1 = 2.0 * cos;
    double d1 = 0;
    double d2 = 0;
    double d0;
    for (int i = 0; i < length; i++) {
      double sample = in.readSample();
      d0 = a1 * d1 - d2 + sample;
      d2 = d1;
      d1 = d0;
    }
    d0 = a1 * d1 - d2;
    d2 = d1;
    d1 = d0;
    complex = new ComplexNumber(d1 - d2 * cos, d2 * sin);
  }
  
  public double getMagnitude() {
    return (complex.getReal() * complex.getReal() + complex.getIma() * complex.getIma());
  }

}
