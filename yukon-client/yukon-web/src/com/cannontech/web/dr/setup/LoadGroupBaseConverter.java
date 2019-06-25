package com.cannontech.web.dr.setup;

import org.springframework.core.convert.converter.Converter;

import com.cannontech.common.dr.setup.LoadGroupBase;
import com.cannontech.common.dr.setup.LoadGroupExpresscom;
import com.cannontech.common.dr.setup.LoadGroupItron;

/**
 * Converter class for load group base.
 * This will take groupType as string and return the appropriate object.
 * Converter is required when a inherited class have to be passed for a base class input.
 */
public class LoadGroupBaseConverter implements Converter<String, LoadGroupBase> {

    @Override
    public LoadGroupBase convert(String groupType) {
        LoadGroupBase loadGroup = null;
        switch (groupType) {
        case "LM_GROUP_EXPRESSCOMM":
        case "LM_GROUP_RFN_EXPRESSCOMM":
            loadGroup = new LoadGroupExpresscom();
            break;
        case "LM_GROUP_ITRON":
            loadGroup = new LoadGroupItron();
            break;
        default:
            loadGroup = new LoadGroupBase();

        }
        return loadGroup;
    }

}
