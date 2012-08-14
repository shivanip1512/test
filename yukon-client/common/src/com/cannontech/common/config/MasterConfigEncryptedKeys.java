package com.cannontech.common.config;

import java.util.HashSet;
import java.util.Set;

public class MasterConfigEncryptedKeys {
    private final static Set<String> encryptedKeys = new HashSet<String>();

    static {
        encryptedKeys.add("DB_USERNAME");
        encryptedKeys.add("DB_PASSWORD");
        encryptedKeys.add("DB_SQLSERVER");
        encryptedKeys.add("DB_SQLSERVER_HOST");
        encryptedKeys.add("DB_JAVA_URL");
    }
    
    public static boolean isEncryptable(String key) {
        return encryptedKeys.contains(key);
    }
}
