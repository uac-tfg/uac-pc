package de.mytfg.uac.examples.stream;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import de.mytfg.uac.wave.stream.InputWaveRecorder;
import de.mytfg.uac.wave.stream.OutputWaveStreamWriter;

public class Recorder {
	
	public static void main(String[] args) throws FileNotFoundException {
		InputWaveRecorder recorder = new InputWaveRecorder(2500);
		OutputWaveStreamWriter writer = new OutputWaveStreamWriter(new FileOutputStream(args[0] + "/record-" + System.currentTimeMillis()));
		double sample;
		while(true) {
			try {
				sample = recorder.readSample();
				writer.writeSample(sample);
			} catch(IOException e) {
				System.err.println("IOEXCEPTION: " + System.currentTimeMillis());
			}
		}
	}

}
