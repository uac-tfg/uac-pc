package de.mytfg.uac.util;

import java.util.HashMap;

public class AverageTimer {
  
  private static HashMap<String, AverageTimer> timers = new HashMap<>();
  
  public static AverageTimer getTimer(String name) {
    AverageTimer timer = timers.get(name);
    if(timer == null) {
      timer = new AverageTimer(name);
      timers.put(name, timer);
    }
    return timer;
  }
  
  public static String list() {
    StringBuilder sb = new StringBuilder();
    for(AverageTimer timer : timers.values()) {
      sb.append(timer.getName() + ": " + timer.getAverage());
      sb.append('\n');
    }
    return sb.toString();
  }
  
  public static void clear() {
    timers.clear();
  }
  
  private String name;
  private int sum = 0;
  private int count = 0;
  private long then = 0;
  
  protected AverageTimer(String name) {
    this.name = name;
  }
  
  public void begin() {
    then = System.currentTimeMillis();
  }
  
  public void end() {
    long now = System.currentTimeMillis();
    sum += now - then;
    count++;
  }
  
  public double getAverage() {
    return sum / (double) count;
  }
  
  public String getName() {
    return name;
  }

}
