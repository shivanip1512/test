package com.cannontech.common.device.groups.util;

import org.apache.commons.lang.StringUtils;

import com.cannontech.common.device.groups.IllegalGroupNameException;

public class DeviceGroupUtil {
    public final static char[] ILLEGAL_NAME_CHARS = {'\\', '/'};

    // Prevent instantiation.
    private DeviceGroupUtil() {}

    /**
     * See /yukon-web/WebContent/WEB-INF/pages/group/home.jsp JavaScript function
     * "isValidGroupName()" for client side implementation.
     */
    public static boolean isValidName(String groupName) {
        return !StringUtils.isBlank(groupName)
            && StringUtils.containsNone(groupName, ILLEGAL_NAME_CHARS);
    }

    public static void validateName(String groupName) throws IllegalGroupNameException {
        if (!isValidName(groupName)) {
            throw new IllegalGroupNameException(groupName);
        }
    }

    /**
     * Returns a string that has invalid DeviceGroup name characters removed and 
     * replaced with underscores.
     * @param string
     * @return
     */
    public static String removeInvalidDeviceGroupNameCharacters(String string) {
        String result = string;
        // what follows is not a perfect solution, it could produce duplicates
        // but this is unlikely in practice and a better solution would probably
        // involve generating really ugly group names (8.3 window's file names???)
        for (char badCharacter : ILLEGAL_NAME_CHARS) {
            result = result.replace(badCharacter, '_');
        }
        return result;
    }
}
