package de.mytfg.uac.wave.view;

import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JPanel;

import de.mytfg.uac.wave.CombinedWave;
import de.mytfg.uac.wave.Wave;

public class WaveVisualizerPanel extends JPanel {

  /**
   * Serial version UID needed by Serializable. Generated by eclipse.
   */
  private static final long serialVersionUID = 7700452874892977640L;

  private CombinedWave wave;

  private boolean[] drawWave;
  
  private long from = 0;
  private int length = 1800;
  private double distance = 1;
  private double scale = 60;
  private int labelEvery = 200;
  private int labelFactor = 5;
  private String labelSuffix = "";
  private int labelMarkerLength = 7;
  
  public enum LabelMethod {
    TIME ("10ns"),
    FRAME ("frame");
    
    private String displayName;
    
    private LabelMethod(String displayName) {
      this.displayName = displayName;
    }

    public String getDisplayName() {
      return displayName;
    }
    
    @Override
    public String toString() {
      return displayName;
    }
  }

  public WaveVisualizerPanel(CombinedWave wave) {
    this.wave = wave;
    this.drawWave = new boolean[wave.getWaves().size() + 1];
    for(int i = 0; i < drawWave.length; i++) {
      drawWave[i] = true;
    }
  }

  @Override
  public void paintComponent(Graphics g) {
    g.clearRect(0, 0, getWidth(), getHeight());

    if (wave == null) {
      g.drawString("No wave given!", 10, 20);
      return;
    }

    g.translate(10, 0);
    drawAxis(g);

    g.translate(0, (int) scale);
    ArrayList<Wave> waves = wave.getWaves();
    for (int i = 0; i <= waves.size(); i++) {
      if(!drawWave[i]) {
        continue;
      }
      Wave w;
      if(i == waves.size()) {
        w = wave.combine();
      } else {
        w = waves.get(i);
      }
      drawWave(g, w);
    }
  }

  private void drawAxis(Graphics g) {
    int size = (int) (length * distance * 1.01d);
    g.drawLine(0, (int) scale, size, (int) scale);

    for (int i = 0; i < length / labelEvery; i++) {
      int x = (int) (i * distance * labelEvery);
      int y = (int) scale;
      g.drawLine(x, y, x, y + labelMarkerLength);
      String s = (i * labelFactor + from) + labelSuffix;
      g.drawString(s, x - g.getFontMetrics().stringWidth(s) / 2, y + 20);
    }
  }

  private void drawWave(Graphics g, Wave wave) {
    double[] data = wave.getFrames(from, length);
    double last = data[0];
    int lastY = (int) (last * scale * -1);
    int lastX = 0;
    for (int i = 1; i < data.length; i++) {
      double next = data[i];
      int nextY = (int) (last * scale * -1);
      int nextX = (int) (i * distance);
      g.drawLine(lastX, lastY, nextX, nextY);
      last = next;
      lastY = nextY;
      lastX = nextX;
    }
  }

  public void setLabeling(LabelMethod method, int every) {
    int factor = 0;
    if (method == LabelMethod.FRAME) {
      factor = every;
    } else if (method == LabelMethod.TIME) {
      // TODO: correct
      double time =
          (wave.getConfig().getNumFrames() / (double) wave.getConfig().getSampleRate()) * 100000;
      double labels = getLength() / getLabelEvery();
      double timePerLabel = time / labels;
      factor = (int) timePerLabel;
    }
    setLabelEvery(every);
    setLabelFactor(factor);
  }

  public void adjustSize() {
    double dist = (getWidth() - 20) / (double) getLength();
    setDistance(dist);
    int scale = (getHeight() - 20) / 2;
    setScale(scale);
  }

  public long getFrom() {
    return from;
  }

  public void setFrom(long from) {
    this.from = from;
  }

  public int getLength() {
    return length;
  }

  public void setLength(int length) {
    this.length = length;
  }

  public double getDistance() {
    return distance;
  }

  public void setDistance(double distance) {
    this.distance = distance;
  }

  public double getScale() {
    return scale;
  }

  public void setScale(double scale) {
    this.scale = scale;
  }

  public int getLabelEvery() {
    return labelEvery;
  }

  public void setLabelEvery(int labelEvery) {
    this.labelEvery = labelEvery;
  }

  public int getLabelFactor() {
    return labelFactor;
  }

  public void setLabelFactor(int labelFactor) {
    this.labelFactor = labelFactor;
  }

  public String getLabelSuffix() {
    return labelSuffix;
  }

  public void setLabelSuffix(String labelSuffix) {
    this.labelSuffix = labelSuffix;
  }

  public int getLabelMarkerLength() {
    return labelMarkerLength;
  }

  public void setLabelMarkerLength(int labelMarkerLength) {
    this.labelMarkerLength = labelMarkerLength;
  }

  public CombinedWave getWave() {
    return wave;
  }
  
  public boolean isWaveDrawn(int i) {
    return drawWave[i];
  }
  
  public void setWaveDrawn(int i, boolean draw) {
    drawWave[i] = draw;
    repaint();
  }

}
