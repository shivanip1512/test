package com.cannontech.encryption;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import com.cannontech.common.util.BootstrapUtils;

public class KeyfileTest {
    private static final File keysFolder = new File(BootstrapUtils.getKeysFolder());
    private static final File masterCfgCryptoFile = new File(keysFolder,"masterConfigKeyfile.dat");

    @Test
    // Generates random keys and tests them all to check for duplicates
    public void test_GenerateRandomPasskeyRandomness() {
        
        // 32 should not generate a collision within 100 iterations
        int passkeySize = 32; 
        int testIterations = 100;
        
        List<String> generatedKeys = new ArrayList<String>();
        String generatedStr;
        
        for (int i=0;i<testIterations;i++) {
            generatedStr = new String(CryptoUtils.generateRandomPasskey(passkeySize));
            for(String testStr : generatedKeys) {
                Assert.assertEquals(false, generatedStr.equals(testStr));
            }
            generatedKeys.add(generatedStr);
        }
    }
    
    @Test
    public void test_GenerateRandomPasskey() {
        int testIterations = 10;
        int passkeyLength = 128;
        char[] generatedPasskey = null;
        String generatedPasskeyStr = null;

        for (int i=0;i<testIterations;i++) {
            generatedPasskey = CryptoUtils.generateRandomPasskey(passkeyLength);
            Assert.assertNotNull(generatedPasskey);
            Assert.assertEquals(passkeyLength,generatedPasskey.length);
            generatedPasskeyStr = new String(generatedPasskey);
            Assert.assertEquals(false,generatedPasskeyStr.equals(""));
        }

    }

    @Test
    public void test_GenerateRandomPasskeyZeroLength() {
        char[] generatedPasskey = null;
        String generatedPasskeyStr = null;
        int passkeyLength = 0;

        generatedPasskey = CryptoUtils.generateRandomPasskey(passkeyLength);
        Assert.assertNotNull(generatedPasskey);
        Assert.assertEquals(passkeyLength,generatedPasskey.length);
        generatedPasskeyStr = new String(generatedPasskey);
        Assert.assertEquals(true,generatedPasskeyStr.equals(""));
    }

    @Test
    public void test_GenerateRandomPasskeyVariableLength() {
        int testIterations = 10;
        Random rand = new Random();
        char[] generatedPasskey = null;
        String generatedPasskeyStr = null;
        int passkeyLength = 128;

        for (int i=0;i<testIterations;i++) {
            passkeyLength = rand.nextInt(255)+1;
            generatedPasskey = CryptoUtils.generateRandomPasskey(passkeyLength);
            Assert.assertNotNull(generatedPasskey);
            Assert.assertEquals(passkeyLength,generatedPasskey.length);
            generatedPasskeyStr = new String(generatedPasskey);
            Assert.assertEquals(false,generatedPasskeyStr.equals(""));
        }
    }

    @Test
    public void test_MasterConfgCryptoFile() {
        try {
            char[] passkey = CryptoUtils.getPasskeyFromCryptoFile(masterCfgCryptoFile);
            Assert.assertNotNull(passkey);
        } catch (Exception e) {
            Assert.fail();
        } 
    }
}