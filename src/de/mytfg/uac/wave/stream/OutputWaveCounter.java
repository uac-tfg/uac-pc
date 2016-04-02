package de.mytfg.uac.wave.stream;

import java.io.IOException;

public class OutputWaveCounter extends OutputWave {
	
	private long counter;
	private OutputWave out;
	
	public OutputWaveCounter(OutputWave out) {
		this.out = out;
	}
	
	@Override
	public void writeSample(double sample) throws IOException {
		counter++;
		out.writeSample(sample);
	}
	
	public long getCounter() {
		return counter;
	}

	@Override
	public void close() throws IOException {
		out.close();
	}

}
