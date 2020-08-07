package com.cannontech.web.admin;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.attribute.model.Assignment;
import com.cannontech.common.util.Range;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;

@Service
public class AssignmentValidator extends SimpleValidator<Assignment> {
    
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    private MessageSourceAccessor accessor;
    
    @PostConstruct
    public void init() {
        accessor = messageResolver.getMessageSourceAccessor(YukonUserContext.system);
    }

    public AssignmentValidator() {
        super(Assignment.class);
    }

    @Override
    protected void doValidation(Assignment assignment, Errors errors) {
        if (!errors.hasFieldErrors("offset")) {
            String pointOffsetLabel = accessor.getMessage("yukon.common.pointOffset");
            Range<Integer> range = Range.inclusive(0, 99999999);
            YukonValidationUtils.checkRange(errors, "offset", pointOffsetLabel, assignment.getOffset(), range, true);
        }
    }
}