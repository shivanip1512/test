package com.cannontech.encryption.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.jdom2.JDOMException;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.database.db.security.EncryptionKey;
import com.cannontech.encryption.CertificateGenerationFailedException;
import com.cannontech.encryption.CryptoException;
import com.cannontech.encryption.CryptoUtils;
import com.cannontech.encryption.EncryptedRouteDao;
import com.cannontech.encryption.EncryptionKeyType;
import com.cannontech.encryption.HoneywellSecurityService;

public class HoneywellSecurityServiceImpl implements HoneywellSecurityService {

    @Autowired private EncryptedRouteDao encryptedRouteDao;

    private static final String DISTINGUISHED_NAME = "CN=Eaton";
    private static final String KEY_GENERATION_ALGORITHM_RSA = "RSA";
    private static final String CERTIFICATE_TYPE = "X.509";
    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";
    private static final int CERTIFICATE_VALIDITY_PERIOD = 5;
    private static final int TIME_OFFSET = 24 * 60 * 60 * 1000;

    @Override
    public X509Certificate generateHoneywellCertificate() throws CertificateGenerationFailedException {
        try {
            // get honeywell public and private keys from database
            Optional<EncryptionKey> honeywellEncryptionKey = encryptedRouteDao.getEncryptionKey(EncryptionKeyType.Honeywell);
            
            
            if(honeywellEncryptionKey.isEmpty()) {
                throw new CertificateGenerationFailedException("Honeywell public key not found in database");
            }

            // decrypt the public and private keys
            String decryptedPublicKey = decryptKey(honeywellEncryptionKey.get().getPublicKey());
            String decryptedPrivateKey = decryptKey(honeywellEncryptionKey.get().getPrivateKey());

            // certificate validity
            Date validFrom = new Date(System.currentTimeMillis() - TIME_OFFSET);
            Calendar expiry = Calendar.getInstance();
            // the certificate will be valid till 5 years from the day before it was generated
            expiry.add(Calendar.YEAR, CERTIFICATE_VALIDITY_PERIOD);
            Date validTill = new Date(expiry.getTimeInMillis());

            // since this is a self-signed certificate, certificate issuer and subject will be the same
            X500Name certificateIssuer = new X500Name(DISTINGUISHED_NAME);

            // create PublicKey object from decrypted public key string value
            X509EncodedKeySpec publicKeySpecification = new X509EncodedKeySpec(Base64.decodeBase64(decryptedPublicKey));
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_GENERATION_ALGORITHM_RSA);
            PublicKey publicKey = keyFactory.generatePublic(publicKeySpecification);

            // create PrivateKey object from decrypted private key string value
            PKCS8EncodedKeySpec privateKeySpecification =
                new PKCS8EncodedKeySpec(Base64.decodeBase64(decryptedPrivateKey));
            PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpecification);

            // generate certificate
            X509v3CertificateBuilder certificateBuilder =
                new X509v3CertificateBuilder(certificateIssuer, BigInteger.valueOf(System.currentTimeMillis()),
                    validFrom, validTill, certificateIssuer, SubjectPublicKeyInfo.getInstance(publicKey.getEncoded()));
            JcaContentSignerBuilder contentSignerBuilder = new JcaContentSignerBuilder(SIGNATURE_ALGORITHM);
            ContentSigner signer = contentSignerBuilder.build(privateKey);
            CertificateFactory certificateFactory = CertificateFactory.getInstance(CERTIFICATE_TYPE);

            return (X509Certificate) certificateFactory.generateCertificate(new ByteArrayInputStream(
                certificateBuilder.build(signer).getEncoded()));
        } catch (IOException | CryptoException | JDOMException | DecoderException | NoSuchAlgorithmException
            | InvalidKeySpecException | OperatorCreationException | CertificateException e) {
            throw new CertificateGenerationFailedException(e);
        }
    }


    private String decryptKey(String key) throws CryptoException, IOException, JDOMException, DecoderException {
        char[] password = CryptoUtils.getSharedPasskey();
        AESPasswordBasedCrypto decrypter = new AESPasswordBasedCrypto(password);
        return new String(decrypter.decryptHexStr(key));
    }

}
