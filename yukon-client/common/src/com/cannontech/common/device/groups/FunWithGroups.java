package com.cannontech.common.device.groups;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.dao.DeviceGroupDao;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.groups.service.FixedDeviceGroupingHack;
import com.cannontech.common.device.groups.service.FixedDeviceGroups;
import com.cannontech.spring.YukonSpringHook;

public class FunWithGroups {
    
    private static final DeviceGroupService deviceGroupService = YukonSpringHook.getBean("deviceGroupService", DeviceGroupService.class);
    private static final DeviceGroupDao deviceGroupDao = YukonSpringHook.getBean("deviceGroupDao", DeviceGroupDao.class);
    private static final FixedDeviceGroupingHack groupingHack = YukonSpringHook.getBean("fixedDeviceGroupingHack", FixedDeviceGroupingHack.class);

    @SuppressWarnings("deprecation")
    public static void main(String[] args) {
        DeviceGroup group;
        group = deviceGroupService.resolveGroupName("/Meters/Alternate/Alt B");
        assert group.getFullName().equals("/Meters/Alternate/Alt B");
        
        group = deviceGroupService.resolveGroupName("/Meters/Billing/Billing B");
        assert group.getName().equals("Billing B");
        
        List<YukonDevice> devices = deviceGroupService.getDevices(group);
        for (YukonDevice device : devices) {
            System.out.println(device);
            
        }
        
        List<Integer> deviceIds = deviceGroupService.getDeviceIds(group);
        assert deviceIds.contains(124);
        assert deviceIds.contains(154);
        
        
        DeviceGroup rootGroup = deviceGroupService.getRootGroup();
        
        printGroupTree(rootGroup, 0);
        
        // play with fixed groups
        List<YukonDevice> devices2 = groupingHack.getDevices(FixedDeviceGroups.BILLING, "Billing Z");
        for (YukonDevice device : devices2) {
            groupingHack.setGroup(FixedDeviceGroups.BILLING, device, null);
            
            List<YukonDevice> devices3 = groupingHack.getDevices(FixedDeviceGroups.BILLING, "Billing Z");
            assert !devices3.contains(device);
        }
        
        printGroupTree(rootGroup, 0);
        
        for (YukonDevice device : devices2) {
            groupingHack.setGroup(FixedDeviceGroups.BILLING, device, "Billing A");
        }
        
        printGroupTree(rootGroup, 0);

    }

    private static void printGroupTree(DeviceGroup rootGroup, int indent) {
        int mult = 2;
        System.out.print(StringUtils.repeat(" ", indent * mult));
        System.out.println(rootGroup.getName() + "/");
        
        List<? extends DeviceGroup> childGroups = deviceGroupDao.getChildGroups(rootGroup);
        Collections.sort(childGroups, new Comparator<DeviceGroup>() {
            public int compare(DeviceGroup o1, DeviceGroup o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });
        for (DeviceGroup group : childGroups) {
            printGroupTree(group, indent + 1);
        }
        
        List<YukonDevice> childDevices = deviceGroupDao.getChildDevices(rootGroup);
        for (YukonDevice device : childDevices) {
            System.out.print(StringUtils.repeat(" ", (indent + 1) * mult));
            System.out.println(device);
        }
    }

}
