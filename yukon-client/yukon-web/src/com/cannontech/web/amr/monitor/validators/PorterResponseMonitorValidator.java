package com.cannontech.web.amr.monitor.validators;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.amr.porterResponseMonitor.dao.PorterResponseMonitorDao;
import com.cannontech.amr.porterResponseMonitor.model.PorterResponseMonitor;
import com.cannontech.amr.porterResponseMonitor.model.PorterResponseMonitorErrorCode;
import com.cannontech.amr.porterResponseMonitor.model.PorterResponseMonitorRule;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.groups.util.DeviceGroupUtil;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.google.common.collect.Lists;

@Service
public class PorterResponseMonitorValidator extends SimpleValidator<PorterResponseMonitor> {

    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private PorterResponseMonitorDao porterResponseMonitorDao;
    private final static String baseKey = "yukon.web.modules.amr.porterResponseMonitor";

    public PorterResponseMonitorValidator() {
        super(PorterResponseMonitor.class);
    }

    @Override
    protected void doValidation(PorterResponseMonitor monitor, Errors errors) {
        validateName(monitor, errors);

        if (monitor.getMonitorId() != null) {
            validateRules(monitor, errors);
        }
    }

    private void validateRules(PorterResponseMonitor monitor, Errors errors) {
        if (deviceGroupService.findGroupName(monitor.getGroupName()) == null) {
            errors.rejectValue("groupName", "yukon.web.modules.amr.invalidGroupName");
        }

        /* uniqueness checks */
        List<Integer> orderList = Lists.newArrayList();
        List<PorterResponseMonitorRule> rules = monitor.getRules();
        for (int i = 0; i < rules.size(); i++) {
            PorterResponseMonitorRule rule = rules.get(i);
            List<PorterResponseMonitorErrorCode> errorCodes = rule.getErrorCodes();

            orderList.add(rule.getRuleOrder());

            // Error Code Uniqueness
            List<Integer> errorsList = Lists.newArrayList();
            for (PorterResponseMonitorErrorCode errorCode : errorCodes) {
                errorsList.add(errorCode.getErrorCode());
            }
            if (containsDuplicates(errorsList)) {
                // we have duplicate errors for this rule
                errors.reject(baseKey + ".rulesTable.errorCodesFormat");
            }
        }

        /**
         * Order Uniqueness check
         * which should not be a problem now that I am normalizing the order in the Constructor
         * -- keeping this here in case that normalization is ever changed / removed
         */
        if (containsDuplicates(orderList)) {
            // we have duplicate orders for this monitor
            errors.reject(baseKey + ".rulesTable.uniqueOrder");
        }
    }

    private void validateName(PorterResponseMonitor monitor, Errors errors) {
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "yukon.web.error.isBlank");
        if (!errors.hasFieldErrors("name")) {
            YukonValidationUtils.checkExceedsMaxLength(errors, "name", monitor.getName(), 60);
            // Check the monitor name does not contain any invalid characters as we use it in Violations Group
            if (!DeviceGroupUtil.isValidName(monitor.getName())) {
                errors.rejectValue("name", "yukon.web.error.deviceGroupName.containsIllegalChars");
            }
        }

        boolean nameAvailable = !porterResponseMonitorDao.monitorExistsWithName(monitor.getName());
        boolean idSpecified = monitor.getMonitorId() != null;

        if (!nameAvailable) {
            if (!idSpecified) {
                // For create, we must have an available name
                errors.rejectValue("name", "yukon.web.error.nameConflict");
            } else {
                // For edit, we can use our own existing name
                PorterResponseMonitor existingDeviceDataMonitor =
                    porterResponseMonitorDao.getMonitorById(monitor.getMonitorId());
                if (!existingDeviceDataMonitor.getName().equals(monitor.getName())) {
                    errors.rejectValue("name", "yukon.web.error.nameConflict");
                }
            }
        }
    }

    private <T> boolean containsDuplicates(List<T> list) {
        Set<T> set = new HashSet<T>(list);
        if (set.size() < list.size()) {
            // duplicates were removed
            return true;
        }
        return false;
    }

}
