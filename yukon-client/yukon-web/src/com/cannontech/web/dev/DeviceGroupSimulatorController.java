/**
 * 
 */
package com.cannontech.web.dev;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.amr.meter.search.dao.MeterSearchDao;
import com.cannontech.amr.meter.search.model.FilterBy;
import com.cannontech.amr.meter.search.model.MeterSearchField;
import com.cannontech.amr.meter.search.model.MeterSearchOrderBy;
import com.cannontech.amr.meter.search.model.StandardFilterBy;
import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.device.groups.composed.dao.DeviceGroupComposedDao;
import com.cannontech.common.device.groups.dao.DeviceGroupType;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.amr.meter.MeterSearchUtils;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.security.annotation.CheckCparm;

@Controller
@CheckCparm(MasterConfigBoolean.DEVELOPMENT_MODE)
public class DeviceGroupSimulatorController {

    @Autowired private MeterSearchDao meterSearchDao;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private DeviceGroupEditorDao deviceGroupEditorDao;
    @Autowired private DeviceGroupComposedDao deviceGroupComposedDao;
    @Autowired private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    private static final String baseKey = "yukon.web.modules.dev.generateDeviceGroups";

    /**
     * Method generates given no of device groups and assigns given no devices to each group equaly
     * 
     * @param model
     * @param request
     * @return
     */
    @RequestMapping("/generateDeviceGroups")
    public String generateDeviceGroups(FlashScope flashScope, HttpServletRequest request) {
        String noOfGroups = ServletRequestUtils.getStringParameter(request, "noOfGroups", "1").trim();
        String noOfDevicesPerGroup = ServletRequestUtils.getStringParameter(request, "noOfDevices", "1").trim();
        String useNestedGroups = ServletRequestUtils.getStringParameter(request, "useNestedGroups", "No").trim();
        String deviceType = ServletRequestUtils.getStringParameter(request, "deviceType", "MCT").trim();
        List<YukonMeter> meters = null;
        List<RfnDevice> rfnLcrDeviceList = null;
        List<DisplayablePao> paoList = null;
        List<String> childGroupNames = new ArrayList<String>();
        int totalNoOfDevicesToBeAssigned = Integer.parseInt(noOfGroups) * Integer.parseInt(noOfDevicesPerGroup);

        if (deviceType.equalsIgnoreCase("MCT")) {
            meters = getMctDevices(request, totalNoOfDevicesToBeAssigned);
            paoList = new ArrayList<DisplayablePao>(meters);
        } else if (deviceType.equalsIgnoreCase("LCR")) {
            rfnLcrDeviceList = rfnDeviceDao.getDevicesByPaoTypes(PaoType.getRfLcrTypes());
            paoList = new ArrayList<DisplayablePao>(rfnLcrDeviceList);
        }
        if (paoList == null || paoList.size() < totalNoOfDevicesToBeAssigned) {
            flashScope.setMessage(new YukonMessageSourceResolvable(baseKey + ".insufficientDevices"),
                FlashScopeMessageType.ERROR);
            return "redirect:viewDeviceGroupSimulator";
        }

        for (int i = 1; i <= Integer.parseInt(noOfGroups); i++) {
            String childGrpName = RandomStringUtils.randomAlphanumeric(10);
            childGroupNames.add(childGrpName);
        }

        // create groups
        List<StoredDeviceGroup> newGroupsCreated = null;
        if (useNestedGroups.equalsIgnoreCase("No")) {
            newGroupsCreated = createBasicGroups(request, childGroupNames);
        } else if (useNestedGroups.equalsIgnoreCase("Yes")) {
            newGroupsCreated = createNestedGroups(request, childGroupNames);
        }

        assignDevicesToGroups(Integer.parseInt(noOfDevicesPerGroup), newGroupsCreated, paoList);
        flashScope.setMessage(new YukonMessageSourceResolvable(baseKey + ".success"), FlashScopeMessageType.SUCCESS);
        return "redirect:viewDeviceGroupSimulator";
    }

    private void assignDevicesToGroups(int noOfDevicesPerGroup, List<StoredDeviceGroup> newGroupsCreated,
            List<DisplayablePao> paoList) {
        int i = 0;
        int j = noOfDevicesPerGroup - 1;
        // assign devices
        for (StoredDeviceGroup storedDeviceGroup : newGroupsCreated) {
            if (paoList != null && i <= (paoList.size() - noOfDevicesPerGroup)) {
                List<DisplayablePao> sublist = paoList.subList(i, j + 1);
                deviceGroupMemberEditorDao.addDevices(storedDeviceGroup, sublist.iterator());
                i = j + 1;
                j = j + noOfDevicesPerGroup;
            }
        }
    }

    private List<YukonMeter> getMctDevices(HttpServletRequest request, int totalNoOfDevicesToBeAssigned) {
        List<FilterBy> filterByList = new ArrayList<FilterBy>(1);
        filterByList.add(new StandardFilterBy("deviceType", MeterSearchField.TYPE));
        // query filter
        List<FilterBy> queryFilter = MeterSearchUtils.getQueryFilter(request, filterByList);
        MeterSearchOrderBy orderBy = new MeterSearchOrderBy(MeterSearchField.PAONAME.toString(), true);

        // Get the required no of MCT meters
        SearchResults<YukonMeter> result = meterSearchDao.search(queryFilter, orderBy, 0, totalNoOfDevicesToBeAssigned);
        List<YukonMeter> mctMeters = result.getResultList();
        return mctMeters;
    }

    private List<StoredDeviceGroup> createBasicGroups(HttpServletRequest request, List<String> childGroupNames) {
        List<StoredDeviceGroup> newGroups = new ArrayList<StoredDeviceGroup>();
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        rolePropertyDao.verifyProperty(YukonRoleProperty.DEVICE_GROUP_MODIFY, userContext.getYukonUser());

        String groupName = "/";
        DeviceGroup group = deviceGroupService.resolveGroupName(groupName);
        if (group.isModifiable()) {
            for (String childGroupName : childGroupNames) {
                childGroupName = childGroupName.trim();
                StoredDeviceGroup storedGroup = deviceGroupEditorDao.getStoredGroup(group);

                // PLAIN STATIC GROUP
                StoredDeviceGroup newGroup =
                    deviceGroupEditorDao.addGroup(storedGroup, DeviceGroupType.STATIC, childGroupName);
                newGroups.add(newGroup);
            }
        }
        return newGroups;
    }

    private List<StoredDeviceGroup> createNestedGroups(HttpServletRequest request, List<String> childGroupNames) {
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        rolePropertyDao.verifyProperty(YukonRoleProperty.DEVICE_GROUP_MODIFY, userContext.getYukonUser());
        List<StoredDeviceGroup> newGroups = new ArrayList<StoredDeviceGroup>();

        List<DeviceGroupNode> parentNodeList = new ArrayList<DeviceGroupNode>();
        for (int i = 0; i < childGroupNames.size(); i++) {
            if (i == 0) {
                // First Device group will have root group as the parent with name "/"
                DeviceGroupNode node = new DeviceGroupNode("1", "0", "0", "/", "/" + childGroupNames.get(i));
                parentNodeList.add(node);
                StoredDeviceGroup newGroup = createNestedDeviceGroup("/", childGroupNames.get(i));
                newGroups.add(newGroup);
            } else {
                DeviceGroupNode node = null;
                for (int j = 0; j < parentNodeList.size(); j++) {
                    if (node == null) {
                        DeviceGroupNode deviceGroupNode = parentNodeList.get(j);
                        String left = deviceGroupNode.getLeftChild();
                        String right = deviceGroupNode.getRightChild();
                        if (left.equals("0")) {
                            // attach the child group to the left of the parent node
                            node =
                                new DeviceGroupNode((i + 1) + "", "0", "0", deviceGroupNode.getId(),
                                    deviceGroupNode.getRootName() + "/" + childGroupNames.get(i));

                            deviceGroupNode.setLeftChild("1");
                            String parent = deviceGroupNode.getRootName();
                            parentNodeList.add(node);
                            StoredDeviceGroup newGroup = createNestedDeviceGroup(parent, childGroupNames.get(i));
                            newGroups.add(newGroup);
                        } else if (right.equals("0")) {
                            // attach the child group to the right of the parent node
                            node =
                                new DeviceGroupNode((i + 1) + "", "0", "0", deviceGroupNode.getId(),
                                    deviceGroupNode.getRootName() + "/" + childGroupNames.get(i));

                            deviceGroupNode.setRightChild("1");
                            String parent = deviceGroupNode.getRootName();
                            parentNodeList.add(node);
                            StoredDeviceGroup newGroup = createNestedDeviceGroup(parent, childGroupNames.get(i));
                            newGroups.add(newGroup);
                        }
                    }
                }
            }

        }
        return newGroups;
    }

    private StoredDeviceGroup createNestedDeviceGroup(String parent, String childGroupName) {
        StoredDeviceGroup newGroup = null;
        String groupName = parent;
        DeviceGroup group = deviceGroupService.resolveGroupName(groupName);
        if (group.isModifiable()) {
            childGroupName = childGroupName.trim();

            StoredDeviceGroup storedGroup = deviceGroupEditorDao.getStoredGroup(group);

            newGroup = deviceGroupEditorDao.addGroup(storedGroup, DeviceGroupType.STATIC, childGroupName);
        }
        return newGroup;

    }

}
