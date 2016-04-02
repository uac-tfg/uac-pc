package de.mytfg.uac.examples.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;

import de.mytfg.uac.signal.SignalConfig;
import de.mytfg.uac.signal.SignalInputStream;
import de.mytfg.uac.util.ByteUtil;
import de.mytfg.uac.wave.stream.InputWaveStreamReader;
import de.mytfg.uac.wave.stream.LimitExceededException;

public class Analyzer {

	static final int SAMPLING_RATE = 2500;
	
	static File recordFile;
	static BufferedReader testFile;
	static int offset;
	
	public static void main(String[] args) throws IOException {
		recordFile = new File("record-1459518677971");
		testFile = new BufferedReader(new FileReader("test-20160401-1551"));
		
		testFile.readLine(); // Test started at
		
		readStartSequence((byte) 50, 40);
		
		{
			for(int i = 50; i <= 950; i += 100) {
				SignalConfig config = new SignalConfig();
			    config.put("samplingrate", SAMPLING_RATE);
			    config.put("periodsperbit", 40);
			    config.put("modulation", "fm");
			    config.put("frequency.high", i + 200);
			    config.put("frequency.low", i);
			    config.put("syncbits", "10011001110011010110000111001100");
			    readTest(config, 100);
			}
			for(int i = 50; i <= 950; i += 100) {
				SignalConfig config = new SignalConfig();
			    config.put("samplingrate", SAMPLING_RATE);
			    config.put("periodsperbit", i / 3);
			    config.put("modulation", "fm");
			    config.put("frequency.high", i + 200);
			    config.put("frequency.low", i);
			    config.put("syncbits", "10011001110011010110000111001100");
			    readTest(config, 100);
			}
		}
	}
	
	static void readStartSequence(byte count, int ppb) throws IOException {
		testFile.readLine(); // Start Sequence
		System.out.println("Start Sequence");
		SignalConfig config = new SignalConfig();
	    config.put("samplingrate", 2500);
	    config.put("periodsperbit", ppb);
	    config.put("modulation", "fm");
	    config.put("frequency.high", 700);
	    config.put("frequency.low", 500);
	    config.put("syncbits", "10011001110011010110000111001100");
	    
	    InputWaveStreamReader reader = new InputWaveStreamReader(new FileInputStream(recordFile));
	    SignalInputStream in = new SignalInputStream(reader, config);
	    
	    byte lastVal = -1;
	    
	    long totalOffset = 0;
	    int receivedCount = 0;
	    
	    while(true) {
	    	in.synchronize();
	    	byte b = (byte) in.read();
	    	if(lastVal > b || b >= count) {
	    		break;
	    	}
	    	receivedCount++;
	    	lastVal = b;
	    	long recorderFrame = reader.getPosition();
	    	
	    	String[] parts;
	    	while(Integer.valueOf((parts = testFile.readLine().split("@"))[0]) < b) {
	    		System.out.println("Missed start sequence #" + parts[0]);
	    	}
	    	long senderFrame = Long.valueOf(parts[1].split("\\-")[1]);
	    	
	    	long offset = recorderFrame - senderFrame;
	    	totalOffset += offset;
	    	
	    	System.out.println(b + "@" + recorderFrame + "/" + senderFrame + "=" + offset);
	    }
	    in.close();
	    
	    Analyzer.offset = (int) (totalOffset / receivedCount);
	    System.out.println("Computed offset of: " + Analyzer.offset);
	}

	static void readTest(SignalConfig config, int count) throws IOException {
		int failCounter = 0;
		int errorHighCounter = 0;
		int errorLowCounter = 0;
		int bits = 0;
		
		String header = testFile.readLine();
		String suffix = header.substring(header.lastIndexOf('@') + 2);
		long beginFrame = Long.valueOf(suffix.substring(0, suffix.indexOf(' ')));
		System.out.println(header);
		
		InputWaveStreamReader reader = new InputWaveStreamReader(new FileInputStream(recordFile));
		reader.skip(beginFrame + offset);
		SignalInputStream in = new SignalInputStream(reader, config);
		
		for(int i = 0; i < count; i++) {
			String[] parts = testFile.readLine().split("@");
			String data = parts[0];
			long start = Long.valueOf(parts[1].split("\\-")[0]);
			long end = Long.valueOf(parts[1].split("\\-")[1]);
			
			byte[] readBytes = new byte[data.length() / 8];
			reader.setLimit(end + offset + 100);
			try {
				in.synchronize();
				in.read(readBytes);
			} catch(LimitExceededException e) {
				failCounter++;
				continue;
			}
			String read = ByteUtil.toBitString(readBytes);
			bits += read.length();
			
			for(int j = 0; j < data.length(); j++)  {
				char c = data.charAt(j);
				if(c != read.charAt(j)) {
					if(c == '1') {
						errorHighCounter++;
					} else {
						errorLowCounter++;
					}
				}
			}
		}
		
		int errorBitsSum = errorHighCounter + errorLowCounter;
		System.out.println("Fail: " + failCounter + " (" + (failCounter / (double) bits) + ")");
		System.out.println("High: " + errorHighCounter + " (" + (errorHighCounter / (double) bits) + ")");
		System.out.println("Low:  " + errorLowCounter + " (" + (errorLowCounter / (double) bits) + ")");
		System.out.println("Sum:  " + errorBitsSum + " (" + (errorBitsSum / (double) bits) + ")");
	}
}
