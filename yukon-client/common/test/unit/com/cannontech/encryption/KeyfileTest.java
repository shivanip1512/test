package com.cannontech.encryption;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jdom2.JDOMException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.cannontech.common.util.BootstrapUtils;

public class KeyfileTest {
    private static final File keysFolder = BootstrapUtils.getKeysFolder().toFile();
    private static final File masterCfgCryptoFile = new File(keysFolder,"masterConfigKeyfile.dat");

    private File testFileGood; // Set before tests run
    private File testFileBad; // Set before tests run
    private static final String testFilePassKeyStr = "1r[cP7.=ui=u$Z}5ct2TC-:siZ1niLA3";

    @BeforeEach
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
                assertEquals(false, generatedStr.equals(testStr));
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
            assertNotNull(generatedPasskey);
            assertEquals(passkeyLength,generatedPasskey.length);
            generatedPasskeyStr = new String(generatedPasskey);
            assertEquals(false,generatedPasskeyStr.equals(""));
        }

    }

    @Test
    public void test_GenerateRandomPasskeyZeroLength() {
        char[] generatedPasskey = null;
        String generatedPasskeyStr = null;
        int passkeyLength = 0;

        generatedPasskey = CryptoUtils.generateRandomPasskey(passkeyLength);
        assertNotNull(generatedPasskey);
        assertEquals(passkeyLength,generatedPasskey.length);
        generatedPasskeyStr = new String(generatedPasskey);
        assertEquals(true,generatedPasskeyStr.equals(""));
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
            assertNotNull(generatedPasskey);
            assertEquals(passkeyLength,generatedPasskey.length);
            generatedPasskeyStr = new String(generatedPasskey);
            assertEquals(false,generatedPasskeyStr.equals(""));
        }
    }

    @Test
    public void test_MasterConfgCryptoFile() {
        try {
            char[] passkey = CryptoUtils.getPasskeyFromCryptoFile(masterCfgCryptoFile);
            assertNotNull(passkey);
        } catch (Exception e) {
            fail();
        } 
    }
    
    @Test
    public void test_validPasskeyFileGetPasskey() {
        char[] passkey = null;
        try {
            passkey = CryptoUtils.getPasskeyFromCryptoFile(testFileGood);
            assertNotNull(passkey);
            String passkeyStr = new String(passkey);
            assertEquals(passkeyStr, testFilePassKeyStr);
        } catch (Exception e) {
            fail();
        } 
        assertNotNull(passkey);
    }

    @Test
    public void test_InvalidPasskeyFile() throws IOException, JDOMException {
        char[] passkey = null;
        try {
            passkey = CryptoUtils.getPasskeyFromCryptoFile(testFileBad);
            fail();
        } catch (CryptoException e) {
            /* Test Pass. */
        }
        assertNull(passkey);
    }

    @Test
    public void test_Passkey_with_null_File() throws IOException, JDOMException {
        char[] passkey = CryptoUtils.getPasskeyFromCryptoFile(null);
        assertNull(passkey);
    }
}