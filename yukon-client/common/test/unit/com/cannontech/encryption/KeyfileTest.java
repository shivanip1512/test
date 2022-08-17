package com.cannontech.encryption;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jdom2.JDOMException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.cannontech.common.util.BootstrapUtils;

public class KeyfileTest {
    private static final File keysFolder = BootstrapUtils.getKeysFolder().toFile();
    private static final File masterCfgCryptoFile = new File(keysFolder,"masterConfigKeyfile.dat");

    private File testFileGood; // Set before tests run
    private File testFileBad; // Set before tests run
    private static final String testFilePassKeyStr = "1r[cP7.=ui=u$Z}5ct2TC-:siZ1niLA3";

    @Before
    public void setup() {
        try {
            testFileGood = new File(getClass().getResource("testKeyfileGood.dat").toURI());
        } catch(URISyntaxException e) {
            testFileGood = new File(getClass().getResource("testKeyfileGood.dat").getPath());
        }

        try {
            testFileBad = new File(getClass().getResource("testKeyfileBad.dat").toURI());
        } catch(URISyntaxException e) {
            testFileBad = new File(getClass().getResource("testKeyfileBad.dat").getPath());
        }
    }
    
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
    
    @Test
    public void test_validPasskeyFileGetPasskey() {
        char[] passkey = null;
        try {
            passkey = CryptoUtils.getPasskeyFromCryptoFile(testFileGood);
            Assert.assertNotNull(passkey);
            String passkeyStr = new String(passkey);
            Assert.assertEquals(passkeyStr, testFilePassKeyStr);
        } catch (Exception e) {
            Assert.fail();
        } 
        Assert.assertNotNull(passkey);
    }

    @Test
    public void test_InvalidPasskeyFile() throws IOException, JDOMException {
        char[] passkey = null;
        try {
            passkey = CryptoUtils.getPasskeyFromCryptoFile(testFileBad);
            Assert.fail();
        } catch (CryptoException e) {
            /* Test Pass. */
        }
        Assert.assertNull(passkey);
    }
}