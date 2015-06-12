package de.mytfg.uac.wave;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Holds several wave objects sharing the same config and folder.
 * 
 * @author Tilman Hoffbauer
 */
public class CombinedWave {

  private WaveConfig config;
  private File folder;
  private ArrayList<Wave> waves = new ArrayList<>();

  /**
   * Initializes a new CombinedWave object.
   * 
   * @param folder the folder to save the buffer files in
   * @param config the config shared by the waves
   */
  public CombinedWave(File folder, WaveConfig config) {
    if (!folder.exists()) {
      folder.mkdirs();
    }
    if (!folder.isDirectory()) {
      throw new IllegalArgumentException("Folder parameter isn't a directory!");
    }

    this.config = config;
    this.folder = folder;
  }

  /**
   * Creates a new wave and adds it to the list.
   * 
   * @return the new wave object
   */
  public Wave newWave() {
    File f = new File(folder.getAbsolutePath() + "/" + UUID.randomUUID().toString() + ".wav");
    Wave wave = new Wave(f, config);
    waves.add(wave);
    return wave;
  }

  /**
   * Removes a wave by index.
   * 
   * @param index
   * @return the wave object removed
   */
  public Wave removeWave(int index) {
    Wave old = waves.remove(index);
    if (old != null) {
      releaseWave(old);
    }
    return old;
  }

  /**
   * Removes a wave by its object.
   * 
   * @param o
   * @return whether there actually was a wave removed
   */
  public boolean removeWave(Object o) {
    boolean exist = waves.remove(o);
    if (exist) {
      releaseWave((Wave) o);
    }
    return exist;
  }

  /**
   * Clears the list of all waves and closes the files.
   */
  public void clear() {
    for (Wave w : waves) {
      releaseWave(w);
    }
    waves.clear();
  }

  private static void releaseWave(Wave w) {
    w.close();
    w.getFile().delete();
  }
  
  public ArrayList<Wave> getWaves() {
    return waves;
  }

  public WaveConfig getConfig() {
    return config;
  }

}
