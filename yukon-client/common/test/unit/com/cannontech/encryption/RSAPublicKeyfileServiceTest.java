package com.cannontech.encryption;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;

import org.junit.jupiter.api.Test;

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
            assertEquals(true,secret.equals(new String(decBytes)));
            
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
