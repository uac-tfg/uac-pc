package de.mytfg.uac.wave.stream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class GoertzelManager {

  private InputWave in;
  private int samplingrate;
  
  private int length;
  private HashMap<Integer, FrequencyData> frequenciesData = new HashMap<>();
  private ArrayList<Goertzel>[] goertzels;
  
  private int offset;

  @SuppressWarnings("unchecked")
  public GoertzelManager(InputWave in, int samplingrate, int length) {
    this.in = in;
    this.samplingrate = samplingrate;
    this.length = length;
    this.goertzels = new ArrayList[length];
  }
  
  private FrequencyData init(int frequency) {
    double k = (((double) length * (double) frequency) / (double) samplingrate);
    double omega = (2d * Math.PI * k) / (double) length;
    double sin = Math.sin(omega);
    double cos = Math.cos(omega);
    FrequencyData data = new FrequencyData(frequency, sin, cos);
//    System.out.println("|||| " + data.frequency + " " + data.sin + " " + data.cos);
    frequenciesData.put(frequency, data);
    return data;
  }
  
  public void add(Goertzel... goertzels) {
    for(Goertzel g : goertzels) {
      add(g);
    }
  }
  
  public void add(Goertzel g) {
    FrequencyData data = frequenciesData.get(g.getFrequency());
    if(data == null) {
      data = init(g.getFrequency());
    }
//    System.out.println("--- " + data.frequency + " " + data.sin + " " + data.cos);
    g.init(data.sin, data.cos);
    ArrayList<Goertzel> list = goertzels[g.getOffset()];
    if(list == null) {
      list = new ArrayList<>();
      goertzels[g.getOffset()] = list;
    }
    list.add(g);
  }
  
  public void processSample() throws IOException {
    double sample = in.readSample();
//    System.out.println("===== " + sample);
    for(int i = 0; i < length; i++) {
      ArrayList<Goertzel> list = goertzels[i];
      if(list == null) {
        continue;
      }
      for(Goertzel g : list) {
        g.processSample(sample);
        if(i == offset) {
          g.newBlock();
        }
      }
    }
    
    offset++;
    if(offset == length) {
      offset = 0;
    }
  }
  
  public Goertzel getGoertzel(int frequency, int offset) {
    ArrayList<Goertzel> list = goertzels[offset];
    Goertzel g = list.stream().filter((goertzel) -> goertzel.getFrequency() == frequency).findFirst().get();
    return g;
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
