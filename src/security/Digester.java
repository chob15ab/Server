package security;

import logic.ConfigLoader;
import service.DBWrapper;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import java.io.IOException;
import java.security.MessageDigest;

//TODO: Comments and documentation needed.
public class Digester {

    //TODO: Set SALT and KEY in config-file.
    private final static String SALT = ConfigLoader.HASH_SALT;
    private final static String KEY = ConfigLoader.ENCRYPT_KEY;
    private static MessageDigest digester;


    //Opretter objekt, som benyttes af MD5 (hashfunktion)
    static {
        try {
            digester = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Hash string with MD5 hashing
     * @param str
     * @return MD5 hash of string
     */

    //Hashing påbegyndes
    public static String hash(String str) {
        if (str == null || str.length() == 0) {
            throw new IllegalArgumentException("Error");
        }
        return Digester._hash(str);
    }

    //Hashing + SALT påbegyndes
    public static String hashWithSalt(String str){
        if (str == null || str.length() == 0) {
            throw new IllegalArgumentException("Error");
        }

        str = str + Digester.SALT;

        return Digester._hash(str);
    }
    //konventerer hashværdien til hexidecimaler
    private static String _hash(String str){
        digester.update(str.getBytes());
        byte[] hash = digester.digest();
        StringBuffer hexString = new StringBuffer();
        for (byte aHash : hash) {
            if ((0xff & aHash) < 0x10) {
                hexString.append("0" + Integer.toHexString((0xFF & aHash)));
            } else {
                hexString.append(Integer.toHexString(0xFF & aHash));
            }
        }
        return hexString.toString();
    }

    public static String encrypt(String s) {

        String encrypted_string = s;

        if(ConfigLoader.ENCRYPTION.equals("TRUE")){
            encrypted_string = base64Encode(xorWithKey(encrypted_string.getBytes(), KEY.getBytes()));
        }
        return encrypted_string;
    }

    public static String decrypt(String s) {

        String decrypted_string = s;

        if(ConfigLoader.ENCRYPTION.equals("TRUE")) {
            decrypted_string = new String(xorWithKey(base64Decode(s), KEY.getBytes()));
        }
        return decrypted_string;
    }

    //
    private static byte[] xorWithKey(byte[] a, byte[] key) {
        byte[] out = new byte[a.length];
        for (int i = 0; i < a.length; i++) {
            out[i] = (byte) (a[i] ^ key[i%key.length]);
        }
        return out;
    }

    //Metode til at dekryptering
    private static byte[] base64Decode(String s) {
        try {
            BASE64Decoder d = new BASE64Decoder();
            return d.decodeBuffer(s);
        } catch (IOException e) {throw new RuntimeException(e);}
    }

    //metode til kryptering
    private static String base64Encode(byte[] bytes) {
        BASE64Encoder enc = new BASE64Encoder();
        return enc.encode(bytes).replaceAll("\\s", "");

    }

    public static String GenerateRandomString(int length) {
        char[] chars = "abcdefghijklmnopqrstuvwxyz-ABCDEFGHIJKLMNOPQRSTUVXYZ".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        return sb.toString();
    }

    public static String GetSessionValue(String sessionId)  {
        Map<String, String> params = new HashMap();
        params.put("sessionId", sessionId);
        String[] attr = new String[1];
        attr[0] = "content";
        try {
            ResultSet rs = DBWrapper.getRecords("sessions", attr, params, null, 1);
            while (rs.next()) {
                return rs.getString("content");
            }
        } catch (SQLException ex) {
            //TODO: Optionally add error message here
        }
        return null;
    }
}