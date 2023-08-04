package hiforce.render.protocol.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Rocky Yu
 * @since 2019/8/3
 */
@Slf4j
public class MD5 {

    public static final String ENCODE = "GBK";

    private static char[] digits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private static Map<Character, Integer> rDigits = new HashMap<Character, Integer>(16);

    static {
        for (int i = 0; i < digits.length; ++i) {
            rDigits.put(digits[i], i);
        }
    }

    private static MD5 me = new MD5();
    private MessageDigest mHasher;

    private MD5() {
        try {
            mHasher = MessageDigest.getInstance("md5");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    public static MD5 getInstance() {
        return me;
    }


    public String getMD5String(String content) {
        return bytes2string(hash(content));
    }


    public String getMD5String(byte[] content) {
        return bytes2string(hash(content));
    }


    public byte[] getMD5Bytes(byte[] content) {
        return hash(content);
    }


    /**
     * @param str
     * @return md5 byte[16]
     */
    public byte[] hash(String str) {
        try {
            byte[] bt = mHasher.digest(str.getBytes(ENCODE));
            if (null == bt || bt.length != 16) {
                throw new IllegalArgumentException("md5 need");
            }
            return bt;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("unsupported utf-8 encoding", e);
        }
    }


    /**
     * @return md5 byte[16]
     */
    public byte[] hash(byte[] data) {
        byte[] bt = mHasher.digest(data);
        if (null == bt || bt.length != 16) {
            throw new IllegalArgumentException("md5 need");
        }
        return bt;
    }


    /**
     * @return
     */
    public String bytes2string(byte[] bt) {
        int l = bt.length;

        char[] out = new char[l << 1];

        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = digits[(0xF0 & bt[i]) >>> 4];
            out[j++] = digits[0x0F & bt[i]];
        }
        return new String(out);
    }
}
