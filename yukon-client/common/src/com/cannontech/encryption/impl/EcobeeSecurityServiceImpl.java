package com.cannontech.encryption.impl;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.util.Date;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.bcpg.SymmetricKeyAlgorithmTags;
import org.bouncycastle.bcpg.sig.Features;
import org.bouncycastle.bcpg.sig.KeyFlags;
import org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider;
import org.bouncycastle.openpgp.PGPCompressedData;
import org.bouncycastle.openpgp.PGPEncryptedData;
import org.bouncycastle.openpgp.PGPEncryptedDataList;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPKeyPair;
import org.bouncycastle.openpgp.PGPKeyRingGenerator;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPObjectFactory;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyEncryptedData;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPSignatureSubpacketGenerator;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.jcajce.JcaPGPObjectFactory;
import org.bouncycastle.openpgp.operator.PBESecretKeyDecryptor;
import org.bouncycastle.openpgp.operator.PBESecretKeyEncryptor;
import org.bouncycastle.openpgp.operator.PGPDigestCalculator;
import org.bouncycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPDigestCalculatorProviderBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPKeyPair;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyDecryptorBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyEncryptorBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePublicKeyDataDecryptorFactoryBuilder;
import org.bouncycastle.util.io.Streams;
import org.jdom2.JDOMException;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.exception.EcobeePGPException;
import com.cannontech.database.db.security.EncryptionKey;
import com.cannontech.encryption.CryptoException;
import com.cannontech.encryption.CryptoUtils;
import com.cannontech.encryption.EcobeeSecurityService;
import com.cannontech.encryption.EncryptedRouteDao;
import com.cannontech.encryption.EncryptionKeyType;

public class EcobeeSecurityServiceImpl implements EcobeeSecurityService {

    private final String PASSPHRASE= "passphrase";
    private final String IDENTITY= "Identity";
    private Logger log = YukonLogManager.getLogger(EcobeeSecurityServiceImpl.class);
    @Autowired private EncryptedRouteDao encryptedRouteDao;

    @Override
    public Instant generateEcobeePGPKeyPair() throws EcobeePGPException {
        Instant timestamp = Instant.now();
        try {
            KeyPair rsaKeyPair = generateRSAKeyPair();
            PGPKeyRingGenerator keyRingGen = createPGPKeyRingGenerator(rsaKeyPair);
            String pgpPrivateKey = generatePGPPrivateKey(keyRingGen);
            String pgpPublicKey = generatePGPPublicKey(keyRingGen);
            savePublicAndPrivateEcobeeKey(pgpPublicKey, pgpPrivateKey, timestamp);

        } catch (NoSuchAlgorithmException | NoSuchProviderException | PGPException | IOException 
                | CryptoException | JDOMException e) {
            throw new EcobeePGPException("Unable to generate Ecobee PGP key Pair", e);
        }
        return timestamp;
    }

    @Override
    public String getEcobeePGPPublicKey() throws Exception {
        String publicKey;
        char[] password = CryptoUtils.getSharedPasskey();
        AESPasswordBasedCrypto encrypter = new AESPasswordBasedCrypto(password);
        Optional<EncryptionKey> encryptionKey = encryptedRouteDao.getEncryptionKey(EncryptionKeyType.Ecobee);
        if (encryptionKey.isPresent()) {
            publicKey = encryptionKey.get().getPublicKey();
            return encrypter.decryptHexStr(publicKey);
        }
        generateEcobeePGPKeyPair();
        publicKey = encryptedRouteDao.getEncryptionKey(EncryptionKeyType.Ecobee).get().getPublicKey();
        return encrypter.decryptHexStr(publicKey);
    }

    @Override
    public byte[] decryptEcobeeFile(InputStream imputStream) throws EcobeePGPException {
        Optional<EncryptionKey> encryptionKey = encryptedRouteDao.getEncryptionKey(EncryptionKeyType.Ecobee);
        if (encryptionKey.isPresent()) {
            return decryptFile(imputStream, encryptionKey.get().getPrivateKey());
        }
        throw new EcobeePGPException("Unable to decrypt gpg file as PGP Private Key is missing");
    }

    @Override
    public Instant getEcobeeKeyPairCreationTime() throws NoSuchElementException {
        Optional<EncryptionKey> encryptionKey = encryptedRouteDao.getEncryptionKey(EncryptionKeyType.Ecobee);
        return encryptionKey.map(EncryptionKey :: getTimestamp)
                            .orElseThrow();
    }

    /**
     * This method generate the RSA key pair with size of 2048 bits.For creating the key pair we
     * are using the BouncyCastleFipsProvider.
     */
    private KeyPair generateRSAKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException {
        Security.addProvider(new BouncyCastleFipsProvider());
        KeyPairGenerator rsaKeyPairGenerator = KeyPairGenerator.getInstance("RSA", "BCFIPS");
        rsaKeyPairGenerator.initialize(2048);
        KeyPair rsaKeyPair = rsaKeyPairGenerator.generateKeyPair();
        return rsaKeyPair;
    }

    /**
     * This method creates the PGPKeyRingGenerator. We are using two RSA key pair one for signing and 
     * other for encrypting.
     */
    private PGPKeyRingGenerator createPGPKeyRingGenerator(KeyPair rsaKeyPair) throws PGPException {
        PGPKeyPair rsaKeyPairSign = new JcaPGPKeyPair(PGPPublicKey.RSA_SIGN, rsaKeyPair, new Date());
        PGPKeyPair rsaKeyPairEncrypt = new JcaPGPKeyPair(PGPPublicKey.RSA_ENCRYPT, rsaKeyPair, new Date());
        PGPSignatureSubpacketGenerator signhashgen = new PGPSignatureSubpacketGenerator();

        signhashgen.setKeyFlags(false, KeyFlags.SIGN_DATA | KeyFlags.CERTIFY_OTHER);
        // Set preferences for secondary crypto algorithms to use when sending messages to this key.
        signhashgen.setPreferredSymmetricAlgorithms
        (false, new int[] {
            SymmetricKeyAlgorithmTags.AES_256,
            SymmetricKeyAlgorithmTags.AES_192,
            SymmetricKeyAlgorithmTags.AES_128
        });
        signhashgen.setPreferredHashAlgorithms
        (false, new int[] {
            HashAlgorithmTags.SHA256,
            HashAlgorithmTags.SHA1,
            HashAlgorithmTags.SHA384,
            HashAlgorithmTags.SHA512,
            HashAlgorithmTags.SHA224
        });

        signhashgen.setFeature(false, Features.FEATURE_MODIFICATION_DETECTION);

        // Create a signature on the encryption subkey.
        PGPSignatureSubpacketGenerator enchashgen = new PGPSignatureSubpacketGenerator();
        // Add metadata to declare its purpose
        enchashgen.setKeyFlags(false, KeyFlags.ENCRYPT_COMMS | KeyFlags.ENCRYPT_STORAGE);

        // Objects used to encrypt the secret key.
        PGPDigestCalculator sha1Calc = new JcaPGPDigestCalculatorProviderBuilder().build()
                                                                                  .get(HashAlgorithmTags.SHA1);

        PGPDigestCalculator sha256Calc =new JcaPGPDigestCalculatorProviderBuilder().build()
                                                                                   .get(HashAlgorithmTags.SHA256);

        PBESecretKeyEncryptor pske = new JcePBESecretKeyEncryptorBuilder(PGPEncryptedData.AES_256, 
                                                                    sha256Calc).setProvider("BCFIPS")
                                                                    .build(PASSPHRASE.toCharArray());

        // Finally, create the keyring itself. The constructor
        // takes parameters that allow it to generate the self
        // signature.
        PGPKeyRingGenerator keyRingGen = 
                new PGPKeyRingGenerator(PGPSignature.POSITIVE_CERTIFICATION, rsaKeyPairSign,
                                        IDENTITY, sha1Calc, signhashgen.generate(), null,
                                        new JcaPGPContentSignerBuilder(rsaKeyPairSign.getPublicKey().getAlgorithm(),
                                        HashAlgorithmTags.SHA384), pske);

        // Add our encryption subkey, together with its signature.
        keyRingGen.addSubKey(rsaKeyPairEncrypt, enchashgen.generate(), null);
        return keyRingGen;
    }

    /**
     * Generate PGP Private Key using PGPKeyRingGenerator.
     */
    private String generatePGPPrivateKey(PGPKeyRingGenerator keyRingGen) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (ArmoredOutputStream armoredOutputStream = new ArmoredOutputStream(outputStream);
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(armoredOutputStream)) {
            keyRingGen.generateSecretKeyRing().encode(bufferedOutputStream);
        }
        String pgpPrivateKey = outputStream.toString(UTF_8.name());
        log.info("Generated PGP PrivateKey " + pgpPrivateKey);
        return pgpPrivateKey;
    }

    /**
     * Generate PGP Public Key using PGPKeyRingGenerator.
     */
    private String generatePGPPublicKey(PGPKeyRingGenerator keyRingGen) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (ArmoredOutputStream armoredOutputStream = new ArmoredOutputStream(outputStream);
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(armoredOutputStream)) {
            keyRingGen.generatePublicKeyRing().encode(bufferedOutputStream);
        }
        String pgpPublicKey = outputStream.toString(UTF_8.name());
        log.info("Generated PGP PublicKey " + pgpPublicKey);
        return pgpPublicKey;
    }

    /**
     * This method will change the private and public key to encoded hex format and save the 
     * encoded private and public Hex string to database.
     */
    private void savePublicAndPrivateEcobeeKey(String publicKey, String privateKey, Instant timestamp) throws CryptoException, IOException, JDOMException {
        char[] password = CryptoUtils.getSharedPasskey();
        AESPasswordBasedCrypto encrypter = new AESPasswordBasedCrypto(password);
        String aesBasedCryptoPublicKey = new String(Hex.encodeHex(encrypter.encrypt(publicKey.getBytes())));
        log.info("AES based crypto Public key [" + aesBasedCryptoPublicKey + "]");
        String aesBasedCryptoPrivateKey = new String(Hex.encodeHex(encrypter.encrypt(privateKey.getBytes())));
        log.info("AES based crypto Private key [" + aesBasedCryptoPrivateKey + "]");
        encryptedRouteDao.saveOrUpdateEncryptionKey(aesBasedCryptoPrivateKey, aesBasedCryptoPublicKey, EncryptionKeyType.Ecobee, timestamp);
    }

    private byte[] decryptFile(InputStream imputStream, String privateKey) throws EcobeePGPException {
        PGPPublicKeyEncryptedData publicKeyEncryptedData = null;
        PGPEncryptedDataList encList;
        JcaPGPObjectFactory jcaPGPObjectFactory;
        try {
            byte[] encryptedGPGByteArray = IOUtils.toByteArray(imputStream);
            char[] password = CryptoUtils.getSharedPasskey();

            AESPasswordBasedCrypto encrypter = new AESPasswordBasedCrypto(password);
            String aesDecryptedPrivatekey = encrypter.decryptHexStr(privateKey);
            PGPSecretKey secretKey = readSecretKey(aesDecryptedPrivatekey);
            PBESecretKeyDecryptor decryptor = new JcePBESecretKeyDecryptorBuilder().build(PASSPHRASE.toCharArray());
            PGPPrivateKey pgpPrivateKey = secretKey.extractPrivateKey(decryptor);
            PGPObjectFactory pgpObjectFactory = new JcaPGPObjectFactory(encryptedGPGByteArray);
            Object obj = pgpObjectFactory.nextObject();
            if (obj instanceof PGPEncryptedDataList) {
                encList = (PGPEncryptedDataList) obj;
            } else {
                encList = (PGPEncryptedDataList) pgpObjectFactory.nextObject();
            }

            Iterator<PGPPublicKeyEncryptedData> iterator = encList.getEncryptedDataObjects();
            while (iterator.hasNext()) {
                publicKeyEncryptedData = iterator.next();
            }

            if (publicKeyEncryptedData == null) {
                throw new EcobeePGPException("Didn't found PGP Public Key Encrypted data.");
            }
            InputStream dataStream = publicKeyEncryptedData
                                    .getDataStream(new JcePublicKeyDataDecryptorFactoryBuilder().build(pgpPrivateKey));

            jcaPGPObjectFactory = new JcaPGPObjectFactory(dataStream);
            PGPCompressedData compressedData = (PGPCompressedData) jcaPGPObjectFactory.nextObject();
            jcaPGPObjectFactory = new JcaPGPObjectFactory(compressedData.getDataStream());

            PGPLiteralData literalData = (PGPLiteralData) jcaPGPObjectFactory.nextObject();
            byte[] decryptedData = Streams.readAll(literalData.getDataStream());
            return decryptedData;
        } catch (CryptoException | IOException | JDOMException | DecoderException | PGPException e) {
            log.error("Error while decrypting the gpg file" + e);
            throw new EcobeePGPException("Unable to decrypt the gpg file");
        }
    }

    /**
     * This method will return the PGPSecret Key from the Secret Key Ring Collection and with secret key
     * we will get the PGPPrivate Key to decrypt the gpg file.
     */
    private PGPSecretKey readSecretKey(String privateKey) throws IOException, PGPException, EcobeePGPException {
        InputStream in = new ByteArrayInputStream(privateKey.getBytes());
        PGPSecretKeyRingCollection pgpSec =
            new PGPSecretKeyRingCollection(PGPUtil.getDecoderStream(in), new JcaKeyFingerprintCalculator());

        Iterator<PGPSecretKeyRing> keyRingIter = pgpSec.getKeyRings();
        while (keyRingIter.hasNext()) {
            PGPSecretKeyRing keyRing = (PGPSecretKeyRing) keyRingIter.next();

            Iterator<PGPSecretKey> keyIter = keyRing.getSecretKeys();
            while (keyIter.hasNext()) {
                PGPSecretKey key = (PGPSecretKey) keyIter.next();

                if (key.isSigningKey()) {
                    return key;
                }
            }
        }

        throw new EcobeePGPException("Can't find signing key in key ring.");
    }

}
