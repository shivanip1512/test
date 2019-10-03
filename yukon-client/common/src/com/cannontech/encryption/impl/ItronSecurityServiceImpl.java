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
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.impl.GlobalSettingDaoImpl;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.KeyPair;

public class ItronSecurityServiceImpl implements ItronSecurityService {

    private Logger log = YukonLogManager.getLogger(ItronSecurityServiceImpl.class);
    @Autowired private EncryptedRouteDao encryptedRouteDao;
    @Autowired private GlobalSettingDaoImpl globalSettingDaoImpl;
    
    @Override
    public Instant generateItronSshRsaPublicPrivateKeys(String comment) {
        Instant timestamp = Instant.now();
        JSch jsch = new JSch();
        ByteArrayOutputStream privateKeyBuff = new ByteArrayOutputStream(2048);
        ByteArrayOutputStream publicKeyBuff = new ByteArrayOutputStream(2048);
        String passPhrase = getPrivateKeyPassword();
        
        try {
            KeyPair keyPair = KeyPair.genKeyPair(jsch, KeyPair.RSA);
            if (passPhrase == null || passPhrase.isEmpty()) {
                keyPair.writePrivateKey(privateKeyBuff);
            } else {
                log.debug("Private Key password provided, encrypting private key");
                keyPair.writePrivateKey(privateKeyBuff, passPhrase.getBytes());
            }
            keyPair.writePublicKey(publicKeyBuff, comment);
            log.debug("Finger print: " + keyPair.getFingerPrint());
            keyPair.dispose();
        } catch (Exception e) {
            log.debug("Error generating key pair", e);
        }
        
        log.debug("Time Stamp: " + timestamp.toString());
        log.debug("Public Key: " + publicKeyBuff.toString());
        log.debug("Private Key: " + privateKeyBuff.toString());
        
        try {
            savePublicAndPrivateItronKey(privateKeyBuff.toString(), publicKeyBuff.toString(), timestamp);
        } catch (CryptoException | IOException | JDOMException e) {
            log.error("caught exception in generateItronSshRsaPublicPrivateKeys", e);
        }
        
        return timestamp;
    }
    
    @Override
    public ItronSecurityKeyPair getItronSshRsaKeyPair() throws ItronSecurityException { 
        log.debug("Retrieving Itron key pair");
        try {
            log.debug("Loading keys from database");
            Optional<EncryptionKey> encryptionKey = encryptedRouteDao.getEncryptionKey(EncryptionKeyType.Itron);
            if(encryptionKey.isEmpty()) {
                log.debug("Encryption Key is empty, generate a new key");
                throw new ItronSecurityException ("Empty Encryption Key, generate a new key");
            }
            
            log.debug("Building encrypter");
            char[] password = CryptoUtils.getSharedPasskey();
            AESPasswordBasedCrypto encrypter = new AESPasswordBasedCrypto(password);
            
            log.debug("Decrypting Itron keys");
            String sshRsaPrivateKey = encrypter.decryptHexStr(encryptionKey.get().getPrivateKey());
            String sshRsaPublicKey = encrypter.decryptHexStr(encryptionKey.get().getPublicKey());
            
            log.debug("Building keypair from keys");
            JSch jsch = new JSch();
            KeyPair keyPair = KeyPair.load(jsch, sshRsaPrivateKey.getBytes(), sshRsaPublicKey.getBytes());
            
            ItronSecurityKeyPair itronKeyPair;
            if(keyPair.isEncrypted()) {
                log.debug("Private Key is encrypted, retrieving private key password");
                String privateKeyPassword = getPrivateKeyPassword();
                log.debug("Attempting to decrypt private key");
                keyPair.decrypt(privateKeyPassword);
                // This checks if the decryption was successful, if not it will pass on the encrypted privateKey
                if(keyPair.isEncrypted()) {
                    log.debug("Private Key decryption was not successful");
                    itronKeyPair = new ItronSecurityKeyPair(sshRsaPrivateKey, sshRsaPublicKey, true);
                } else {
                    log.debug("Private Key decryption successful");
                    ByteArrayOutputStream privateKeyBuff = new ByteArrayOutputStream(2048);
                    keyPair.writePrivateKey(privateKeyBuff);
                    itronKeyPair = new ItronSecurityKeyPair(privateKeyBuff.toString(), sshRsaPublicKey, false);
                }
            } else {
                log.debug("Proceeding with unencrypted private key");
                itronKeyPair = new ItronSecurityKeyPair(sshRsaPrivateKey, sshRsaPublicKey, false);
            }
            
            keyPair.dispose();
            log.debug("Finished retrieving Itron key pair");
            return itronKeyPair;
        } catch (Exception e) {
            log.debug("Exception getting ItronSshRsaKeyPair", e);
            throw new ItronSecurityException("Error retrieving Itron keys.", e);
        }
    }
    
    @Override
    public String getItronPublicSshRsaKey() throws ItronSecurityException {
        log.info("Getting Itron Public Key");
        ItronSecurityKeyPair keyPair = getItronSshRsaKeyPair();
        log.info(keyPair.getPublicKey());
        return keyPair.getPublicKey();
    }
    
    @Override
    public String getItronPrivateSshRsaKey() throws ItronSecurityException {
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
    
    @Override
    public String encryptPrivateKey(String privateKey) throws ItronSecurityException {
        try {
            JSch jsch = new JSch();
            String passPhrase = getPrivateKeyPassword();
            if(passPhrase != null && !passPhrase.isBlank()) {
                        ByteArrayOutputStream privateKeyBuff = new ByteArrayOutputStream(2048);
                KeyPair keyPair = KeyPair.load(jsch, privateKey.getBytes(), privateKey.getBytes());
                keyPair.writePrivateKey(privateKeyBuff, passPhrase.getBytes());
                keyPair.dispose();
                return privateKeyBuff.toString();
            }
            return privateKey;
        } catch (Exception e) {
            throw new ItronSecurityException("Error retrieving Itron keys.", e);
        }
    }
    
    private String getPrivateKeyPassword() {
        return globalSettingDaoImpl.getString(GlobalSettingType.ITRON_SFTP_PRIVATE_KEY_PASSWORD);
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
