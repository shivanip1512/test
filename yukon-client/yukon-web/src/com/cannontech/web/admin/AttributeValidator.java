package com.cannontech.web.admin;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.pao.attribute.model.CustomAttribute;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;

@Service
public class AttributeValidator extends SimpleValidator<CustomAttribute> {
    
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    private MessageSourceAccessor accessor;
    
    @PostConstruct
    public void init() {
        accessor = messageResolver.getMessageSourceAccessor(YukonUserContext.system);
    }

    public AttributeValidator() {
        super(CustomAttribute.class);
    }

    @Override
    protected void doValidation(CustomAttribute attribute, Errors errors) {
        String nameI18nText = accessor.getMessage("yukon.web.modules.adminSetup.config.attributes.attributeName");
        YukonValidationUtils.checkIsBlank(errors, "name", attribute.getName(), nameI18nText, false);

        if (!errors.hasFieldErrors("name")) {
            YukonValidationUtils.checkExceedsMaxLength(errors, "name", attribute.getName(), 60);
            if (StringUtils.containsAny(attribute.getName(), PaoUtils.ILLEGAL_NAME_CHARS)) {
                errors.rejectValue("name", "yukon.web.error.paoName.containsIllegalChars");
            }
        }
    }
}