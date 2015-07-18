package de.mytfg.uac.wave;

/**
 * Config for a wave object.
 * 
 * @author Tilman Hoffbauer
 */
public class WaveConfig {

  private int validBits;
  private int sampleRate;
  private long numFrames;

  /**
   * Constructs a new config.
   * 
   * @param validBits
   * @param sampleRate
   * @param numChannels
   * @param numFrames
   */
  public WaveConfig(int validBits, int sampleRate, long numFrames) {
    super();
    this.validBits = validBits;
    this.sampleRate = sampleRate;
    this.numFrames = numFrames;
  }

  /**
   * Creates a new default config. Default values are:<br>
   * validBits = 16 <br>
   * sampleRate = 44100
   * 
   * @param numFrames
   * @return the wave config
   */
  public static WaveConfig createDefaultWaveConfig(long numFrames) {
    return new WaveConfig(16, 44100, numFrames);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (int) (numFrames ^ (numFrames >>> 32));
    result = prime * result + sampleRate;
    result = prime * result + validBits;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    WaveConfig other = (WaveConfig) obj;
    if (numFrames != other.numFrames)
      return false;
    if (sampleRate != other.sampleRate)
      return false;
    if (validBits != other.validBits)
      return false;
    return true;
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

  public long getNumFrames() {
    return numFrames;
  }

  public void setNumFrames(long numFrames) {
    this.numFrames = numFrames;
  }

}
