package com.cannontech.common.device.groups.dao.impl.providers;

import java.util.Arrays;
import java.util.List;

import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.util.StringUtils;

public abstract class AttributeGroupProviderBase extends CompleteBinningDeviceGroupProviderBase<BuiltInAttribute> {
    private List<BuiltInAttribute> allBins = Arrays.asList(BuiltInAttribute.values());
    
    @Override
    protected List<BuiltInAttribute> getAllBins() {
        return allBins;
    }

    @Override
    protected String getGroupName(BuiltInAttribute bin) {
        String groupName = StringUtils.removeInvalidDeviceGroupNameCharacters(bin.getDescription());
        return groupName;
    }
}
