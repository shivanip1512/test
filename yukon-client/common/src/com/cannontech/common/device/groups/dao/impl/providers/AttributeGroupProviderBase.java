package com.cannontech.common.device.groups.dao.impl.providers;

import java.util.List;

import com.cannontech.common.device.groups.util.DeviceGroupUtil;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.google.common.collect.ImmutableList;

public abstract class AttributeGroupProviderBase extends CompleteBinningDeviceGroupProviderBase<BuiltInAttribute> {

    private ImmutableList<BuiltInAttribute> allBins = ImmutableList.copyOf(BuiltInAttribute.values());
    
    @Override
    protected List<BuiltInAttribute> getAllBins() {
        return allBins;
    }

    @Override
    protected String getGroupName(BuiltInAttribute bin) {
    	return DeviceGroupUtil.removeInvalidDeviceGroupNameCharacters(bin.getDescription());
    }
}
