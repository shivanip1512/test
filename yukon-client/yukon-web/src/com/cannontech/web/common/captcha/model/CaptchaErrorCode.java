package com.cannontech.web.common.captcha.model;

import java.util.Arrays;
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
    INVALID_HOST(false),
    INCORRECT_CAPTCHA_SOL(false),
    TIMEOUT_OR_DUPLICATE(false),
    DEFAULT_CAPTCHA_ERROR_CODE(false);

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
        } else {
            String captchaResponseUpper = captchaErrorCodeList.get(0).toUpperCase().replaceAll("-", "_");
            if (Arrays.stream(CaptchaErrorCode.values()).anyMatch(
                captchaResponse -> captchaResponseUpper.equals(captchaResponse.toString()))) {
                return valueOf(captchaResponseUpper);
            } else {
                return DEFAULT_CAPTCHA_ERROR_CODE;
            }
        }
    }
}