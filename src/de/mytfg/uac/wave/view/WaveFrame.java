package de.mytfg.uac.wave.view;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import de.mytfg.uac.wave.stream.InputWave;

public class WaveFrame extends JFrame {

  private JPanel contentPane;

  /**
   * Create the frame.
   */
  public WaveFrame(InputWave[] wave) {
    setTitle("Wave Viewer");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setBounds(100, 100, 1248, 400);
    
    contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    contentPane.setLayout(new BorderLayout(0, 0));
    setContentPane(contentPane);
    
    WavePanel panel = new WavePanel(wave);
    contentPane.add(panel);
  }

}
