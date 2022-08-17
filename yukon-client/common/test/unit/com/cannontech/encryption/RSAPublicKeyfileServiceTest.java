package com.cannontech.encryption;

import java.io.File;

import junit.framework.Assert;

import org.junit.Test;

import com.cannontech.encryption.impl.RSAKeyfileServiceImpl;

public class RSAPublicKeyfileServiceTest {
    @Test
    public void test_RSAPublicKeyfileService() throws CryptoException {
        try {
            String secret = "Hello world!!";
            RSAKeyfileService service = new RSAKeyfileServiceImpl();
            File file = File.createTempFile("test_ImportKeyFile", "tmp");
            service.generateNewKeyPair();
            
            service.createFile(file, secret.getBytes(),service.getPublicKey().getEncoded());
            byte[] decBytes = service.decryptAndExtractData(file);
            Assert.assertEquals(true,secret.equals(new String(decBytes)));
            
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }
}
