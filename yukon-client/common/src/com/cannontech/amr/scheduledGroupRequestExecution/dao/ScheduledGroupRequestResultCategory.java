package com.cannontech.amr.scheduledGroupRequestExecution.dao;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.cannontech.common.i18n.DisplayableEnum;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

public enum ScheduledGroupRequestResultCategory implements DisplayableEnum {

    SUCCESS, 
    FAILED, 
    NOT_CONFIGURED, 
    UNSUPPORTED, 
    CANCELED
    ;

    @Override
    public String getFormatKey() {
        return "yukon.common.device.schedules.result.category." + this.name();
    }

    public static Set<ScheduledGroupRequestResultCategory> getCategories(String commaDelimitedCategories) {
        Set<ScheduledGroupRequestResultCategory> result = new HashSet<ScheduledGroupRequestResultCategory>();
        if (!StringUtils.isEmpty(commaDelimitedCategories)) {
            for (String category : Splitter.on(',').split(commaDelimitedCategories)) {
                result.add(ScheduledGroupRequestResultCategory.valueOf(category));
            }
        }
        return result;
    }

    public static String getCommaDelimitedString(Set<ScheduledGroupRequestResultCategory> categories) {
        if (categories.isEmpty()) {
            return null;
        }
        String values = Joiner.on(",").join(categories);
        return values;
    }
}
