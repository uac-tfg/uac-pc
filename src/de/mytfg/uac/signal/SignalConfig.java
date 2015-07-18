package de.mytfg.uac.signal;

import java.util.HashMap;

public class SignalConfig {
  
  private HashMap<String, Object> values = new HashMap<>();

  public Object get(String key) {
    return values.get(key.toLowerCase());
  }
  
  public int getInt(String key) {
    return (int) values.get(key);
  }
  
  public double getDouble(String key) {
    return (double) values.get(key);
  }
  
  public long getLong(String key) {
    return (long) values.get(key);
  }
  
  public String getString(String key) {
    return (String) values.get(key);
  }

  public Object put(String key, Object value) {
    return values.put(key.toLowerCase(), value);
  }

}
