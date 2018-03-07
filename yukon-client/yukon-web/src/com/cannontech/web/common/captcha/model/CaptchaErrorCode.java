package com.cannontech.web.common.captcha.model;

import java.util.List;
import org.apache.commons.collections4.CollectionUtils;

import com.cannontech.common.i18n.DisplayableEnum;

public enum CaptchaErrorCode implements DisplayableEnum {
    NO_ERRORS(true),
    MISSING_INPUT_SECRET(false),
    INVALID_INPUT_SECRET(false),
    MISSING_INPUT_RESPONSE(false),
    INVALID_INPUT_RESPONSE(false),
    BAD_REQUEST(false),
    INVALID_HOST(false);

    private boolean valid;
    private static final String displayKeyPrefix = "yukon.web.captcha.captchaErrorCode.";

    private CaptchaErrorCode(boolean valid) {
        this.valid = valid;
    }

    public boolean isValid() {
        return this.valid;
    }

    @Override
    public String getFormatKey() {
        return displayKeyPrefix + name();
    }

    public static CaptchaErrorCode getByCaptchaResponse(List<String> captchaErrorCodeList) {
        if (CollectionUtils.isEmpty(captchaErrorCodeList)) {
            return NO_ERRORS;
        }

        String captchaResponseUpper = captchaErrorCodeList.get(0).toUpperCase().replaceAll("-", "_");
        return valueOf(captchaResponseUpper);
    }
}