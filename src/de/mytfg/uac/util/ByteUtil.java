package de.mytfg.uac.util;

import java.nio.ByteBuffer;

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

  public static byte[] toByteArray(double value) {
    byte[] bytes = new byte[8];
    ByteBuffer.wrap(bytes).putDouble(value);
    return bytes;
  }

  public static double toDouble(byte[] bytes) {
    return ByteBuffer.wrap(bytes).getDouble();
  }

  public static String toBitString(byte[] data) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < data.length * 8; i++) {
      sb.append(getBit(data, i));
    }
    return sb.toString();
  }
  
  public static byte[] toByteArray(String bits) {
    int byteLength = bits.length() / 8;
    if(bits.length() % 8 != 0) {
      byteLength += 1;
    }
    byte[] bytes = new byte[byteLength];
    for(int i = 0; i < bits.length(); i++) {
      setBit(bytes, i, (byte) (bits.charAt(i) == '1' ? 1 : 0));
    }
    return bytes;
  }

}
