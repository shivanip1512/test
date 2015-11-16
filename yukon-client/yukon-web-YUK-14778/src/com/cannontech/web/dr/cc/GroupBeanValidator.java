package com.cannontech.web.dr.cc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.cc.dao.GroupDao;
import com.cannontech.cc.model.GroupBean;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;

public class GroupBeanValidator extends SimpleValidator<GroupBean> {
    @Autowired private GroupDao groupDao;
    private static final String keyBase = "yukon.web.modules.dr.cc.groupCreate.";
    
    public GroupBeanValidator() {
        super(GroupBean.class);
    }

    @Override
    protected void doValidation(GroupBean group, Errors errors) {
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", keyBase + "required.name");
        YukonValidationUtils.checkExceedsMaxLength(errors, "name", group.getName(), 255);
    }
    
}
