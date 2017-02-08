package com.cannontech.web.admin;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.easymock.EasyMock;
import org.jdom2.JDOMException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.database.db.security.EncryptionKey;
import com.cannontech.encryption.CryptoException;
import com.cannontech.encryption.CryptoUtils;
import com.cannontech.encryption.EncryptedRouteDao;
import com.cannontech.encryption.EncryptedRouteService;
import com.cannontech.encryption.impl.AESPasswordBasedCrypto;
import com.cannontech.encryption.impl.EncryptedRouteServiceImpl;

public class YukonSecurityControllerTest {

    private EncryptedRouteDao mockEncryptedRouteDao;
    private YukonSecurityController yukonSecurityController;
    private EncryptedRouteService encryptedRouteService;

    @Before
    public void setup() {
        mockEncryptedRouteDao = EasyMock.createMock(EncryptedRouteDao.class);
        yukonSecurityController = new YukonSecurityController();
        encryptedRouteService = new EncryptedRouteServiceImpl();
        ReflectionTestUtils.setField(encryptedRouteService, "encryptedRouteDao", mockEncryptedRouteDao);
        ReflectionTestUtils.setField(yukonSecurityController, "encryptedRouteService", encryptedRouteService);
    }

    @Test
    public void test_DownloadHoneywellPublicKey() {
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
            EasyMock.expect(mockEncryptedRouteDao.getHoneywellEncryptionKey()).andReturn(encryptionKey);
            EasyMock.replay(mockEncryptedRouteDao);

            // call the controller method to be tested
            yukonSecurityController.downloadHoneywellPublicKey();

            // read the .crt file from file system
            StringBuilder crtFilePath = new StringBuilder();
            crtFilePath.append(System.getenv("YUKON_BASE"));
            crtFilePath.append("\\Server\\Config\\Keys\\cert-eaton.crt");
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            InputStream inputStream = new FileInputStream(new File(crtFilePath.toString()));
            X509Certificate certificate = (X509Certificate) certificateFactory.generateCertificate(inputStream);

            // compare the public decoded from certificate with that provided as input.
            assert (Arrays.equals(certificate.getPublicKey().getEncoded(), keyPair.getPublic().getEncoded()));
            EasyMock.verify(mockEncryptedRouteDao);

        } catch (CryptoException | JDOMException | CertificateException e) {
            fail("Certificate generation failed " + e);
        } catch (NoSuchAlgorithmException e) {
            fail("Invalid key generation algorithm " + e);
        } catch (FileNotFoundException e) {
            fail("Could not read .crt file " + e);
        } catch (IOException e) {
            fail("Certificate generation failed " + e);
        }
    }

}
