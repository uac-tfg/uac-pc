package de.mytfg.uac.wave;

/**
 * Config for a wave object.
 * 
 * @author Tilman Hoffbauer
 */
public class WaveConfig {

  private int validBits;
  private int sampleRate;
  private int numChannels;
  private long numFrames;

  /**
   * Constructs a new config.
   * 
   * @param validBits
   * @param sampleRate
   * @param numChannels
   * @param numFrames
   */
  public WaveConfig(int validBits, int sampleRate, int numChannels, long numFrames) {
    super();
    this.validBits = validBits;
    this.sampleRate = sampleRate;
    this.numChannels = numChannels;
    this.numFrames = numFrames;
  }

  /**
   * Creates a new default config. Default values are:<br>
   * validBits = 16
   * sampleRate = 44100
   * 
   * @param numFrames
   * @return the wave config
   */
  public static WaveConfig createDefaultWaveConfig(long numFrames) {
    return new WaveConfig(16, 44100, 1, numFrames);
  }

  public int getValidBits() {
    return validBits;
  }

  public void setValidBits(int validBits) {
    this.validBits = validBits;
  }

  public int getSampleRate() {
    return sampleRate;
  }

  public void setSampleRate(int sampleRate) {
    this.sampleRate = sampleRate;
  }

  public int getNumChannels() {
    return numChannels;
  }

  public void setNumChannels(int numChannels) {
    this.numChannels = numChannels;
  }

  public long getNumFrames() {
    return numFrames;
  }

  public void setNumFrames(long numFrames) {
    this.numFrames = numFrames;
  }

}
