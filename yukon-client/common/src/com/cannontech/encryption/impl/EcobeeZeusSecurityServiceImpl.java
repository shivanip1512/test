package com.cannontech.encryption.impl;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Optional;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.database.db.security.EncryptionKey;
import com.cannontech.dr.ecobee.message.ZeusEncryptionKey;
import com.cannontech.encryption.CryptoException;
import com.cannontech.encryption.CryptoUtils;
import com.cannontech.encryption.EcobeeZeusSecurityService;
import com.cannontech.encryption.EncryptedRouteDao;
import com.cannontech.encryption.EncryptionKeyType;

public class EcobeeZeusSecurityServiceImpl implements EcobeeZeusSecurityService {

    @Autowired private EncryptedRouteDao encryptedRouteDao;

    private Logger log = YukonLogManager.getLogger(EcobeeZeusSecurityServiceImpl.class);

    public ZeusEncryptionKey generateZeusEncryptionKey() throws CryptoException {

        try {
            char[] password = CryptoUtils.getSharedPasskey();
            AESPasswordBasedCrypto encrypter = new AESPasswordBasedCrypto(password);
            // GENERATE THE PUBLIC/PRIVATE RSA KEY PAIR
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048, new SecureRandom());

            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            PublicKey publicKey = keyPair.getPublic();

            byte publicKeyEncoded[] = publicKey.getEncoded();
            byte[] publicKeyEncoded64 = Base64.encodeBase64(publicKeyEncoded);
            String publicStringKey = new String(publicKeyEncoded64);

            String encryptedpublicKeyValue = new String(Hex.encodeHex(encrypter.encrypt(publicStringKey.getBytes())));

            PrivateKey privateKey = keyPair.getPrivate();

            byte privateKeyEncoded[] = privateKey.getEncoded();
            byte[] privateKeyEncoded64 = Base64.encodeBase64(privateKeyEncoded);
            String privateStringKey = new String(privateKeyEncoded64);

            String encryptedPrivateKeyValue = new String(Hex.encodeHex(encrypter.encrypt(privateStringKey.getBytes())));
            Instant instant = Instant.now();
            encryptedRouteDao.saveOrUpdateEncryptionKey(encryptedPrivateKeyValue, encryptedpublicKeyValue, EncryptionKeyType.EcobeeZeus, instant);

            return new ZeusEncryptionKey(privateStringKey, publicStringKey, instant);

        } catch (Exception ex) {
            log.error("Error while generating EcobeeZeus encryption keys. ", ex);
        }
        return null;
    }

    public ZeusEncryptionKey getZeusEncryptionKey() throws Exception {
        ZeusEncryptionKey zeusEncryptionKey = null;
        Optional<EncryptionKey> encryptionKey = encryptedRouteDao.getEncryptionKey(EncryptionKeyType.EcobeeZeus);
        if (encryptionKey.isPresent()) {
            char[] sharedPasskey = CryptoUtils.getSharedPasskey();
            AESPasswordBasedCrypto aes = new AESPasswordBasedCrypto(sharedPasskey);
            zeusEncryptionKey = new ZeusEncryptionKey(aes.decryptHexStr(encryptionKey.get().getPrivateKey()),
                                                      aes.decryptHexStr(encryptionKey.get().getPublicKey()),
                                                      encryptionKey.get().getTimestamp());
        }
        return zeusEncryptionKey;
    }
}
