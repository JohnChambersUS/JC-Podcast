package us.johnchambers.podcast.misc;

import java.util.UUID;

/**
 * Created by johnchambers on 1/7/18.
 */

public class HashMaker {

    public String md5(String sourceValue) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(sourceValue.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }

    public String uuid(String seed) {
        return UUID.nameUUIDFromBytes(seed.getBytes()).toString();
    }



}
