package de.mytfg.uac.util;

public class ByteUtil {

  public static byte getBit(byte[] data, long pos) {
    int posByte = (int) (pos / 8);
    int posBit = (int) (pos % 8);
    byte valByte = data[posByte];
    byte valBit = (byte) (valByte >> (8 - (posBit + 1)) & 0x0001);
    return valBit;
  }

  public static void setBit(byte[] data, long pos, byte val) {
    int posByte = (int) (pos / 8);
    int posBit = (int) (pos % 8);
    byte oldByte = data[posByte];
    oldByte = (byte) (((0xFF7F >> posBit) & oldByte) & 0x00FF);
    byte newByte = (byte) ((val << (8 - (posBit + 1))) | oldByte);
    data[posByte] = newByte;
  }
  
  public static String toBitString(byte[] data) {
    StringBuilder sb = new StringBuilder();
    for(int i = 0; i < data.length * 8; i++) {
      sb.append(getBit(data, i));
    }
    return sb.toString();
  }

}
