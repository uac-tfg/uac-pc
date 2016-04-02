package de.mytfg.uac.examples.stream;

import java.io.FileInputStream;
import java.io.IOException;

import de.mytfg.uac.wave.stream.InputWaveStreamReader;
import de.mytfg.uac.wave.stream.OutputWaveSpeaker;

public class Speaker {

	public static void main(String[] args) throws IOException {
		InputWaveStreamReader reader = new InputWaveStreamReader(new FileInputStream("record-1459424042096"));
		OutputWaveSpeaker speaker = new OutputWaveSpeaker(2500);
		double sample;
		while(true) {
			sample = reader.readSample();
			speaker.writeSample(sample);
		}
	}
}
