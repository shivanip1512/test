package com.cannontech.openadr.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;

import org.bouncycastle.util.encoders.Hex;

public class CertificateUtil {
    public static String getThumbprint(Certificate certificate, int printLength) 
            throws NoSuchAlgorithmException, CertificateEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] encoded = certificate.getEncoded();
        md.update(encoded);
        
        String hexString = new String(Hex.encode(md.digest()));
        
        if (hexString.length() < printLength) {
            return "";
        }
        
        return hexString.substring(hexString.length() - printLength);
    }
}