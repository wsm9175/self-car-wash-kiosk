package com.lodong.android.selfcarwashkiosk.outApp;

import android.util.Log;

import java.security.InvalidParameterException;
import java.text.NumberFormat;

public class Util {
    public static byte STX = (byte)0x02;
    public static byte ETX = (byte)0x03;
    public static byte EOT = (byte)0x04;
    public static byte ENQ = (byte)0x05;
    public static byte ACK = (byte)0x06;
    public static byte CR  = (byte)0x0D;
    public static byte DLE = (byte)0x10;
    public static byte NAK = (byte)0x15;
    public static byte ESC = (byte)0x1B;
    public static byte FS  = (byte)0x1C;

    public static void byteTo20ByteLog(byte[] bMsg, String msg) {
        for (int i = 0; i < (bMsg.length / 20) + 1; i++) {
            byte[] sub_packet;
            if (i == bMsg.length / 20) {
                sub_packet = copyOfRange(bMsg, i * 20, bMsg.length - (20 * i));
            } else {
                sub_packet = copyOfRange(bMsg, i * 20, 20);
            }

            Log.e("KSCAT_INTENT_RESULT", msg + byteArrayToHex(sub_packet));

            try {
                Thread.sleep(10);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static byte[] copyOfRange(byte[] original, int from, int to) {
        int newLength = to;
        if (to < 0)
            throw new IllegalArgumentException(from + " > " + to);
        byte[] copy = new byte[to];
        System.arraycopy(original, from, copy, 0,
                Math.min(original.length - from, newLength));
        return copy;
    }

    public static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder();
        for (final byte b : a)
            sb.append(String.format("%02x ", b & 0xff));
        return sb.toString();
    }

    public static class HexDump {
        private final static char[] HEX_DIGITS = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
        };

        public static String dumpHexString(byte[] array) {
            return dumpHexString(array, 0, array.length);
        }

        public static String dumpHexString(byte[] array, int offset, int length) {
            StringBuilder result = new StringBuilder();

            byte[] line = new byte[8];
            int lineIndex = 0;

            for (int i = offset; i < offset + length; i++) {
                if (lineIndex == line.length) {
                    for (int j = 0; j < line.length; j++) {
                        if (line[j] > ' ' && line[j] < '~') {
                            result.append(new String(line, j, 1));
                        } else {
                            result.append(".");
                        }
                    }

                    result.append("\n");
                    lineIndex = 0;
                }

                byte b = array[i];
                result.append(HEX_DIGITS[(b >>> 4) & 0x0F]);
                result.append(HEX_DIGITS[b & 0x0F]);
                result.append(" ");

                line[lineIndex++] = b;
            }

            for (int i = 0; i < (line.length - lineIndex); i++) {
                result.append("   ");
            }
            for (int i = 0; i < lineIndex; i++) {
                if (line[i] > ' ' && line[i] < '~') {
                    result.append(new String(line, i, 1));
                } else {
                    result.append(".");
                }
            }

            return result.toString();
        }

        public static String toHexString(byte b) {
            return toHexString(toByteArray(b));
        }

        public static String toHexString(byte[] array) {
            return toHexString(array, 0, array.length);
        }

        public static String toHexString(byte[] array, int offset, int length) {
            char[] buf = new char[length * 2];

            int bufIndex = 0;
            for (int i = offset; i < offset + length; i++) {
                byte b = array[i];
                buf[bufIndex++] = HEX_DIGITS[(b >>> 4) & 0x0F];
                buf[bufIndex++] = HEX_DIGITS[b & 0x0F];
            }

            return new String(buf);
        }

        public static String toHexString(int i) {
            return toHexString(toByteArray(i));
        }

        public static String toHexString(short i) {
            return toHexString(toByteArray(i));
        }

        public static byte[] toByteArray(byte b) {
            byte[] array = new byte[1];
            array[0] = b;
            return array;
        }

        public static byte[] toByteArray(int i) {
            byte[] array = new byte[4];

            array[3] = (byte) (i & 0xFF);
            array[2] = (byte) ((i >> 8) & 0xFF);
            array[1] = (byte) ((i >> 16) & 0xFF);
            array[0] = (byte) ((i >> 24) & 0xFF);

            return array;
        }

        public static byte[] toByteArray(short i) {
            byte[] array = new byte[2];

            array[1] = (byte) (i & 0xFF);
            array[0] = (byte) ((i >> 8) & 0xFF);

            return array;
        }

        private static int toByte(char c) {
            if (c >= '0' && c <= '9')
                return (c - '0');
            if (c >= 'A' && c <= 'F')
                return (c - 'A' + 10);
            if (c >= 'a' && c <= 'f')
                return (c - 'a' + 10);

            throw new InvalidParameterException("Invalid hex char '" + c + "'");
        }

        public static byte[] hexStringToByteArray(String hexString) {
            int length = hexString.length();
            byte[] buffer = new byte[length / 2];

            for (int i = 0; i < length; i += 2) {
                buffer[i / 2] = (byte) ((toByte(hexString.charAt(i)) << 4) | toByte(hexString
                        .charAt(i + 1)));
            }

            return buffer;
        }
    }

    public static byte[] insertBytes( byte[] src, int srcPosition, byte target )
    {
        return insertBytes( src, srcPosition, new byte[]{ target } );
    }

    public static byte[] insertBytes( byte[] src, int srcPosition, byte[] target )
    {
        if( src == null )
            return target;

        if( target == null )
            return src;

        byte[] bytes = new byte[ src.length + target.length ];

        System.arraycopy( src, 0, bytes, 0, srcPosition );
        System.arraycopy( target, 0, bytes, srcPosition, target.length );
        System.arraycopy( src, srcPosition, bytes, srcPosition + target.length, src.length - srcPosition );

        return bytes;
    }

    public static byte[] recreatePacketByApprovalFormat(byte[] packet)
    {
        packet = insertBytes(packet, packet.length, ETX);
        packet = insertBytes(packet, packet.length, CR);
        packet = insertBytes(packet, 0, STX);
        packet = insertBytes(packet, 0, String.format("%04d", packet.length).getBytes());

        return packet;
    }

    public static byte[] recreatePacketBySubFuncFormat(byte[] packet)
    {
        packet = insertBytes(packet, packet.length, ETX);
        packet = insertBytes(packet, packet.length, CR);
        packet = insertBytes(packet, 0, String.format("%04d", packet.length).getBytes());
        packet = insertBytes(packet, 0, STX);

        return packet;
    }

//    public static byte[] recreatePacketByApprovalFormat(byte[] packet){
//        packet = insertBytes(packet, packet.length, ETX);
//        packet = insertBytes(packet, packet.length, CR);
//        packet = insertBytes(packet, 0, STX);
//        packet = insertBytes(packet, 0, intToByte(packet.length));
//
//        // Log.e(TAG,"recreatePacketByApprovalFormat : \n" + Utils.HexDump.dumpHexString(packet));
//
//        return packet;
//    }

    public static byte[] intToByte(int value)
    {
        byte first	= (byte) ( (value & 0xFF000000 ) >>> 24 );
        byte second	= (byte) ( (value & 0x00FF0000 ) >>> 16 );
        byte third	= (byte) ( (value & 0x0000FF00 ) >>> 8 );
        byte forth	= (byte) ( (value & 0x000000FF ) );

        byte[] result = new byte[] { first, second, third, forth } ;

        return result;
    }

    public static String formatCurrency(String numberStr)
    {
        long number = 0;
        try
        {
            number = Long.parseLong(numberStr);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return NumberFormat.getInstance().format(number);
    }

    public static String stringToAmount(String str, int length)
    {
        int strLength = str.getBytes().length;
        String temp = "";

        for (int i = strLength; i < length; i++)
        {
            temp = temp + "0";
        }

        temp = temp + str;
        return temp;
    }


    public static class CalcTax
    {
        public long mTotAmt = 0;                   // 거래총액
        public long mSupplyAmt = 0;                // 공급가액
        public long mVAT = 0;                      // 부가세
        public long mServiceAmt = 0;               // 봉사료
        public long mTaxfreeAmt = 0;               // 면세금액
        public double mVATRate;                    // 부가세 비율
        public double mServiceRate;                // 봉사료 비율

        public long getServiceAmt() { return this.mServiceAmt; }         // 봉사료
        public long getVAT()
        {
            return this.mVAT;
        }                       // 부가세
        public long getSupplyAmt() { return this.mSupplyAmt; }           // 공급금액
        public long getTaxfreeAmt()
        {
            return this.mTaxfreeAmt;
        }         // 면세금액

        // [부가세 타입]
        public enum TaxType
        {
            // 포함, 모름, 복합, 없음
            Added, Spacial, Complex, None;
        }

        //////////////////////////////////////////////////
        // 1. 총금액에서 봉사료를 발라낸다.
        // 2. 총금액 - 봉사료에서 부가세를 발라낸다.
        // 3. 총금액 - 봉사료 - 부가세에서 공급금액을 발라낸다.
        // 4. 부가세 여부에 따라 면세금액을 발라낸다.
        //////////////////////////////////////////////////

        // 새로 추가한 부가세, 봉사료 비율에 대한 설정값
        public void setConfig(long totAmt, double VATRate, double serviceRate)
        {
            mTotAmt = totAmt;
            mVATRate = VATRate;
            mServiceRate = serviceRate;

            calcAmount();
        }

        public void calcAmount()
        {
            // 순서대로 써야 함. 따로따로 쓰지 말 것.
            calcService();
            calcVAT();
        }

        private long calcService()
        {
            long result = 0;

            if (mServiceRate > 0)
            {
                double p = 1 + (mServiceRate / 100.0);
                double r = (mTotAmt / p);
                result = mTotAmt - Math.round(r);
            }

            mServiceAmt = result;

            return result;

        }

        private long calcVAT()
        {
            long result = 0;

            if (mVATRate > 0)
            {
                double p = 1 + (mVATRate / 100.0);
                double r = ((mTotAmt - mServiceAmt) / p);
                result = (mTotAmt - mServiceAmt) - Math.round(r);
            }
            mVAT = result;

            if (mVAT == 0)
                mTaxfreeAmt = mSupplyAmt = mTotAmt - mServiceAmt - mVAT;
            else
                mSupplyAmt = mTotAmt - mServiceAmt - mVAT;

            return result;
        }
    }

    public static byte[] toByte(String hexString)
    {
        int len = hexString.length() / 2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++)
            result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2), 16).byteValue();
        return result;
    }

}
