package de.mytfg.uac.examples.signal;

import java.io.IOException;
import java.util.Random;

import de.mytfg.uac.signal.SignalConfig;
import de.mytfg.uac.signal.SignalOutputStream;
import de.mytfg.uac.util.ByteUtil;
import de.mytfg.uac.wave.stream.OutputWaveCounter;
import de.mytfg.uac.wave.stream.OutputWaveSpeaker;

public class SendTest {

	static final int SAMPLING_RATE = 2500;
	static final int DELAY = 1000;
	static final int DATA_LENGTH = 4;
	
	private static Random random;
	private static OutputWaveCounter counter;
	
	public static void main(String[] args) throws IOException {
		long seed = new Random().nextLong();
		random = new Random(seed);
		
		OutputWaveSpeaker speaker = new OutputWaveSpeaker(SAMPLING_RATE);
		counter = new OutputWaveCounter(speaker);
		
		System.out.println("Test started at: " + System.currentTimeMillis());
		sendStartSequence((byte) 10, 4);
		{
			for(int i = 50; i <= 950; i += 200) {
				SignalConfig config = new SignalConfig();
			    config.put("samplingrate", SAMPLING_RATE);
			    config.put("periodsperbit", 4);
			    config.put("modulation", "fm");
			    config.put("frequency.high", i + 200);
			    config.put("frequency.low", i);
			    config.put("syncbits", "10011001110011010110000111001100");
			    test("Frequency constant ppb f=" + i, config, 10);
			}
			for(int i = 50; i <= 950; i += 200) {
				SignalConfig config = new SignalConfig();
			    config.put("samplingrate", SAMPLING_RATE);
			    config.put("periodsperbit", i / 30);
			    config.put("modulation", "fm");
			    config.put("frequency.high", i + 200);
			    config.put("frequency.low", i);
			    config.put("syncbits", "10011001110011010110000111001100");
			    test("Frequency variable ppb f=" + i, config, 10);
			}
		}
		
		counter.close();
		System.out.println("Total samples: " + counter.getCounter());
		System.out.println("End time: " + System.currentTimeMillis());
		
		System.out.println("Seed: " + seed);
	}
	
	private static void sendStartSequence(byte count, int ppb) {
		System.out.println("Start Sequence");
		SignalConfig config = new SignalConfig();
	    config.put("samplingrate", SAMPLING_RATE);
	    config.put("periodsperbit", ppb);
	    config.put("modulation", "fm");
	    config.put("frequency.high", 700);
	    config.put("frequency.low", 500);
	    config.put("syncbits", "10011001110011010110000111001100");
	    
		@SuppressWarnings("resource")
		SignalOutputStream out = new SignalOutputStream(counter, config);
		
		for(byte b = 0; b < count; b++) {
			System.out.print(b + "@" + counter.getCounter() + "-");
			try {
				out.synchronize();
				out.write(new byte[] {b});
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println(counter.getCounter());
			try {
				writeZero(DELAY);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    }

	public static void test(String name, SignalConfig config, int count) {
		System.out.println("=== " + name + " @ " + counter.getCounter() + " / " + System.currentTimeMillis() +" ===");
		
		@SuppressWarnings("resource")
		SignalOutputStream out = new SignalOutputStream(counter, config);
		
		for(int i = 0; i < count; i++) {
			byte[] data = new byte[DATA_LENGTH];
			random.nextBytes(data);
			System.out.print(ByteUtil.toBitString(data) + "@" + counter.getCounter() + "-");
			try {
				out.synchronize();
				out.write(data);
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println(counter.getCounter());
			try {
				writeZero(DELAY);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	static void writeZero(int ms) throws IOException {
		int count = (int) (ms * (SAMPLING_RATE / 1000d));
		for(int i = 0; i < count; i++) {
			counter.writeSample(0);
		}
	}

}
