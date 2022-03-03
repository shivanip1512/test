package com.cannontech.encryption;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.UnsupportedEncodingException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.junit.jupiter.api.Test;

import com.cannontech.encryption.impl.RSAPublicKeyCrypto;

public class RSAPublicKeyCryptoTest  {
    private KeyPair keys;
    
    // The following are dependent on each other.
    // Change one and the tests will fail
    private String publicKey = "30820222300d06092a864886f70d01010105000382020f003082020a0282020100a225b942bbb7c555d1917eaa28107a13fbbe29b1840f59a0745f5f8cae749c741a24aee4805c8ebcd2b546a2b5a3119d623d2d8cd650323e7bb5c59566cd44fb2df16235b61098634ecdc2ccd1ee9e2b9278b71e22d8ef60bbdeafb0e5f4db2e03b17e8b36d87ce25094c99431e5a87d6a838033449bfbb5310d06283145e6f0f320958fba77b90d5d1f4a339914cec957355a31c9acd4466c2e416e7dcf6699902767455dc40ae590a95115d4ec65c8ed0398cb063ed80586c8017e108917997438d62fa464c1c72e2add95a37aa8f934642dfe101925f06d5fa11e89a48d99be198f7fbe43264b12d85900e8ebace4f261e3cb4844dd8720093476f471ce1db564c6ee3987442b6475f4caf129450c8c110de5705527301da7bf60c7e39b2743662ab9701aaa1e04cd9eed75aa1c374f3c41b9dc24a1795ffb4e6ec9dae9f648f9718fde4680169968a8493b61940864fb21be3bc152a00cab41b6586edf85cbdd08dd3bb9dd9c383f8aab930e3e0df816d1aabb949bb275daffd6702329feefd501d017cbab8c404fbe85e8301e2f7d033ca723ccebfc7ee85af11bca4bb8408e7e7cb81ea496a2f37caa9b997c2e8b9b60ffe253871f05a06e2cd53b2a49f32a54bd9a2e459182830f3e4979c7be721fd5933ca322cf5f61187069d350a7fb1c008e6584d69df407df94ab75f287e0af81785cd9b65065e7a413712d17730203010001";
    private String privateKey = "30820942020100300d06092a864886f70d01010105000482092c308209280201000282020100a225b942bbb7c555d1917eaa28107a13fbbe29b1840f59a0745f5f8cae749c741a24aee4805c8ebcd2b546a2b5a3119d623d2d8cd650323e7bb5c59566cd44fb2df16235b61098634ecdc2ccd1ee9e2b9278b71e22d8ef60bbdeafb0e5f4db2e03b17e8b36d87ce25094c99431e5a87d6a838033449bfbb5310d06283145e6f0f320958fba77b90d5d1f4a339914cec957355a31c9acd4466c2e416e7dcf6699902767455dc40ae590a95115d4ec65c8ed0398cb063ed80586c8017e108917997438d62fa464c1c72e2add95a37aa8f934642dfe101925f06d5fa11e89a48d99be198f7fbe43264b12d85900e8ebace4f261e3cb4844dd8720093476f471ce1db564c6ee3987442b6475f4caf129450c8c110de5705527301da7bf60c7e39b2743662ab9701aaa1e04cd9eed75aa1c374f3c41b9dc24a1795ffb4e6ec9dae9f648f9718fde4680169968a8493b61940864fb21be3bc152a00cab41b6586edf85cbdd08dd3bb9dd9c383f8aab930e3e0df816d1aabb949bb275daffd6702329feefd501d017cbab8c404fbe85e8301e2f7d033ca723ccebfc7ee85af11bca4bb8408e7e7cb81ea496a2f37caa9b997c2e8b9b60ffe253871f05a06e2cd53b2a49f32a54bd9a2e459182830f3e4979c7be721fd5933ca322cf5f61187069d350a7fb1c008e6584d69df407df94ab75f287e0af81785cd9b65065e7a413712d17730203010001028202004538a9904da7347d608c2670e9cdaef01d069fb202d8b2c0872db812ccdf4773613dcbd40ce076a731a4e5d4cc8a2318f7397adaa71b7177af447867418ddc27070407933a787735de0c855eff384919fcdbc001038b6d15f4de2a4ef0bcc95d29827aad5feaa7d5cde4d6f18723adfc6c9e5afa880db735310d88555c43a545b3e57333fabce5a948d9f4c9764ea5c0da94123f3303af53a9174e23a75e0545057f6cdcf61c9d3247c1a0b61b421869e0952a8371d51e4fadac70c6aab47bf8acdf1ec11403579e7a9d1e1fa5bcd5aa1dadaedcc96e92fdc313922b0143d8978bca49b2953d017a073f892f9440187d5ce4616505233117609bcd6403131a94704e746a2f18179446329752dacae258e4a7dc5f526bec0cd543d236a11bdd4e96f9359004cdfc29fe7a4ea43ee49308c26db799094b94e7965619af66abf14d46031675788b94a7034e8011a1cd9c60a3eb47d32f9085a8b83fc4657d080330d608bc0aecd9cf9df134f0da7a74dd66855c8af68bbd238d3b6610b38082062e89501edfae3bfb3421fae12daf4d629f2de7beed5186f0cd08952f792f2c24c34817bf5e1ea905d2850d44ea892ecf6252e1960b8d9cc9c53dcccf6c539989ff8cdbe72622293118464911b8c364552f375a90a5d92361c767e94ec3fecbdbfa7cb6202dcc05340ebf52298493f64119a5eae1e0be9f02a7a14e94d8243a19110282010100ecdf21dd7731c88fb5e08fd9dc9f484c491fecee9ae7c5b20ce03c7fa47e340a03f3e92e57a679d5422399467c3b6678b59427c614c5e426398c553862b73a6980fc033cdc8826ba707eff1ed26d8184dd474de97968e40de11a8a6bb59352c3b5134c2e83dd4f5e1fd257f39d7db8cd3c1437ff8abf7220e998e7824042fe40e0e06d82696128d8ff693271a10383de627950d259af751b00f093fd707b40410ffb4314d20428e55fe57a343f349992c70e9a0a3c63db0a08dda5a675a1433449d3c240008a03fcab32991a52e6e2e75aedcc837436092e0b6709a6a5ad8059420632c66c133371e0cd0635caafe704e4a17c252dec29e0634fbf8d605f1a7f0282010100af3dcf71509d575580ce9f81d089cb3ba65c029ca01234986c87af6fa1058fb7cecfa2f1893cbfaf16b206fbabe330d199c318016c282157a8ad98a112243778cdc4fd397cbf457b0cfdc290a2adc7aeb281d0ceabbaac86131ca6a081642d21e5a7fdb002641e524a8adcad473e6d77e6f81322803d59eeab19ab51b4979f7c41833561f74a455b75073d254f9e7c27310ec5b3ceffb496c746ab188058a7265504fd88d2093d753f78474f1fe663c74870836bfacf15db3317e775bf6295cdceb6436a6431776cd7bdbfe4320a01021745497e145c3cc2581fd75d11520e6584c27502134af345aa11b89b4f47bf0e04f64996732bd496c523c2471121c10d028201010091d49de8d725533106ec8623beec617a75daade8087e80fc29693455f153b17e2908d59aae63d3c633bdeef3b2306fda0910799c3dd5c7acb0e970de12087471d7f34f9afcbae5fd0a9d653bf8996a8ffe1b9420159b3a2c415631ba3643c944ac0a817071c6a2c2741cf001100902e977ff8aaa4d25a88212cf0c58e86f5651632148ca6d4e5ff398eb8f1eddde682555c312bb2e741348d8bf449d57cec9b444be908214fa72a324f306d57bc44a784f0cf25132f4d739ca538b547c3f183cece6ee44b110477c1b7a3f4031a1ec383b8703ce3a91b9fd1b1008a50c8350eda2f465f63b3b6704c93df080d5948e4a38e822a2512f8068a13bcbd6a26a6ac9028201002e7654e9d655e131ea54de35acf4dbcd94ee104336ad9d044cc22ce18befcd8b0e6a3ee98b596ff91c2b576ea2c439cd58d741261f6704701968ed6ec74092f4949a060997e99ea2616f6686b29b20b650ddda5ef1692d14df3c597174dcfa9a622988398268c921486248e0286384e2a57a60986527df10488d25bf24028f668278274f32219f0200a54ab0f9cb2b5fb8c211e8fa99116868ad73efab17d63973913f276788cba2aa915956c13e747601ded2f96e2639c54466b217a6069d7153bd3f6295ee079bf4177b29259b41692c825259fb4bf9500dcd636c698b3805f28d1c38d0a4ae9b69dc08f624af0118d677f64acd7e717f0805d31cc48e0e81028201001cd15facd7289cff511ca6557dd8bb0cec1a19314af014806d08eed7962b3ef6bd804aebe35535036ecfb3330e33e8b8b0a4b6f3fc95ff65c33ed2a8c20a02502167249c6b34bd5d6fe395cc506e20f96897677a3dbfc03e6fc547a1c3bc80a96d46efb112318bb613f212a3ec1901ddf44de0382f0e3004dd895fa9f0e2bd300aa8d862b8f5ce0a293da8e37860c67f98296b104cfe85fa82e1e129637ab2bca958cf0b8aba10de037fad005bd692054ebe21d730431a18e9d8611a726f557bdfa3441c7325193da378104ab54707c9dee0492c774f59f10eaacd6815d0a948b755064c146bd1aaba8e06ba623d2faf7aa76f5b02ffb896521bee43cee15972";
    private String cipherText = "8460a4b754fe7bfdb8468392b047c9c38eaee15104dded7fc2a73d4fd4c9b81819e58da31f12abda5ad5b6c78d2459f058fbac44c83135c2e8a61fd897b0405430ffda3478c69844ca6e8d5dbee53df7802db771079d276f812b80f13d7be1d9322df030956fd984ea34494d3b0b4a50afe30b87934451efd7abf43aa527e2fb99d7cd3af17801625042c20d03bdb4858c1dfa06e23d07e10c331e9c36b38f450c76657702a9425a25f02df6c63a53cd4d2c9a307f574b9d83beb544d5d4aab8c7b6c674f3ef7228cc86bb179915c725f3e1d607e47a722820455f5794a0805b2b7c472fb33e5b3a971c08f5d186de279d2227db0d74daa5750963380941380f44c12e54f72d05212028429d44b2e1f12b8307f25724ab1c5d3a2c73856176cfc759b8cc55bbd06577d1709935253a33ce8b0f33747e23d0f5761bd13f964e2c8672911fd6b178cb78f6599f8967b4747101f7dc6befc5c3182b82ad97c7f0203154ecd9617205d1354e2d30353411d053648bbd0e99b84e82c6f52a813b6112906ce620d570ca19c2a671f4145c162e04800c22df62576e3b8dc83e322f631a83632d5c30deb417385f2ee431e1b70bddb43e4e9ec3cf5673a61f38245b809aac24f5eb35b771b0fb9509fb0e5f56be45cc0be0abe2d06e9ca83fb581b79523dc1e5b0472328380794966093a2a451c19cbf4b3e8067255aa11c30eaec9c8a5";
    private String plainTextStr = "Hello World! This is a test";
    private static final String ALGORITHM = "RSA";
    
    public RSAPublicKeyCryptoTest() throws NoSuchAlgorithmException, UnsupportedEncodingException {
       keys = CryptoUtils.generateRSAKeyPair();
    }
    
    @Test
    public void test_encryption() throws CryptoException, DecoderException, InvalidKeySpecException, NoSuchAlgorithmException {
        byte[] preComputedPlainTextBytes = plainTextStr.getBytes();
        byte[] publicKeyBytes = Hex.decodeHex(publicKey.toCharArray());
        PublicKey publicKey = KeyFactory.getInstance(ALGORITHM).generatePublic(new X509EncodedKeySpec(publicKeyBytes));
        byte[] computedCipherBytes = RSAPublicKeyCrypto.encrypt(preComputedPlainTextBytes, publicKey);

        // Weak test. Testing that the returned value isn't the plain text.
        // Since PKCS1 padding makes this cipher non-deterministic we can't test
        // the computed cipher bytes against known cipher bytes since it changes
        // every time.

        assertEquals(false, Arrays.equals(preComputedPlainTextBytes, computedCipherBytes));
    }
    
    @Test
    public void test_decryption() throws CryptoException, InvalidKeySpecException, NoSuchAlgorithmException, DecoderException {
        byte[] preComputedPlainTextBytes = plainTextStr.getBytes();
        byte[] preComputedCipherTextBytes = Hex.decodeHex(cipherText.toCharArray());
        byte[] privateKeyBytes = Hex.decodeHex(privateKey.toCharArray());
        PrivateKey privateKey = KeyFactory.getInstance(ALGORITHM).generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
        byte[] computedPlainBytes = RSAPublicKeyCrypto.decrypt(preComputedCipherTextBytes, privateKey);

        // Testing the known plain text to the computed plain text using the precomputed private key.
        assertEquals(true, Arrays.equals(preComputedPlainTextBytes, computedPlainBytes));
    }
    
    @Test
    public void test_encryptionDecryption_pass() throws CryptoException {
        int randomDataLength = 32;
        byte [] knownData = new byte[randomDataLength];
        byte [] encryptedData = new byte[randomDataLength];
        byte [] computedData = new byte[randomDataLength];

        SecureRandom rand = new SecureRandom();
        for (int e=0;e<randomDataLength;e++) {
            knownData[e] = (byte)rand.nextInt(256);
        }

        // Encrypt
        encryptedData = RSAPublicKeyCrypto.encrypt(knownData,keys.getPublic());
        // Decrypt
        computedData = RSAPublicKeyCrypto.decrypt(encryptedData,keys.getPrivate());

        // Make sure something changed, that we wern't just passing around the same data
        assertEquals(false, Arrays.equals(encryptedData,computedData));
        assertEquals(false, Arrays.equals(encryptedData,knownData));

        // Make sure we got plain text back.
        assertEquals(true, Arrays.equals(knownData,computedData));
    }
    
}