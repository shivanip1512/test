package com.cannontech.web.admin;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.attribute.model.AttributeAssignment;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;

@Service
public class AttributeAssignmentValidator extends SimpleValidator<AttributeAssignment> {
    
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    private MessageSourceAccessor accessor;
    
    @PostConstruct
    public void init() {
        accessor = messageResolver.getMessageSourceAccessor(YukonUserContext.system);
    }

    public AttributeAssignmentValidator() {
        super(AttributeAssignment.class);
    }

    @Override
    protected void doValidation(AttributeAssignment assignment, Errors errors) {
        String pointOffsetLabel = accessor.getMessage("yukon.common.pointOffset");
        YukonValidationUtils.checkIfFieldRequired("offset", errors, assignment.getOffset(), pointOffsetLabel);
    }
}