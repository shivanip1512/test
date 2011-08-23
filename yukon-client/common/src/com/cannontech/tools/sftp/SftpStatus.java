package com.cannontech.tools.sftp;

import com.cannontech.common.i18n.DisplayableEnum;

public enum SftpStatus implements DisplayableEnum {
    SENDING,
    BAD_USER_PASS,
    SEND_ERROR, 
    SUCCESS;

    private static final String keyPrefix =
        "yukon.web.modules.support.supportBundle.ftp.status.";

    @Override
    public String getFormatKey() {
        return keyPrefix + name();
    }
}
