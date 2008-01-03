package com.cannontech.database.db.device;

import java.sql.SQLException;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.service.FixedDeviceGroupingHack;
import com.cannontech.common.device.groups.service.FixedDeviceGroups;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.spring.YukonSpringHook;

public class DeviceGroupMember extends DBPersistent {
    
    private YukonDevice yukonDevice = null;
    private String collectionGroup = com.cannontech.common.util.CtiUtilities.STRING_DEFAULT;
    private String alternateGroup = com.cannontech.common.util.CtiUtilities.STRING_DEFAULT;
    private String billingGroup = com.cannontech.common.util.CtiUtilities.STRING_DEFAULT;
    private String customGroup1 = com.cannontech.common.util.CtiUtilities.STRING_DEFAULT;
    private String customGroup2 = com.cannontech.common.util.CtiUtilities.STRING_DEFAULT;
    private String customGroup3 = com.cannontech.common.util.CtiUtilities.STRING_DEFAULT;
    FixedDeviceGroupingHack hacker = (FixedDeviceGroupingHack) YukonSpringHook.getBean("fixedDeviceGroupingHack"); 
    
    public static final String CONSTRAINT_COLUMNS[] = { "YukonPaoID" };
    public static final String TABLE_NAME = "DeviceGroupMember";
    
    public DeviceGroupMember() 
    {
        super();
    }
    
    public DeviceGroupMember(YukonDevice yd, 
                                   String billingGroup, 
                                   String collectionGroup, 
                                   String alternateGroup, 
                                   String customGroup1, 
                                   String customGroup2, 
                                   String customGroup3) {
        this.yukonDevice = yd;
        this.collectionGroup = collectionGroup;
        this.alternateGroup = alternateGroup;
        this.billingGroup = billingGroup;
        this.customGroup1 = customGroup1;
        this.customGroup2 = customGroup2;
        this.customGroup3 = customGroup3;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void add() throws SQLException {
        // TODO Auto-generated method stub
        hacker.setGroup(FixedDeviceGroups.BILLINGGROUP, yukonDevice, billingGroup);
        hacker.setGroup(FixedDeviceGroups.COLLECTIONGROUP, yukonDevice, collectionGroup);
        hacker.setGroup(FixedDeviceGroups.TESTCOLLECTIONGROUP, yukonDevice, alternateGroup);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void delete() throws SQLException {
        // TODO Auto-generated method stub
        hacker.setGroup(FixedDeviceGroups.BILLINGGROUP, yukonDevice, null);
        hacker.setGroup(FixedDeviceGroups.COLLECTIONGROUP, yukonDevice, null);
        hacker.setGroup(FixedDeviceGroups.TESTCOLLECTIONGROUP, yukonDevice, null);
    }

    @Override
    public void retrieve() throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void update() throws SQLException {
        // TODO Auto-generated method stub

    }

}
