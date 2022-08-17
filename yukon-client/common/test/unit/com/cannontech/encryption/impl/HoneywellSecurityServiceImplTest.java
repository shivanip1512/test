package com.cannontech.encryption.impl;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Optional;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.easymock.EasyMock;
import org.jdom2.JDOMException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.database.db.security.EncryptionKey;
import com.cannontech.encryption.CertificateGenerationFailedException;
import com.cannontech.encryption.CryptoException;
import com.cannontech.encryption.CryptoUtils;
import com.cannontech.encryption.EncryptedRouteDao;
import com.cannontech.encryption.EncryptionKeyType;
import com.cannontech.encryption.HoneywellSecurityService;

public class HoneywellSecurityServiceImplTest {

    private EncryptedRouteDao mockEncryptedRouteDao;
    private HoneywellSecurityService honeywellSecurityService;

    @Before
    public void setup() {
        mockEncryptedRouteDao = EasyMock.createMock(EncryptedRouteDao.class);
        honeywellSecurityService = new HoneywellSecurityServiceImpl();
        ReflectionTestUtils.setField(honeywellSecurityService, "encryptedRouteDao", mockEncryptedRouteDao);
    }

    @Test
    public void test_generateHoneywellPublicKey() {
        try {
            char[] password = CryptoUtils.getSharedPasskey();
            AESPasswordBasedCrypto encrypter = new AESPasswordBasedCrypto(password);

            // generate public/private key pair
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(1024, new SecureRandom());

            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            PublicKey publicKey = keyPair.getPublic();
            String publicStringKey = new String(Base64.encodeBase64(publicKey.getEncoded()));
            String encryptedPublicKey = new String(Hex.encodeHex(encrypter.encrypt(publicStringKey.getBytes())));

            PrivateKey privateKey = keyPair.getPrivate();
            String privateStringKey = new String(Base64.encodeBase64(privateKey.getEncoded()));
            String encryptedPrivateKey = new String(Hex.encodeHex(encrypter.encrypt(privateStringKey.getBytes())));
            EncryptionKey encryptionKey = new EncryptionKey(encryptedPrivateKey, encryptedPublicKey);
            
            // mock the call to get honeywell keys from DB.
            EasyMock.expect(mockEncryptedRouteDao.getEncryptionKey(EncryptionKeyType.Honeywell)).andReturn(Optional.of(encryptionKey));
            EasyMock.replay(mockEncryptedRouteDao);

            // call the service method to be tested
            X509Certificate certificate = honeywellSecurityService.generateHoneywellCertificate();

            // compare the public decoded from certificate with that provided as input.
            assert (Arrays.equals(certificate.getPublicKey().getEncoded(), keyPair.getPublic().getEncoded()));
            EasyMock.verify(mockEncryptedRouteDao);

        } catch (CryptoException | JDOMException e) {
            fail("Certificate generation failed " + e);
        } catch (NoSuchAlgorithmException e) {
            fail("Invalid key generation algorithm " + e);
        } catch (IOException e) {
            fail("Certificate generation failed " + e);
        } catch (CertificateGenerationFailedException e) {
            fail("Certificate generation failed " + e);
        }
    }

}
