package com.cannontech.web.api.dr.ecobee;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.dr.ecobee.message.ZeusEncryptionKey;
import com.cannontech.encryption.EcobeeZeusSecurityService;
import com.cannontech.web.api.token.AuthenticationException;

import io.jsonwebtoken.Jwts;

public class EcobeeZeusJwtTokenAuthService {
    @Autowired private EcobeeZeusSecurityService ecobeeZeusSecurityService;

    public void validateEcobeeJwtToken(String jwtToken) {

        try {
            Jwts.parser().setSigningKey(getPublicKey())
                         .parseClaimsJws(jwtToken)
                         .getBody();
        } catch (Exception ex) {
            throw new AuthenticationException("Expired or invalid JWT token");
        }
    }

    /**
     * Convert String PublicKey to PublicKey.
     */
    private PublicKey getPublicKey() throws Exception {
        PublicKey pubKey = null;
        ZeusEncryptionKey encryptionKey = ecobeeZeusSecurityService.getZeusEncryptionKey();
        if (encryptionKey != null) {
            byte[] publicBytes = Base64.decodeBase64(encryptionKey.getPublicKey());
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            pubKey = keyFactory.generatePublic(keySpec);
        }
        return pubKey;
    }

    /**
     * Convert String Private to PrivateKey.
     */
    public PrivateKey getPrivateKey() throws Exception {
        PrivateKey privKey = null;
        ZeusEncryptionKey encryptionKey = ecobeeZeusSecurityService.getZeusEncryptionKey();
        if (encryptionKey != null) {
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(encryptionKey.getPrivateKey()));
            KeyFactory kf = KeyFactory.getInstance("RSA");
            privKey = kf.generatePrivate(keySpec);
        }

        return privKey;
    }
}
