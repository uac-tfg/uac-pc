package de.mytfg.uac.examples.teensy;

public class TeensyByteTest {
	
}
//
//import java.io.BufferedReader;
//import java.io.BufferedWriter;
//import java.io.FileWriter;
//import java.io.InputStreamReader;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.Enumeration;
//import java.util.Random;
//
//import de.mytfg.uac.signal.SignalConfig;
//import de.mytfg.uac.signal.SignalOutputStream;
//import de.mytfg.uac.util.ByteUtil;
//import de.mytfg.uac.wave.stream.OutputWaveSpeaker;
//import gnu.io.CommPortIdentifier;
//import gnu.io.SerialPort;
//import gnu.io.SerialPortEvent;
//import gnu.io.SerialPortEventListener;
//
//
//public class TeensyByteTest {
//
//  private static final String PORT_NAMES[] = {"/dev/ttyACM0", "/dev/ttyACM1"};
//  private static final int TIME_OUT = 2000;
//  private static final int DATA_RATE = 9600;
//
//  private static SerialPort serialPort;
//  private static byte[] data = new byte[4];
//
//  public static void initialize() {
//    CommPortIdentifier portId = null;
//    Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();
//
//    while (portEnum.hasMoreElements()) {
//      CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
//      for (String portName : PORT_NAMES) {
//        if (currPortId.getName().equals(portName)) {
//          portId = currPortId;
//          break;
//        }
//      }
//    }
//    if (portId == null) {
//      System.out.println("Could not find COM port.");
//      return;
//    }
//
//    try {
//      serialPort = (SerialPort) portId.open("TeensyByteTest", TIME_OUT);
//      serialPort.setSerialPortParams(DATA_RATE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
//          SerialPort.PARITY_NONE);
//      final BufferedReader input =
//          new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
//
//      serialPort.addEventListener(new SerialPortEventListener() {
//        @Override
//        public void serialEvent(SerialPortEvent oEvent) {
//          if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
//            try {
//              String inputLine = input.readLine();
//              System.out.println(inputLine);
//            } catch (Exception e) {
//              System.err.println(e.toString());
//            }
//          }
//        }
//      });
//      serialPort.notifyOnDataAvailable(true);
//    } catch (Exception e) {
//      System.err.println(e.toString());
//    }
//  }
//
//  public static void main(String[] args) throws Exception {
//    SignalConfig config = new SignalConfig();
//    config.put("samplingrate", 2500);
//    config.put("periodsperbit", 4);
//    
////    config.put("modulation", "am");
////    config.put("mainfrequency", 250);
////    config.put("threshold", 100d);
//    
//    config.put("modulation", "fm");
//    config.put("frequency.high", 350);
//    config.put("frequency.low", 250);
//    config.put("syncbits", "10011001110011010110000111001100");
////    config.put("syncbits", "11110000111100001111000011110000");
//    
//    OutputWaveSpeaker speaker = new OutputWaveSpeaker(config.getInt("samplingrate"));
//    SignalOutputStream out = new SignalOutputStream(speaker, config);
//    TeensyByteTest.initialize();
//    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//    Random r = new Random();
//    while(reader.readLine() == null || true) {
//      out.synchronize();
//      r.nextBytes(data);
//      String dataString = ByteUtil.toBitString(data);
//      System.out.println(dataString);
//      out.write(data);
//    }
//  }
//}
