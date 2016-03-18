package de.mytfg.uac.examples.signal;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ReadAnalysis {
  
  static BufferedReader record;
  static BufferedReader output;

  public static void main(String[] args) throws IOException {
    record = new BufferedReader(new FileReader("record1.txt"));
    output = new BufferedReader(new FileReader("output1.txt"));
    
    doPart();
    
    record.close();
    output.close();
  }
  
  static void doPart() throws IOException {
    while(true) {
      doLine();
    }
  }
  
  static void doLine() throws IOException {
    String recFull = readRecordLine();
    if(recFull == null) {
      return;
    }
    String[] recs = recFull.split(" ");
    int index = Integer.valueOf(recs[0].substring(0, recs[0].length() - 1));
    String rec = recs[1];
    while(true) {
      String out = readOutputLine();
      if(out == null) {
        return;
      }
      int localBitsHighErrorCounter = 0;
      int localBitsLowErrorCounter = 0;
      for(int i = 0; i < out.length(); i++) {
        char outC = out.charAt(i);
        char recC = rec.charAt(i);
        if(outC != recC) {
          if(outC == '1') {
            localBitsHighErrorCounter++;
          } else {
            localBitsLowErrorCounter++;
          }
        }
      }
      int localTotalBitsErrorCounter = localBitsHighErrorCounter + localBitsLowErrorCounter;
      if(localTotalBitsErrorCounter < 10) {
        System.out.println(index + ": " + localTotalBitsErrorCounter);
        break;
      }
    }
  }
  
  static String readRecordLine() throws IOException {
    String line = null;
    while(true) {
      line = record.readLine();
      if(line.matches("[1-9]+: [10]+")) {
        break;
      } else if(line.startsWith("=====")) {
        return null;
      }  
    }
    return line;
  }
  
  static String readOutputLine() throws IOException {
    while(true) {
      String line = null;
      while(true) {
        line = output.readLine();
        if(line == null) {
          return null;
        }
        line = line.trim();
        if(line.matches("[10]+")) {
          return line;
        }
      }
    }
  }

}
