package de.mytfg.uac.terminal;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Scanner;

import de.mytfg.uac.signal.SignalConfig;
import de.mytfg.uac.signal.SignalInputStream;
import de.mytfg.uac.signal.SignalOutputStream;
import de.mytfg.uac.util.ByteUtil;
import de.mytfg.uac.wave.stream.InputWaveRecorder;
import de.mytfg.uac.wave.stream.OutputWaveSpeaker;

public class Terminal implements Runnable {

  private static final byte START_BYTE = ByteUtil.toByteArray("10011001")[0];

  private SignalConfig config;

  private OutputWaveSpeaker outSpeaker;
  private SignalOutputStream outSignal;
  private DataOutputStream out;

  private InputWaveRecorder inRecorder;
  private SignalInputStream inSignal;
  private DataInputStream in;

  private Thread receiverThread;
  private boolean run = false;
  private boolean running = false;

  public Terminal() {
    config = new SignalConfig();
    config.put("mainfrequency", 250);
    config.put("samplingrate", 2500);
    config.put("periodsperbit", 3);
    config.put("threshold", 0.00075d);

    receiverThread = new Thread(this, "Receiver");

    outSpeaker = new OutputWaveSpeaker(config.getInt("samplingrate"));
    outSignal = new SignalOutputStream(outSpeaker, config);
    out = new DataOutputStream(outSignal);

    inRecorder = new InputWaveRecorder(config.getInt("samplingrate"));
    inSignal = new SignalInputStream(inRecorder, config);
    in = new DataInputStream(inSignal);
  }

  public void start() throws IOException {
    run = true;
    receiverThread.start();
  }

  public void stop() {
    run = false;
    while (running) {
    }
    try {
      out.close();
      outSpeaker.close();
    } catch (IOException e) {
      System.out.println("Error while closing stream!");
    }
  }

  public void send(String text) throws IOException {
    outSignal.synchronize();
    out.write(START_BYTE);
    out.writeUTF(text);
  }

  @Override
  public void run() {
    try {
      running = true;
      // inSignal.synchronize();
      while (run) {
        inSignal.synchronize();
        // inSignal.waitFor(START_BYTE);
        byte read = (byte) inSignal.read();
        System.out.println(ByteUtil.toBitString(new byte[] {read}));
        if (read != START_BYTE) {
          continue;
        }
        System.out.println("Signal!");
        try {
          String text = in.readUTF();
          System.out.println("Received: " + text);
        } catch (Exception e) {
          System.out.println("Error while receiving: " + e.getMessage() + " ("
              + e.getClass().getSimpleName() + ")");
        }
        System.out.println();
      }
    } catch (IOException e) {
      System.out.println("SEVERE: Receiver throws IOException! Thread stopped.");
      e.printStackTrace();
    } finally {
      try {
        inRecorder.close();
        in.close();
      } catch (IOException e) {
        System.out.println("Error while closing stream!");
      }
      running = false;
    }
  }

  public static void main(String[] args) throws IOException {
    Terminal terminal = new Terminal();
    terminal.start();

    Scanner scanner = new Scanner(System.in);
    scanner.useDelimiter("\n\n");
    while (true) {
      String text = scanner.next();
      if (text.trim().isEmpty()) {
        continue;
      } else if (text.trim().equals("/stop")) {
        break;
      }
      terminal.send(text);
    }
    System.out.println("Stopping");
    terminal.stop();
    scanner.close();
  }

}
