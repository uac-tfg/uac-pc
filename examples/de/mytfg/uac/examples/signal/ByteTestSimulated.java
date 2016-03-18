package de.mytfg.uac.examples.signal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.mytfg.uac.signal.SignalConfig;
import de.mytfg.uac.signal.SignalInputStream;
import de.mytfg.uac.signal.SignalOutputStream;
import de.mytfg.uac.util.ByteUtil;
import de.mytfg.uac.wave.stream.InputWaveStreamReader;
import de.mytfg.uac.wave.stream.OutputWaveStreamWriter;

public class ByteTestSimulated {
  
  public static void main(String[] args) throws IOException {
    SignalConfig config = new SignalConfig();
    config.put("samplingrate", 5000);
    config.put("periodsperbit", 3);
    
//    config.put("modulation", "am");
//    config.put("mainfrequency", 250);
    config.put("threshold", 1d);
    
    config.put("modulation", "fm");
    config.put("frequency.high", 250);
    config.put("frequency.low", 550);
    
//    byte[] data = new byte[1];
//    new Random().nextBytes(data);
    byte[] data = ByteUtil.toByteArray("10101010110011001111000010011001");
    
    ByteArrayOutputStream bufferOut = new ByteArrayOutputStream();
    OutputWaveStreamWriter waveOut = new OutputWaveStreamWriter(bufferOut);
    SignalOutputStream out = new SignalOutputStream(waveOut, config);
    
    for(int i = 0; i < 100; i++) {
      waveOut.writeSample(0);
    }
    
    out.synchronize();
    out.write(data);
    
    for(int i = 0; i < 3000; i++) {
      waveOut.writeSample(0);
    }
    
    out.close();
    
    System.out.println(ByteUtil.toBitString(data));
    
    byte[] buffer = bufferOut.toByteArray();
    System.out.println(buffer.length);
    
    ByteArrayInputStream bufferIn = new ByteArrayInputStream(buffer);
    InputWaveStreamReader waveIn = new InputWaveStreamReader(bufferIn);
    SignalInputStream in = new SignalInputStream(waveIn, config);
    
    byte[] read = new byte[data.length];
    in.synchronize();
    in.read(read, 0, 3);
    read[3] = (byte) in.read();
    
    in.close();
    
    System.out.println(ByteUtil.toBitString(data));
    System.out.println(ByteUtil.toBitString(read));
  }

}
