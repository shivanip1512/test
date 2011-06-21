package com.cannontech.common.device.groups.dao.impl.providers;

import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.util.StringUtils;
import com.google.common.collect.ImmutableList;

public abstract class AttributeGroupProviderBase extends CompleteBinningDeviceGroupProviderBase<BuiltInAttribute> {
    private ImmutableList<BuiltInAttribute> allBins = ImmutableList.of(BuiltInAttribute.values());
    
    @Override
    protected ImmutableList<BuiltInAttribute> getAllBins() {
        return allBins;
    }

    @Override
    protected String getGroupName(BuiltInAttribute bin) {
        String groupName = StringUtils.removeInvalidDeviceGroupNameCharacters(bin.getDescription());
        return groupName;
    }
}
