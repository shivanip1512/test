package com.cannontech.analysis.service;

import com.cannontech.analysis.data.group.SimpleReportGroup;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.model.DeviceGroup;

public interface ReportGroupService {

    /**
     * This method returns a SimpleReportGroup for device that is a member of base deviceGroup. 
     * @param base
     * @param device
     * @return
     */
    public SimpleReportGroup getSimpleGroupMembership(DeviceGroup base, YukonDevice device);
    
    /**
     * This method returns a SimpleReportGroup for device that is a member of the SystemGroupEnum group.
     * @param systemGroupEnum
     * @param device
     * @return
     */
    public SimpleReportGroup getSimpleGroupMembership(SystemGroupEnum systemGroupEnum, YukonDevice device);
    
    /**
     * This method returns a String for the GroupName with the parent group name removed.
     * @return
     */
    public String getPartialGroupName(SystemGroupEnum systemGroupEnum, SimpleReportGroup simpleReportGroup);
}
