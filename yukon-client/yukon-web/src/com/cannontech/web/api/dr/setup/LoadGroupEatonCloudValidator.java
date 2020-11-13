package com.cannontech.web.api.dr.setup;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.setup.LoadGroupEatonCloud;

@Service
public class LoadGroupEatonCloudValidator extends LoadGroupSetupValidator<LoadGroupEatonCloud> {

    private final static String key = "yukon.web.modules.dr.setup.loadGroup.error.";

    public LoadGroupEatonCloudValidator() {
        super(LoadGroupEatonCloud.class);
    }

    @Override
    public boolean supports(Class clazz) {
        return LoadGroupEatonCloud.class.isAssignableFrom(clazz);
    }

    @Override
    protected void doValidation(LoadGroupEatonCloud loadGroup, Errors errors) {
        if (loadGroup.getRelayUsage() == null || CollectionUtils.isEmpty(loadGroup.getRelayUsage())) {
            errors.rejectValue("relayUsage", key + "eatonCloudLoadRequired");
        }
    }
}
