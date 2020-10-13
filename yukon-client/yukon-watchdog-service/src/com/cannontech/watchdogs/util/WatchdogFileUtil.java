package com.cannontech.watchdogs.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import org.apache.commons.io.FileUtils;

import com.cannontech.common.util.CtiUtilities;

public class WatchdogFileUtil {

    private static final String SUBSCRIBER_EMAIL_IDS = "subscriberEmailIds";
    private static final String SEPARATOR = ":";

    public void writeToFile(String subscriberEmailIds) {

        try {
            // file name : Subscribersinfo.txt
            String yukonBase = CtiUtilities.getYukonBase();
            File file = new File(yukonBase, "/Server/Config/System/Subscribersinfo.txt");
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdir();
            }
            file.createNewFile();
            String SubscriberIdText = SUBSCRIBER_EMAIL_IDS + SEPARATOR + WatchdogFileCryptoUtil.encrypt(subscriberEmailIds);
            List<String> lines = Arrays.asList(SubscriberIdText);
            Files.write(file.toPath(), lines, StandardCharsets.UTF_8);

        } catch (IOException | IllegalBlockSizeException | BadPaddingException e) {
        }

    }

}
