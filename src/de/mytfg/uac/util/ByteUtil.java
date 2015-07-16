package de.mytfg.uac.util;

public class ByteUtil {

  public static byte getBit(byte[] data, long pos) {
    int posByte = (int) (pos / 8);
    int posBit = (int) (pos % 8);
    byte valByte = data[posByte];
    byte valBit = (byte) (valByte >> (8 - (posBit + 1)) & 0x0001);
    return valBit;
  }

}
