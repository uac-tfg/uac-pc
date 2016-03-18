package de.mytfg.uac.examples.signal;

import java.io.IOException;
import java.util.Random;

import de.mytfg.uac.signal.SignalConfig;
import de.mytfg.uac.signal.SignalOutputStream;
import de.mytfg.uac.util.ByteUtil;
import de.mytfg.uac.wave.stream.OutputWaveSpeaker;

public class SendTest {

	private static Random random;
	
	public static void main(String[] args) {
		long seed = new Random().nextLong();
		random = new Random(seed);
		
//		System.out.println("Startbits");
//		{
//			System.out.println(System.currentTimeMillis());
//		    SignalConfig config = new SignalConfig();
//		    config.put("samplingrate", 2500);
//		    config.put("periodsperbit", 40);
//		    
////		    config.put("modulation", "am");
////		    config.put("mainfrequency", 250);
////		    config.put("threshold", 100d);
//		    
//		    config.put("modulation", "fm");
//		    config.put("frequency.high", 250);
//		    config.put("frequency.low", 150);
//		    config.put("syncbits", "10011001110011010110000111001100");
////		    config.put("syncbits", "11110000111100001111000011110000");
//		    test(config, 50);
//		}
//		{
//			System.out.println(System.currentTimeMillis());
//		    SignalConfig config = new SignalConfig();
//		    config.put("samplingrate", 2500);
//		    config.put("periodsperbit", 40);
//		    
////		    config.put("modulation", "am");
////		    config.put("mainfrequency", 250);
////		    config.put("threshold", 100d);
//		    
//		    config.put("modulation", "fm");
//		    config.put("frequency.high", 250);
//		    config.put("frequency.low", 150);
//		    config.put("syncbits", "10101010101010101010101010101010");
//		    test(config, 50);
//		}
//		{
//			System.out.println(System.currentTimeMillis());
//		    SignalConfig config = new SignalConfig();
//		    config.put("samplingrate", 2500);
//		    config.put("periodsperbit", 40);
//		    
////		    config.put("modulation", "am");
////		    config.put("mainfrequency", 250);
////		    config.put("threshold", 100d);
//		    
//		    config.put("modulation", "fm");
//		    config.put("frequency.high", 250);
//		    config.put("frequency.low", 150);
//		    config.put("syncbits", "11001100110011001100110011001100");
////		    config.put("syncbits", "11110000111100001111000011110000");
//		    test(config, 50);
//		}
//		{
//			System.out.println(System.currentTimeMillis());
//		    SignalConfig config = new SignalConfig();
//		    config.put("samplingrate", 2500);
//		    config.put("periodsperbit", 40);
//		    
////		    config.put("modulation", "am");
////		    config.put("mainfrequency", 250);
////		    config.put("threshold", 100d);
//		    
//		    config.put("modulation", "fm");
//		    config.put("frequency.high", 250);
//		    config.put("frequency.low", 150);
//		    config.put("syncbits", "10111011101110111011101110111011");
////		    config.put("syncbits", "11110000111100001111000011110000");
//		    test(config, 50);
//		}
//		{
//			System.out.println(System.currentTimeMillis());
//		    SignalConfig config = new SignalConfig();
//		    config.put("samplingrate", 2500);
//		    config.put("periodsperbit", 40);
//		    
////		    config.put("modulation", "am");
////		    config.put("mainfrequency", 250);
////		    config.put("threshold", 100d);
//		    
//		    config.put("modulation", "fm");
//		    config.put("frequency.high", 250);
//		    config.put("frequency.low", 150);
//		    config.put("syncbits", "11111110111111101111111011111110");
////		    config.put("syncbits", "11110000111100001111000011110000");
//		    test(config, 50);
//		}
//		{
//			System.out.println(System.currentTimeMillis());
//		    SignalConfig config = new SignalConfig();
//		    config.put("samplingrate", 2500);
//		    config.put("periodsperbit", 40);
//		    
////		    config.put("modulation", "am");
////		    config.put("mainfrequency", 250);
////		    config.put("threshold", 100d);
//		    
//		    config.put("modulation", "fm");
//		    config.put("frequency.high", 250);
//		    config.put("frequency.low", 150);
//		    config.put("syncbits", "00000001000000010000000100000001");
////		    config.put("syncbits", "11110000111100001111000011110000");
//		    test(config, 50);
//		}
//		{
//			System.out.println(System.currentTimeMillis());
//		    SignalConfig config = new SignalConfig();
//		    config.put("samplingrate", 2500);
//		    config.put("periodsperbit", 40);
//		    
////		    config.put("modulation", "am");
////		    config.put("mainfrequency", 250);
////		    config.put("threshold", 100d);
//		    
//		    config.put("modulation", "fm");
//		    config.put("frequency.high", 250);
//		    config.put("frequency.low", 150);
//		    config.put("syncbits", "11100011100011100011100011100011");
////		    config.put("syncbits", "11110000111100001111000011110000");
//		    test(config, 50);
//		}
//		{
//			System.out.println(System.currentTimeMillis());
//		    SignalConfig config = new SignalConfig();
//		    config.put("samplingrate", 2500);
//		    config.put("periodsperbit", 40);
//		    
////		    config.put("modulation", "am");
////		    config.put("mainfrequency", 250);
////		    config.put("threshold", 100d);
//		    
//		    config.put("modulation", "fm");
//		    config.put("frequency.high", 250);
//		    config.put("frequency.low", 150);
//		    config.put("syncbits", "10011001100110011001100110011001");
////		    config.put("syncbits", "11110000111100001111000011110000");
//		    test(config, 50);
//		}
		{
			System.out.println(System.currentTimeMillis());
			System.out.println("Frequency constant ppb");
			for(int i = 50; i <= 950; i += 100) {
				SignalConfig config = new SignalConfig();
			    config.put("samplingrate", 2500);
			    config.put("periodsperbit", 40);
			    
//			    config.put("modulation", "am");
//			    config.put("mainfrequency", 250);
//			    config.put("threshold", 100d);
			    
			    config.put("modulation", "fm");
			    config.put("frequency.high", i + 200);
			    config.put("frequency.low", i);
			    config.put("syncbits", "10011001110011010110000111001100");
//			    config.put("syncbits", "11110000111100001111000011110000");
			    test(config, 100);
			}
			System.out.println(System.currentTimeMillis());
			System.out.println("Frequency variable ppb");
			for(int i = 50; i <= 950; i += 100) {
				SignalConfig config = new SignalConfig();
			    config.put("samplingrate", 2500);
			    config.put("periodsperbit", i / 3);
			    
//			    config.put("modulation", "am");
//			    config.put("mainfrequency", 250);
//			    config.put("threshold", 100d);
			    
			    config.put("modulation", "fm");
			    config.put("frequency.high", i + 200);
			    config.put("frequency.low", i);
			    config.put("syncbits", "10011001110011010110000111001100");
//			    config.put("syncbits", "11110000111100001111000011110000");
			    test(config, 100);
			}
		}
		
		System.out.println("Seed: " + seed);
	}
	
	public static void test(SignalConfig config, int count) {
		System.out.println(System.currentTimeMillis());
		System.out.println("=================================");
		
		OutputWaveSpeaker speaker = new OutputWaveSpeaker(config.getInt("samplingrate"));
		SignalOutputStream out = new SignalOutputStream(speaker, config);
		
		for(int i = 0; i < count; i++) {
			byte[] data = new byte[4];
			random.nextBytes(data);
			System.out.println(ByteUtil.toBitString(data));
			try {
				out.synchronize();
				out.write(data);
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		try {
			out.close();
			speaker.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

}