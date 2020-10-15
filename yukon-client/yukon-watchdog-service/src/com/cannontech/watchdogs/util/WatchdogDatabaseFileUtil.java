package com.cannontech.watchdogs.util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
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
import com.cannontech.common.config.SmtpEncryptionType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

public class WatchdogDatabaseFileUtil {

    private static final Logger log = YukonLogManager.getLogger(WatchdogDatabaseFileUtil.class);

    @Autowired private GlobalSettingDao globalSettingDao;

    public static final String SUBSCRIBER_EMAIL_IDS = "subscriber_email_ids";
    public static final String SMTP_HOST = "smtp_host";
    public static final String SMTP_PORT = "smtp_port";
    public static final String SMTP_ENCRYPTION_TYPE = "smtp_encryption_ype";
    public static final String SMTP_USERNAME = "smtp_username";
    public static final String SMTP_PASSWORD = "smtp_password";
    public static final String SEPARATOR = ":";
    public static final String MAIL_FROM_ADDRESS = "mail_from_address";

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
            List<String> smtpMetaData = new ArrayList<String>();
            smtpMetaData.add(WatchdogCryptoUtil.encrypt(SUBSCRIBER_EMAIL_IDS).concat(SEPARATOR)
                    .concat(WatchdogCryptoUtil.encrypt(subscriberEmailIds)));
            smtpMetaData.addAll(getSmtpDetails());
            Files.write(SMTP_META_DATA_FILE.toPath(), smtpMetaData, StandardCharsets.UTF_8);
        } catch (IOException | IllegalBlockSizeException | BadPaddingException e) {
            log.error("Error writting encrypted SMTP meta data to file", e);
        }
    }

    /**
     * Method to retrieve SMTP metadata from database.
     */
    private List<String> getSmtpDetails() throws IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        List<String> notificationInfo = new ArrayList<String>();
        notificationInfo.add(WatchdogCryptoUtil.encrypt(SMTP_HOST).concat(SEPARATOR)
                .concat(WatchdogCryptoUtil.encrypt(globalSettingDao.getString(GlobalSettingType.SMTP_HOST))));
        notificationInfo.add(WatchdogCryptoUtil.encrypt(SMTP_PORT).concat(SEPARATOR)
                .concat(WatchdogCryptoUtil.encrypt(globalSettingDao.getString(GlobalSettingType.SMTP_PORT))));
        notificationInfo.add(WatchdogCryptoUtil.encrypt(SMTP_ENCRYPTION_TYPE).concat(SEPARATOR).concat(
                globalSettingDao.getEnum(GlobalSettingType.SMTP_ENCRYPTION_TYPE, SmtpEncryptionType.class).getProtocol()));
        notificationInfo.add(WatchdogCryptoUtil.encrypt(SMTP_USERNAME).concat(SEPARATOR)
                .concat(WatchdogCryptoUtil.encrypt(globalSettingDao.getString(GlobalSettingType.SMTP_USERNAME))));
        notificationInfo.add(WatchdogCryptoUtil.encrypt(SMTP_PASSWORD).concat(SEPARATOR)
                .concat(WatchdogCryptoUtil.encrypt(globalSettingDao.getString(GlobalSettingType.SMTP_PASSWORD))));
        notificationInfo.add(WatchdogCryptoUtil.encrypt(MAIL_FROM_ADDRESS).concat(SEPARATOR)
                .concat(WatchdogCryptoUtil.encrypt(globalSettingDao.getString(GlobalSettingType.MAIL_FROM_ADDRESS))));
        return notificationInfo;
    }

    /**
     * Methods to read the SMTP meta data from the file.
     */
    public static Map<String, String> readFromFile() throws IOException {
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
        }
        return metadataMap;
    }
}
