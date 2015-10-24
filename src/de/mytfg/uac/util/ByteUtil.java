package de.mytfg.uac.util;

public class ByteUtil {

  public static byte getBit(byte[] data, long pos) {
    int posByte = (int) (pos / 8);
    int posBit = (int) (pos % 8);
    byte valByte = data[posByte];
    return getBit(valByte, posBit);
  }
  
  public static byte getBit(byte data, int pos) {
    return (byte) (data >> (8 - (pos + 1)) & 0x0001);
  }

  public static void setBit(byte[] data, long pos, byte val) {
    int posByte = (int) (pos / 8);
    int posBit = (int) (pos % 8);
    byte oldByte = data[posByte];
    data[posByte] = setBit(oldByte, posBit, val);
  }
  
  public static byte setBit(byte data, long pos, byte val) {
    data = (byte) (((0xFF7F >> pos) & data) & 0x00FF);
    return (byte) ((val << (8 - (pos + 1))) | data);
  }
  
  public static String toBitString(byte[] data) {
    StringBuilder sb = new StringBuilder();
    for(int i = 0; i < data.length * 8; i++) {
      sb.append(getBit(data, i));
    }
    return sb.toString();
  }

}
