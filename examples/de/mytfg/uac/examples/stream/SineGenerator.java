package de.mytfg.uac.examples.stream;

import java.io.IOException;

import de.mytfg.uac.wave.stream.InputWaveSine;
import de.mytfg.uac.wave.stream.OutputWaveSpeaker;

public class SineGenerator {

  public static void main(String[] args) throws IOException {
    int samplingrate = 2500;
    int f = 250;
    InputWaveSine sine = new InputWaveSine(f, 0, samplingrate);
    OutputWaveSpeaker speaker = new OutputWaveSpeaker(samplingrate);
    for(int i = 0; i < samplingrate * 3; i++) {
      speaker.writeSample(sine.readSample());
    }
    speaker.close();
  }

}
