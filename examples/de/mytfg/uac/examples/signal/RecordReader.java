package de.mytfg.uac.examples.signal;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import de.mytfg.uac.signal.SignalConfig;
import de.mytfg.uac.signal.SignalInputStream;
import de.mytfg.uac.util.ByteUtil;
import de.mytfg.uac.wave.stream.InputWaveStreamReader;

public class RecordReader {

  static long streamStartMs = 1457885050786l;

  public static void main(String[] args) throws FileNotFoundException {
    test();
  }

  public static void doPart(SignalConfig config, long fromMs, long untilMs)
      throws FileNotFoundException {
    FileInputStream fin =
        new FileInputStream("/media/uboot/Fedora-Live-KDE-x86_64-23-10/record-1457885041538");
    InputWaveStreamReader file = new InputWaveStreamReader(fin);

    try {
      if (streamStartMs != -1) {
        long fromMsRel = fromMs - streamStartMs;
        long fromSample = (long) (fromMsRel * (2500 / 1000d));
        file.skip((int) fromSample);
      }
      SignalInputStream in = new SignalInputStream(file, config);
      Thread thread = new Thread() {
        private long last = -1;

        @Override
        public void run() {
          while (true) {
            long sample = file.getPosition();
            if (sample / (2500 * 600) > last) {
              last = sample / (2500 * 600);
              long seconds = sample / 2500;
              long tenMinutes = seconds / 600;
              System.err.println("Minute: " + tenMinutes * 10);
            }
            if (streamStartMs != -1) {
              long untilMsRel = untilMs - streamStartMs;
              long untilSample = (long) (untilMsRel * (2500 / 1000d));
              if (sample >= untilSample) {
                in.stop = true;
                System.err.println("block");
                break;
              }
            }
            try {
              Thread.sleep(0, 10);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          }
        }
      };
      thread.start();
      int i = 0;
      while (true) {
        in.synchronize();
        if (streamStartMs == -1) {
          streamStartMs = 1457885050786l - (long) (file.getPosition() / 2.5d);
        }
        byte[] data = new byte[4];
        in.read(data);
        System.out.println(i + ": " + ByteUtil.toBitString(data));
        i++;
        if (in.stop) {
          break;
        }
      }
      in.close();
      fin.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    System.out.println("================================");
  }

  public static void test() throws FileNotFoundException {
    System.out.println("Startbits");
    {
      SignalConfig config = new SignalConfig();
      config.put("samplingrate", 2500);
      config.put("periodsperbit", 40);
      config.put("modulation", "fm");
      config.put("frequency.high", 250);
      config.put("frequency.low", 150);
      config.put("syncbits", "10011001110011010110000111001100");
      doPart(config, 1457885050786l, 1457886145951l);
    }
    {
      SignalConfig config = new SignalConfig();
      config.put("samplingrate", 2500);
      config.put("periodsperbit", 40);
      config.put("modulation", "fm");
      config.put("frequency.high", 250);
      config.put("frequency.low", 150);
      config.put("syncbits", "10101010101010101010101010101010");
      doPart(config, 1457886145951l, 1457887240457l);
    }
    {
      SignalConfig config = new SignalConfig();
      config.put("samplingrate", 2500);
      config.put("periodsperbit", 40);
      config.put("modulation", "fm");
      config.put("frequency.high", 250);
      config.put("frequency.low", 150);
      config.put("syncbits", "11001100110011001100110011001100");
      doPart(config, 1457887240457l, 1457888335087l);
    }
    {
      SignalConfig config = new SignalConfig();
      config.put("samplingrate", 2500);
      config.put("periodsperbit", 40);
      config.put("modulation", "fm");
      config.put("frequency.high", 250);
      config.put("frequency.low", 150);
      config.put("syncbits", "10111011101110111011101110111011");
      doPart(config, 1457888335087l, 1457889429389l);
    }
    {
      SignalConfig config = new SignalConfig();
      config.put("samplingrate", 2500);
      config.put("periodsperbit", 40);
      config.put("modulation", "fm");
      config.put("frequency.high", 250);
      config.put("frequency.low", 150);
      config.put("syncbits", "11111110111111101111111011111110");
      doPart(config, 1457889429389l, 1457890524115l);
    }
    {
      SignalConfig config = new SignalConfig();
      config.put("samplingrate", 2500);
      config.put("periodsperbit", 40);
      config.put("modulation", "fm");
      config.put("frequency.high", 250);
      config.put("frequency.low", 150);
      config.put("syncbits", "00000001000000010000000100000001");
      doPart(config, 1457890524115l, 1457891618563l);
    }
    {
      SignalConfig config = new SignalConfig();
      config.put("samplingrate", 2500);
      config.put("periodsperbit", 40);
      config.put("modulation", "fm");
      config.put("frequency.high", 250);
      config.put("frequency.low", 150);
      config.put("syncbits", "11100011100011100011100011100011");
      doPart(config, 1457891618563l, 1457892713129l);
    }
    {
      SignalConfig config = new SignalConfig();
      config.put("samplingrate", 2500);
      config.put("periodsperbit", 40);
      config.put("modulation", "fm");
      config.put("frequency.high", 250);
      config.put("frequency.low", 150);
      config.put("syncbits", "10011001100110011001100110011001");
      doPart(config, 1457892713129l, 1457892713129l + (1457891618563l - 1457892713129l) * 2);
    }
  }
}
