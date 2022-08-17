package com.cannontech.encryption;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.ClassPathResource;

import com.cannontech.clientutils.YukonLogManager;

public class SystemPublisherMetadataCryptoUtils {

    private static final Logger log = YukonLogManager.getLogger(SystemPublisherMetadataCryptoUtils.class);
    private static final String SECRET_KEY = "452C3BdAD-1RT2-508A-6D62-FDFB58B52TRM";
    private static final String ENCRYPTED_FILE_PATH = "\\resource\\encryptedSystemPublisherMetadata.yaml";
    private static final String SYSTEM_PUBLISHER_METADATA = "systemPublisherMetadata.yaml";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";
    private static final String CHARACTER_SET = "UTF-16";
    private static final String MESSAGEDIGEST_ALGORITHM = "SHA-256";
    private static final String ENCRYPTION_ALGORITHM = "AES";
    private static final String DICTIONARY_FIELDS_SPACE = "    ";
    private static final String SOURCE_FIELD_SPACE = "        ";
    private static final String SOURCE_FIELD = "source";
    private static final String YAML_MULTILINE_IDENTIFIER = ">-";
    private static final String END_LINE_CHARACTER = System.getProperty("line.separator");
    private static final String AUTO_ENCRYPTED_TEXT = "(AUTO_ENCRYPTED)";
    private static final String SEPARATOR = ":";
    private static final List<String> NON_SOURCE_FIELDS = Arrays.asList(DICTIONARY_FIELDS_SPACE + "field",
                                                                        DICTIONARY_FIELDS_SPACE + "description",
                                                                        DICTIONARY_FIELDS_SPACE + "details",
                                                                        DICTIONARY_FIELDS_SPACE + "iotType",
                                                                        DICTIONARY_FIELDS_SPACE + "frequency");

    private static SecretKeySpec secretKey;
    private static Cipher cipher;

    /**
     * Set the SecretKeySpec by using the the provided secret key.
     */
    private static void setKey() {
        try {
            byte[] key = SECRET_KEY.getBytes(CHARACTER_SET);
            MessageDigest sha = MessageDigest.getInstance(MESSAGEDIGEST_ALGORITHM);
            key = sha.digest(key);
            secretKey = new SecretKeySpec(key, ENCRYPTION_ALGORITHM);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            log.error("Error while creating secret key ", e);
        }
    }

    /**
     * Set the Chiper by using provided mode(Cipher.ENCRYPT_MODE, Cipher.DECRYPT_MODE) and SecretKeySpec.
     */
    private static void setChiper(Integer cipherMode) {
        try {
            cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(cipherMode, secretKey);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            log.error("Error while initilizing cipher ", e);
        }
    }

    /**
     * Encrypt the input string and returns the encrypted value
     */
    public static String encrypt(String strToEncrypt)
            throws IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        setKey();
        setChiper(Cipher.ENCRYPT_MODE);
        return new String(Base64.getEncoder().encode(cipher.doFinal(strToEncrypt.getBytes(CHARACTER_SET))));
    }

    /**
     * Decrypt the input string and returns the plain text value
     */
    public static String decrypt(String strToDecrypt)
            throws IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        setKey();
        setChiper(Cipher.DECRYPT_MODE);
        return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)), CHARACTER_SET);
    }

    /**
     * Reads every lines of the YAML file and then encrypt the source field(Both multiline and single line).
     */
    private static void processFile() throws IOException, IllegalBlockSizeException, BadPaddingException {

        // Get the actual directory of the file as we need to encrypt the actual file.
        File encryptedYamlFile = new File(System.getProperty("user.dir") + ENCRYPTED_FILE_PATH);
        StringBuilder tempYamlBuilder = new StringBuilder();

        processLines(tempYamlBuilder);

        FileUtils.writeStringToFile(encryptedYamlFile, tempYamlBuilder.toString());
    }

    /**
     * Process the every line and set the required flags which will be used for encrypting the source fields.
     */
    private static void processLines(StringBuilder tempYamlBuilder)
            throws IOException, IllegalBlockSizeException, BadPaddingException {

        InputStream yamlInputStream = new ClassPathResource(SYSTEM_PUBLISHER_METADATA).getInputStream();
        // Flag to identify multiline source. Set it to true when "....source" contains multiple lines (identified by ">-" text).
        boolean multiLineSource = false;
        boolean encryptionRequired = false;
        String multiLineSourceStr = StringUtils.EMPTY;
        try (BufferedReader systemPublisherMetadataReader = new BufferedReader(new InputStreamReader(yamlInputStream))) {
            List<String> lines = systemPublisherMetadataReader.lines().collect(Collectors.toList());
            for (String line : lines) {
                String[] values = line.split(SEPARATOR, 2);
                if (line.startsWith(DICTIONARY_FIELDS_SPACE + SOURCE_FIELD)) {
                    if (values[1].contains(YAML_MULTILINE_IDENTIFIER)) {
                        multiLineSource = true;
                    }
                    encryptionRequired = true;
                } else if (NON_SOURCE_FIELDS.contains(values[0])) {
                    if (multiLineSource && StringUtils.isNotEmpty(multiLineSourceStr)) {
                        encryptLine(multiLineSourceStr, encryptionRequired, multiLineSource, tempYamlBuilder);
                        multiLineSourceStr = StringUtils.EMPTY;
                    }
                    encryptionRequired = false;
                    multiLineSource = false;
                } else if (multiLineSource) {
                    multiLineSourceStr = multiLineSourceStr.concat(StringUtils.SPACE).concat(line.trim());
                    continue;
                }
                encryptLine(line, encryptionRequired, multiLineSource, tempYamlBuilder);
            }
        }
    }

    /**
     * Encrypt the line if it's a part of source field (both multiline and single line) else keep as it is.
     */
    private static void encryptLine(String line, Boolean encryptionRequired, Boolean multiLineSource,
            StringBuilder tempYamlBuilder) throws IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {

        String[] values = line.split(SEPARATOR, 2);
        if (encryptionRequired && !line.contains(YAML_MULTILINE_IDENTIFIER)) {
            if (multiLineSource) {
                // Add the required spaces to have a valid and readable YAML file. Applicable for multiline source.
                // Encrypt line as a whole as its a part of the SQL query.
                tempYamlBuilder.append(SOURCE_FIELD_SPACE).append(AUTO_ENCRYPTED_TEXT)
                               .append(encrypt(line))
                               .append(END_LINE_CHARACTER);
            } else {
                // For single line source, keep the Key(source) as it is and encrypt value(SQL query).
                // Append space for readable and valid YAML file.
                tempYamlBuilder.append(values[0]).append(SEPARATOR).append(StringUtils.SPACE)
                               .append(AUTO_ENCRYPTED_TEXT)
                               .append(encrypt(values[1]))
                               .append(END_LINE_CHARACTER);
            }
        } else {
            tempYamlBuilder.append(line).append(END_LINE_CHARACTER);
        }
    }

    public static void main(String ar[]) {
        try {
            processFile();
        } catch (IOException | IllegalBlockSizeException | BadPaddingException e) {
            log.error("Error while processing the file ", e);
        }
    }

}