package de.mytfg.uac.wave;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

public class CombinedWave {

  private WaveConfig config;
  private File folder;
  private ArrayList<Wave> waves = new ArrayList<>();

  public CombinedWave(File folder, WaveConfig config) {
    if (!folder.exists()) {
      folder.mkdirs();
    }
    if(!folder.isDirectory()) {
      throw new IllegalArgumentException("Folder parameter isn't a directory!");
    }
    
    this.config = config;
    this.folder = folder;
  }

  public Wave newWave() {
    File f = new File(folder.getAbsolutePath() + "/" + UUID.randomUUID().toString() + ".wav");
    Wave wave = new Wave(f, config);
    waves.add(wave);
    return wave;
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
  
  public WaveConfig getConfig() {
    return config;
  }

}
