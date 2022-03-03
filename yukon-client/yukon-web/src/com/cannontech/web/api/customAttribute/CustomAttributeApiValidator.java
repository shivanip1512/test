package com.cannontech.web.api.customAttribute;

import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.api.error.model.ApiErrorDetails;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.attribute.model.CustomAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonApiValidationUtils;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;

@Service
public class CustomAttributeApiValidator extends SimpleValidator<CustomAttribute> {

    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    private MessageSourceAccessor accessor;

    @PostConstruct
    public void init() {
        accessor = messageResolver.getMessageSourceAccessor(YukonUserContext.system);
    }

    public CustomAttributeApiValidator() {
        super(CustomAttribute.class);
    }

    @Override
    protected void doValidation(CustomAttribute attribute, Errors errors) {
        String nameI18nText = accessor.getMessage("yukon.web.modules.adminSetup.config.attributes.attributeName");
        YukonApiValidationUtils.checkIsBlank(errors, "name", attribute.getName(), nameI18nText, false);

        if (!errors.hasFieldErrors("name")) {
            String attributeNameWithoutSpace = attribute.getName().trim();
            YukonApiValidationUtils.checkExceedsMaxLength(errors, "name", attribute.getName(), 60);
            YukonApiValidationUtils.checkBlacklistedCharacter(errors, "name", attribute.getName(), nameI18nText);
            
            Integer customAttributeId = attribute.getCustomAttributeId();
            AttributeService.customAttributes.asMap()
                    .values()
                    .stream()
                    .filter(attr -> attr.getName().equalsIgnoreCase(attributeNameWithoutSpace))
                    .findAny()
                    .ifPresent(cattr -> {
                        if (customAttributeId == null || cattr.getCustomAttributeId() != customAttributeId) {
                            errors.rejectValue("name", ApiErrorDetails.ALREADY_EXISTS.getCodeString(),
                                    new Object[] { attributeNameWithoutSpace }, "");
                        }
                    });
        }
    }
}