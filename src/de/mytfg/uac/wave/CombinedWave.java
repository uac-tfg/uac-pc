package de.mytfg.uac.wave;

import java.io.File;
import java.io.IOException;
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
   * Add an existing wave
   * @param w
   */
  public void addWave(Wave w) {
    if(!config.equals(w.getConfig())) {
      throw new IllegalArgumentException("Configs are not equal!");
    }
    waves.add(w);
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
      old.delete();
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
      ((Wave) o).delete();
    }
    return exist;
  }

  /**
   * Combines the wave, i.e. adding all the waves together. Saves the wave in a temporary file prefixed
   * with "combined".
   * 
   * @return
   */
  public Wave combine() {
    File f;
    try {
      f = File.createTempFile("combined", ".wav");
    } catch (IOException e) {
      throw new RuntimeException("Could not create temporary file for combined wave!", e);
    }
    Wave result = new Wave(f, getConfig());
    double scale = 1d / getWaves().size();
    for (Wave w : getWaves()) {
      result.addWave(w, 0, 0, result.getNumFrames(), scale);
    }
    return result;
  }

  /**
   * Clears the list of all waves and closes the files.
   */
  public void clear() {
    for (Wave w : waves) {
      w.delete();
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
