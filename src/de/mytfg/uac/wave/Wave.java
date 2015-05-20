package de.mytfg.uac.wave;

import java.io.File;
import java.io.IOException;

import de.mytfg.uac.wave.wav.WavFile;
import de.mytfg.uac.wave.wav.WavFileException;

public class Wave {
	
	private WavFile wav;
	
	public Wave(File file, int numChannels, long numFrames, int validBits, int sampleRate) {
		try {
			this.wav = WavFile.newWavFile(file, numChannels, numFrames, validBits, sampleRate);
		} catch (IOException | WavFileException e) {
			throw new RuntimeException(e);
		}
	}
	
	public Wave(File file) {
		try {
			this.wav = WavFile.openWavFile(file);
		} catch (IOException | WavFileException e) {
			throw new RuntimeException(e);
		}
	}
	
//	public double getSample(int c, int i, int from, int to) {
//		
//	}

}
