package com.cannontech.encryption.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Optional;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.joda.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.database.db.security.EncryptionKey;
import com.cannontech.dr.ecobee.message.ZeusEncryptionKey;
import com.cannontech.encryption.CryptoUtils;
import com.cannontech.encryption.EncryptedRouteDao;
import com.cannontech.encryption.EncryptionKeyType;

public class EcobeeZeusSecurityServiceImplTest {
	
    private EcobeeZeusSecurityServiceImpl mockEcobeeZeusSecurityServiceImpl;
    private EncryptedRouteDao mockEncryptedRouteDao;

    @BeforeEach
    public void setup() {
        mockEncryptedRouteDao = EasyMock.createMock(EncryptedRouteDao.class);
        mockEcobeeZeusSecurityServiceImpl = new EcobeeZeusSecurityServiceImpl();
        ReflectionTestUtils.setField(mockEcobeeZeusSecurityServiceImpl, "encryptedRouteDao", mockEncryptedRouteDao);
    }
    
    @Test
    public void test_generateEcobeeZeusKey() {
        try {
            
            EncryptedRouteDao encryptedRouteDao = EasyMock.createStrictMock(EncryptedRouteDao.class);
            Capture<String> privateKeyArg = new Capture<>();
            Capture<String> publicKeyArg = new Capture<>();
            Capture<EncryptionKeyType> keyType = new Capture<>();
            Capture<Instant> time = new Capture<>();
            
            encryptedRouteDao.saveOrUpdateEncryptionKey(EasyMock.capture(privateKeyArg),EasyMock.capture(publicKeyArg),EasyMock.capture(keyType),EasyMock.capture(time));
            EasyMock.expectLastCall().once();
            EasyMock.replay(encryptedRouteDao);

            // call the service method to be tested
            ZeusEncryptionKey zeusEncryptionKey = mockEcobeeZeusSecurityServiceImpl.generateZeusEncryptionKey();

            assertNotNull(zeusEncryptionKey.getPublicKey());
            assertNotNull(zeusEncryptionKey.getPrivateKey());

        } catch (Exception  e) {
            fail("Generate Ecobee Zeus Encryption Key Failed " + e);
        }
    }
    
	@Test
	public void test_getEcobeeZeusKey() {
		try {
			char[] password = CryptoUtils.getSharedPasskey();
			AESPasswordBasedCrypto encrypter = new AESPasswordBasedCrypto(password);

			// generate public/private key pair
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(2048, new SecureRandom());

			KeyPair keyPair = keyPairGenerator.generateKeyPair();

			PublicKey publicKey = keyPair.getPublic();
			String publicStringKey = new String(Base64.encodeBase64(publicKey.getEncoded()));
			String encryptedPublicKey = new String(Hex.encodeHex(encrypter.encrypt(publicStringKey.getBytes())));

			PrivateKey privateKey = keyPair.getPrivate();
			String privateStringKey = new String(Base64.encodeBase64(privateKey.getEncoded()));
			String encryptedPrivateKey = new String(Hex.encodeHex(encrypter.encrypt(privateStringKey.getBytes())));
			EncryptionKey encryptionKey = new EncryptionKey(encryptedPrivateKey, encryptedPublicKey);

			// mock the call to get Ecobee Zeus keys from DB.
			EasyMock.expect(mockEncryptedRouteDao.getEncryptionKey(EncryptionKeyType.EcobeeZeus)).andReturn(Optional.of(encryptionKey));
			EasyMock.replay(mockEncryptedRouteDao);

			// call the service method to be tested
			ZeusEncryptionKey zeusEncryptionKey = mockEcobeeZeusSecurityServiceImpl.getZeusEncryptionKey();

			// compare the public and private keys.
			assertEquals(zeusEncryptionKey.getPublicKey(), publicStringKey);
			assertEquals(zeusEncryptionKey.getPrivateKey(), privateStringKey);

		} catch (Exception e) {
			fail("Get Ecobee Zeus Excryption Key Failed " + e);
		}
	}
    
}
