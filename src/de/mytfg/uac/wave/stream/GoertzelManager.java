package de.mytfg.uac.wave.stream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class GoertzelManager {

  private InputWave in;
  private int samplingrate;
  
  private int length;
  private HashMap<Integer, FrequencyData> frequenciesData = new HashMap<>();
  private ArrayList<GoertzelParallelized>[] goertzels;
  
  private int offset;

  @SuppressWarnings("unchecked")
  public GoertzelManager(InputWave in, int samplingrate, int length) {
    this.in = in;
    this.samplingrate = samplingrate;
    this.length = length;
    this.goertzels = new ArrayList[length];
  }
  
  private FrequencyData init(int frequency) {
    double k = frequency / (double) samplingrate;
    double omega = 2d * Math.PI * k;
    double sin = Math.sin(omega);
    double cos = Math.cos(omega);
    FrequencyData data = new FrequencyData(frequency, sin, cos);
    frequenciesData.put(frequency, data);
    return data;
  }
  
  public void add(GoertzelParallelized... goertzels) {
    for(GoertzelParallelized g : goertzels) {
      add(g);
    }
  }
  
  public void add(GoertzelParallelized g) {
    FrequencyData data = frequenciesData.get(g.getFrequency());
    if(data == null) {
      data = init(g.getFrequency());
    }
    g.init(data.sin, data.cos);
    ArrayList<GoertzelParallelized> list = goertzels[g.getOffset()];
    if(list == null) {
      list = new ArrayList<>();
      goertzels[g.getOffset()] = list;
    }
    list.add(g);
  }
  
  public void processSample() throws IOException {
    
    offset++;
    if(offset == length) {
      offset = 0;
    }
    
    double sample = in.readSample();
    for(int i = 0; i < length; i++) {
      ArrayList<GoertzelParallelized> list = goertzels[i];
      if(list == null) {
        continue;
      }
      for(GoertzelParallelized g : list) {
        if(!g.isEnabled()) {
          continue;
        }
        g.processSample(sample);
        if(i == offset) {
          g.newBlock();
        }
      }
    }
    
  }
  
  public void processSamples(int count) throws IOException {
    for(int i = 0; i < count; i++) {
      processSample();
    }
  }
  
  public GoertzelParallelized getGoertzel(int frequency, int offset) {
    ArrayList<GoertzelParallelized> list = goertzels[offset];
    GoertzelParallelized g = list.stream().filter((goertzel) -> goertzel.getFrequency() == frequency).findFirst().get();
    return g;
  }

  public int getOffset() {
    return offset;
  }

}

class FrequencyData {
  
  int frequency;
  double sin;
  double cos;
  
  public FrequencyData(int frequency, double sin, double cos) {
    super();
    this.frequency = frequency;
    this.sin = sin;
    this.cos = cos;
  }
  
}
