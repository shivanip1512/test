package com.cannontech.web.dashboards;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.web.common.dashboard.dao.DashboardDao;
import com.cannontech.web.common.dashboard.model.Dashboard;

@Service
public class DashboardValidator extends SimpleValidator<Dashboard> {

    @Autowired DashboardDao dashboardDao;


    public DashboardValidator() {
        super(Dashboard.class);
    }

    @Override
    public void doValidation(Dashboard dashboard, Errors errors) {

        validateName(dashboard, errors);
        YukonValidationUtils.checkExceedsMaxLength(errors, "description", dashboard.getDescription(), 500);
    }

    private void validateName(Dashboard dashboard, Errors errors) {

        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "yukon.web.error.isBlank");

        if (!errors.hasFieldErrors("name")) {
            if (!PaoUtils.isValidPaoName(dashboard.getName())) {
                errors.rejectValue("name", "yukon.web.error.paoName.containsIllegalChars");
            }
        }
        YukonValidationUtils.checkExceedsMaxLength(errors, "name", dashboard.getName(), 100);
        
        //TODO: check if name already exists
    }
}
