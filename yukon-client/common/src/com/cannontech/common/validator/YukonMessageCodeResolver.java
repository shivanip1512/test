package com.cannontech.common.validator;

import org.springframework.validation.DefaultMessageCodesResolver;

public class YukonMessageCodeResolver extends DefaultMessageCodesResolver {
    public YukonMessageCodeResolver(String prefix) {
        setPrefix(prefix);
    }

    @Override
    protected String postProcessMessageCode(String code) {
        // Messages generated using YukonValidationUtils
        // can't use the prefix.
        if (code != null && code.startsWith("yukon.web.")) {
            return code;
        }
        return super.postProcessMessageCode(code);
    }
}
