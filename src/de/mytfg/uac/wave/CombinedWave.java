package de.mytfg.uac.wave;

import java.util.ArrayList;

public class CombinedWave {

  private WaveConfig config;
  private ArrayList<Wave> waves = new ArrayList<>();

  public CombinedWave(WaveConfig config) {
    this.config = config;
  }

  public CombinedWave(long numFrames) {
    this.config = WaveConfig.createDefaultWaveConfig(numFrames);
  }

  public boolean addWave(Wave e) {
    return waves.add(e);
  }

  public Wave removeWave(int index) {
    Wave old = waves.remove(index);
    if(old != null) {
      old.close();
    }
    return old;
  }

  public boolean removeWave(Object o) {
    boolean exist = waves.remove(o);
    if(exist) {
      ((Wave) o).close();
    }
    return exist;
  }

  public void clear() {
    for(Wave w : waves) {
      w.close();
    }
    waves.clear();
  }

  public ArrayList<Wave> getWaves() {
    return waves;
  }

}
