package com.wxh.shareiot.utils;

import com.wxh.shareiot.ble.BleDataConvertUtil;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class Byte4ToFloat {

    public static float byte2Float(String hex) {
        byte[] bytes = BleDataConvertUtil.HexString2Bytes(hex);
        return byte2Float(bytes);
    }

    public static float byte2Float(byte[] a) {
        byte[] b = dataValueRollback(a);
//        int accum = 0;
//        accum= accum|(b[0] & 0xff) << 0;
//        accum= accum|(b[1] & 0xff) << 8;
//        accum= accum|(b[2] & 0xff) << 16;
//        accum= accum|(b[3] & 0xff) << 24;
//        return Float.intBitsToFloat(accum);

        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.put(b);
        buffer.rewind();
//        System.out.println(buffer.getFloat());
        return buffer.getFloat();

    }

    public static float byte2Int(byte[] a) {
        byte[] b = dataValueRollback(a);
//        int accum = 0;
//        accum= accum|(b[0] & 0xff) << 0;
//        accum= accum|(b[1] & 0xff) << 8;
//        accum= accum|(b[2] & 0xff) << 16;
//        accum= accum|(b[3] & 0xff) << 24;
//        return Float.intBitsToFloat(accum);

        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.put(b);
        buffer.rewind();
//        System.out.println(buffer.getFloat());
        return buffer.getFloat();

    }

    //数值反传
    private static byte[] dataValueRollback(byte[] data) {
        ArrayList<Byte> al = new ArrayList<Byte>();
        for (int i = data.length - 1; i >= 0; i--) {
            al.add(data[i]);
        }

        byte[] buffer = new byte[al.size()];
        for (int i = 0; i <= buffer.length - 1; i++) {
            buffer[i] = al.get(i);
        }
        return buffer;
    }
}
