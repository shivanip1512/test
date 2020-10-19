package com.cannontech.watchdogs.util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.tools.smtp.SmtpMetadataCacheUtil;
import com.cannontech.tools.smtp.SmtpMetadataConstants;

public class WatchdogDatabaseFileUtil {

    private static final Logger log = YukonLogManager.getLogger(WatchdogDatabaseFileUtil.class);

    @Autowired private SmtpMetadataCacheUtil databaseCacheUtil;

    private static final String SEPARATOR = ":";
    private static final String FILE_NAME = "/Server/Config/System/SmtpMetadata.txt";
    private static File SMTP_META_DATA_FILE = null;
    static {
        try {
            String yukonBase = CtiUtilities.getYukonBase();
            SMTP_META_DATA_FILE = new File(yukonBase, FILE_NAME);
            if (!SMTP_META_DATA_FILE.getParentFile().exists()) {
                SMTP_META_DATA_FILE.getParentFile().mkdir();
            }
            SMTP_META_DATA_FILE.createNewFile();
        } catch (IOException e) {
            log.error("Error creating SMTP metadata file", e);
        }
    }

    /**
     * Method to write the metadata to the file.
     */
    public void writeToFile(String subscriberEmailIds) {
        try {
            List<String> smtpMetadata = new ArrayList<String>();
            databaseCacheUtil.update(SmtpMetadataConstants.SUBSCRIBER_EMAIL_IDS, subscriberEmailIds);
            smtpMetadata.addAll(getSmtpMetadataDetails());
            Files.write(SMTP_META_DATA_FILE.toPath(), smtpMetadata);
        } catch (IOException | IllegalBlockSizeException | BadPaddingException e) {
            log.error("Error writting encrypted SMTP metadata to file", e);
        }
    }

    /**
     * Method to retrieve SMTP metadata from database.
     */
    private List<String> getSmtpMetadataDetails()
            throws IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        List<String> notificationInfo = new ArrayList<String>();
        notificationInfo.add(WatchdogCryptoUtil.encrypt(SmtpMetadataConstants.SMTP_HOST)
                        .concat(SEPARATOR)
                        .concat(WatchdogCryptoUtil.encrypt(databaseCacheUtil.getValue(SmtpMetadataConstants.SMTP_HOST))));
        
        notificationInfo.add(WatchdogCryptoUtil.encrypt(SmtpMetadataConstants.SMTP_PORT)
                        .concat(SEPARATOR)
                        .concat(WatchdogCryptoUtil.encrypt(databaseCacheUtil.getValue(SmtpMetadataConstants.SMTP_PORT))));
        notificationInfo.add(WatchdogCryptoUtil.encrypt(SmtpMetadataConstants.SMTP_ENCRYPTION_TYPE)
                        .concat(SEPARATOR)
                        .concat(WatchdogCryptoUtil.encrypt(databaseCacheUtil.getValue(SmtpMetadataConstants.SMTP_ENCRYPTION_TYPE))));
        notificationInfo.add(WatchdogCryptoUtil.encrypt(SmtpMetadataConstants.SMTP_USERNAME)
                        .concat(SEPARATOR)
                        .concat(WatchdogCryptoUtil.encrypt(databaseCacheUtil.getValue(SmtpMetadataConstants.SMTP_USERNAME))));
        notificationInfo.add(WatchdogCryptoUtil.encrypt(SmtpMetadataConstants.SMTP_PASSWORD)
                        .concat(SEPARATOR)
                        .concat(WatchdogCryptoUtil.encrypt(databaseCacheUtil.getValue(SmtpMetadataConstants.SMTP_PASSWORD))));
        notificationInfo.add(WatchdogCryptoUtil.encrypt(SmtpMetadataConstants.MAIL_FROM_ADDRESS)
                        .concat(SEPARATOR)
                        .concat(WatchdogCryptoUtil.encrypt(databaseCacheUtil.getValue(SmtpMetadataConstants.MAIL_FROM_ADDRESS))));
        notificationInfo.add(WatchdogCryptoUtil.encrypt(SmtpMetadataConstants.SUBSCRIBER_EMAIL_IDS)
                        .concat(SEPARATOR)
                        .concat(WatchdogCryptoUtil.encrypt(databaseCacheUtil.getValue(SmtpMetadataConstants.SUBSCRIBER_EMAIL_IDS))));
        return notificationInfo;
    }

    /**
     * Methods to read the SMTP metadata from the file.
     */
    public static Map<String, String> readFromFile() {
        Map<String, String> metadataMap = new HashMap<String, String>();
        try (Stream<String> lines = Files.lines(Paths.get(SMTP_META_DATA_FILE.toURI()), Charset.defaultCharset())) {
            lines.forEach(line -> {
                try {
                    String tokens[] = line.split(SEPARATOR);
                    metadataMap.put(WatchdogCryptoUtil.decrypt(tokens[0]), WatchdogCryptoUtil.decrypt(tokens[1]));
                } catch (IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException e) {
                    log.error("Error retrieving SMTP meta data from file", e);
                }
            });
        } catch (IOException exception) {
            log.error("Error reading SmtpMetadata.txt file", exception);
        }
        return metadataMap;
    }
}
