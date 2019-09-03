package com.cannontech.encryption.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.apache.commons.codec.binary.Hex;
import org.apache.logging.log4j.Logger;
import org.jdom2.JDOMException;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.database.db.security.EncryptionKey;
import com.cannontech.encryption.CryptoException;
import com.cannontech.encryption.CryptoUtils;
import com.cannontech.encryption.EncryptedRouteDao;
import com.cannontech.encryption.EncryptionKeyType;
import com.cannontech.encryption.ItronSecurityKeyPair;
import com.cannontech.encryption.ItronSecurityService;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.KeyPair;

public class ItronSecurityServiceImpl implements ItronSecurityService{

    private Logger log = YukonLogManager.getLogger(ItronSecurityServiceImpl.class);
    @Autowired private EncryptedRouteDao encryptedRouteDao;
    
    @Override
    public Instant generateItronSshRsaPublicPrivateKeys(String comment, String passPhrase) {
        Instant timestamp = Instant.now();
        JSch jsch = new JSch();
        ByteArrayOutputStream privateKeyBuff = new ByteArrayOutputStream(2048);
        ByteArrayOutputStream publicKeyBuff = new ByteArrayOutputStream(2048);
        
        try {
            KeyPair keyPair = KeyPair.genKeyPair(jsch, KeyPair.RSA);
            if (passPhrase == null || passPhrase.isEmpty()) {
                keyPair.writePrivateKey(privateKeyBuff);
            } else {
                log.debug("Private Key password provided, encrypting private key");
                keyPair.writePrivateKey(privateKeyBuff, passPhrase.getBytes());
            }
            keyPair.writePublicKey(publicKeyBuff, comment);
            System.out.println("Finger print: " + keyPair.getFingerPrint());
            keyPair.dispose();
        } catch (Exception e) {
            System.out.println(e);
        }
        
        log.debug("Time Stamp: " + timestamp.toString());
        log.debug("Public Key: " + publicKeyBuff.toString());
        log.debug("Private Key: " + privateKeyBuff.toString());
        
        try {
            savePublicAndPrivateItronKey(privateKeyBuff.toString(), publicKeyBuff.toString(), timestamp);
        } catch (CryptoException e) {
            log.warn("caught exception in generateItronSshRsaPublicPrivateKeys", e);
        } catch (IOException e) {
            log.warn("caught exception in generateItronSshRsaPublicPrivateKeys", e);
        } catch (JDOMException e) {
            log.warn("caught exception in generateItronSshRsaPublicPrivateKeys", e);
        }
        
        return timestamp;
    }
    
    @Override
    public ItronSecurityKeyPair getItronSshRsaKeyPair() throws Exception {
        //TODO Get global Itron private key encryption variable, else set to null. Currently setting to NULL as default
        String privateKeyPassword = null;
        
        char[] password = CryptoUtils.getSharedPasskey();
        AESPasswordBasedCrypto encrypter = new AESPasswordBasedCrypto(password);
        Optional<EncryptionKey> encryptionKey = encryptedRouteDao.getEncryptionKey(EncryptionKeyType.Itron);
        String sshRsaPrivatekey = encrypter.decryptHexStr(encryptionKey.get().getPrivateKey());
        String sshRsaPublickey = encrypter.decryptHexStr(encryptionKey.get().getPublicKey());
        ItronSecurityKeyPair itronKeyPair;
        JSch jsch = new JSch();
        ByteArrayOutputStream privateKeyBuff = new ByteArrayOutputStream(2048);
        KeyPair keyPair = KeyPair.load(jsch, sshRsaPrivatekey.getBytes(), sshRsaPublickey.getBytes());
        // Change this check to see if its encrypted and there is a global password specified
        if(keyPair.isEncrypted()) {
            log.debug("Private Key is encrypted, attempting to decrypt");
            keyPair.decrypt(privateKeyPassword);
            // This checks if the decryption was successful, if not it will pass on the encrypted privateKey
            if(keyPair.isEncrypted()) {
                log.debug("Private Key decryption was not successful");
                itronKeyPair = new ItronSecurityKeyPair(sshRsaPrivatekey, sshRsaPublickey, true);
            }
            else {
                keyPair.writePrivateKey(privateKeyBuff);
                itronKeyPair = new ItronSecurityKeyPair(privateKeyBuff.toString(), sshRsaPublickey, false);
            }
        }
        else {
            itronKeyPair = new ItronSecurityKeyPair(sshRsaPrivatekey, sshRsaPublickey, false);
        }
        
        return itronKeyPair;
    }
    
    @Override
    public String getItronPublicSshRsaKey() throws Exception {
        log.info("Getting Itron Public Key");
        ItronSecurityKeyPair keyPair = getItronSshRsaKeyPair();
        log.info(keyPair.getPublicKey());
        return keyPair.getPublicKey();
    }
    
    @Override
    public String getItronPrivateSshRsaKey() throws Exception {
        log.info("Getting Itron Private Key");
        ItronSecurityKeyPair keyPair = getItronSshRsaKeyPair();
        log.info(keyPair.getPrivateKey());
        return keyPair.getPrivateKey();
    }
    
    @Override
    public Instant getItronKeyPairCreationTime() throws NoSuchElementException {
        Optional<EncryptionKey> encryptionKey = encryptedRouteDao.getEncryptionKey(EncryptionKeyType.Itron);
        return encryptionKey.map(EncryptionKey :: getTimestamp)
                            .orElseThrow();
    }
    
    private void savePublicAndPrivateItronKey(String privateKey, String publicKey, Instant timestamp)
            throws CryptoException, IOException, JDOMException {
        char[] password = CryptoUtils.getSharedPasskey();
        AESPasswordBasedCrypto encrypter = new AESPasswordBasedCrypto(password);
        String aesBasedCryptoPublicKey = new String(Hex.encodeHex(encrypter.encrypt(publicKey.getBytes())));
        log.debug("AES based crypto Public key [" + aesBasedCryptoPublicKey + "]");
        String aesBasedCryptoPrivateKey = new String(Hex.encodeHex(encrypter.encrypt(privateKey.getBytes())));
        log.debug("AES based crypto Private key [" + aesBasedCryptoPrivateKey + "]");
        encryptedRouteDao.saveOrUpdateEncryptionKey(aesBasedCryptoPrivateKey, aesBasedCryptoPublicKey, EncryptionKeyType.Itron, timestamp);
    }
}
