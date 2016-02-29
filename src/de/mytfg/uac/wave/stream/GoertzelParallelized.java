package de.mytfg.uac.wave.stream;

import de.mytfg.uac.util.ComplexNumber;

public class GoertzelParallelized {
  
  private int frequency;
  private int offset;
  private double sin;
  private double cos;
  private double a;
  
  private double d0;
  private double d1;
  private double d2;
  
  private boolean enabled = true;
  
  private ComplexNumber complex;

  public GoertzelParallelized(int frequency, int offset) {
    this.frequency = frequency;
    this.offset = offset;
  }
  
  public void init(double sin, double cos) {
    this.sin = sin;
    this.cos = cos;
    this.a = 2.0 * cos;
    this.d0 = 0;
    this.d1 = 0;
    this.d2 = 0;
  }
  
  public void processSample(double sample) {
    if(complex == null) {
      return;
    }
    d0 = a * d1 - d2 + sample;
    d2 = d1;
    d1 = d0;
  }
  
  public void newBlock() {
    d0 = a * d1 - d2;
    d2 = d1;
    d1 = d0;
    complex = new ComplexNumber(d1 - d2 * cos, d2 * sin);
    d0 = 0;
    d1 = 0;
    d2 = 0;
  }
  
  public double getMagnitude() {
    if(complex == null) {
      return -1;
    }
    return (complex.getReal() * complex.getReal() + complex.getIma() * complex.getIma());
  }

  public int getFrequency() {
    return frequency;
  }

  public void setFrequency(int frequency) {
    this.frequency = frequency;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
    if(!enabled) {
      complex = null;
    }
  }

  public int getOffset() {
    return offset;
  }

  public void setOffset(int offset) {
    this.offset = offset;
  }

}
