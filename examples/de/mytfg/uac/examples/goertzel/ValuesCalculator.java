package de.mytfg.uac.examples.goertzel;

public class ValuesCalculator {
  
  public static void main(String[] args) {
    int samplingrate = 2500;
    int frequency = 350;
    
    double k = frequency / (double) samplingrate;
    double omega = 2d * Math.PI * k;
    double sin = Math.sin(omega);
    double cos = Math.cos(omega);
    double a = 2.0 * cos;
    
    System.out.println(sin);
    System.out.println(cos);
    System.out.println(a);
  }

}
