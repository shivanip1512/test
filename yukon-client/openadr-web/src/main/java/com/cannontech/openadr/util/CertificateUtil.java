package com.cannontech.openadr.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;

public class CertificateUtil {
    public static String getThumbprint(Certificate certificate, int printLength) 
            throws NoSuchAlgorithmException, CertificateEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] encoded = certificate.getEncoded();
        md.update(encoded);
        byte[] digest = md.digest();
        
        String hexString = toHexString(digest);
        
        if (hexString.length() < printLength) {
            return "";
        }
        
        return hexString.substring(hexString.length() - printLength);
    }
    
    private static String toHexString(byte bytes[]) {
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', 
                            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

        StringBuffer buf = new StringBuffer(bytes.length * 2);

        for (int i = 0; i < bytes.length; ++i) {
            buf.append(hexDigits[(bytes[i] & 0xf0) >> 4]);
            buf.append(hexDigits[bytes[i] & 0x0f]);
        }

        return buf.toString();
    }
}
