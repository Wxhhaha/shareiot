package com.wxh.shareiot;

import com.wxh.shareiot.ble.BleDataConvertUtil;
import com.wxh.shareiot.ble.CRC8Util;

import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
//        assertEquals(4, 2 + 2);
        //"A10E0211C1F55542FBA8524182CD19C3A2D1"
        String hexStr = "A1080A015C46B0166AEF";
//       // short value = Integer.valueOf(hexStr,16).shortValue();
//        System.out.println(Integer.toHexString(0xff &CRC8Util.calcCrc8(BleDataConvertUtil.HexString2Bytes(hexStr))));
//        System.out.println(CRC8Util.ncalcCrc8(BleDataConvertUtil.HexString2Bytes(hexStr)));

     //   byte[] tempByte = {(byte) 0xec, 0x51};
//        getFloat(tempByte);
//        Byte4ToFloat.byte2Float(tempByte);

        byte[] data = BleDataConvertUtil.HexString2Bytes(hexStr);
        byte[] v=new byte[1];
        v[0]= CRC8Util.calcCrc8(data);
        System.out.println(BleDataConvertUtil.byte2hex(v));

//        System.out.println(BleDataConvertUtil.bytesToInt(tempByte));
//        System.out.println(String.format("%016d",Integer.valueOf(Integer.toBinaryString(2))));

  //      System.out.println(BleDataConvertUtil.byte2hex(ControllerSettingData.getInstance().sendSettingData()));
    }

    public static float getFloat(byte[] a) {
        byte[] b = dataValueRollback(a);

//        int l;
//        l = b[0];
//        l &= 0xff;
//        l |= ((long) b[1] << 8);
//        l &= 0xffff;
//        l |= ((long) b[2] << 16);
//        l &= 0xffffff;
//        l |= ((long) b[3] << 24);
//        float f = Float.intBitsToFloat(l);
//        System.out.println(f);
//        return f;

        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.put(b);
        buffer.rewind();
        float f = buffer.getFloat();
//        System.out.println(f);
        return f;

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