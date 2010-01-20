package com.cannontech.amr.deviceread.service;

import java.util.Set;

import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.CommandRequestExecutionType;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.google.common.collect.Multimap;

public interface MeterReadCommandGeneratorService {

    /**
     * Get a Multimap of YukonDevice-to-CommandRequestDevice collection for a given set of Attributes.
     * A device will not appear as a key in the result Multimap if does not support an Attribute.
     * @param devices
     * @param attributes
     * @param type
     * @return
     */
    public Multimap<YukonDevice, CommandRequestDevice> getCommandRequests(Iterable<? extends YukonDevice> devices, Set<? extends Attribute> attributes, CommandRequestExecutionType type);

    public boolean isReadable(YukonDevice device, Set<? extends Attribute> attributes);
}
