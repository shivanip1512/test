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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

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
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
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
    private static final String baseKey = "yukon.web.modules.dev.deviceGroupSimulator";

    /**
     * Method generates given no of device groups and assigns given no devices to each group equally
     */
    @RequestMapping(value = "/generateDeviceGroups", method = RequestMethod.POST)
    public String generateDeviceGroups(FlashScope flashScope, LiteYukonUser user,
            @RequestParam(defaultValue = "1") int noOfGroups,
            @RequestParam(defaultValue = "1") int noOfDevicesPerGroup,
            @RequestParam(defaultValue = "false") boolean useNestedGroups,
            @RequestParam(defaultValue = "MCT") String deviceType, HttpServletRequest request) {
        List<YukonMeter> meters = null;
        List<RfnDevice> rfnLcrDeviceList = null;
        List<DisplayablePao> paoList = null;
        List<String> childGroupNames = new ArrayList<String>();
        int totalNoOfDevicesToBeAssigned = noOfGroups * noOfDevicesPerGroup;

        if (deviceType.equalsIgnoreCase("MCT")) {
            meters = getMctDevices(request, totalNoOfDevicesToBeAssigned);
            paoList = new ArrayList<DisplayablePao>(meters);
        } else if (deviceType.equalsIgnoreCase("LCR")) {
            rfnLcrDeviceList = rfnDeviceDao.getDevicesByPaoTypes(PaoType.getRfLcrTypes());
            paoList = new ArrayList<DisplayablePao>(rfnLcrDeviceList);
        }
        if (paoList == null || paoList.size() < totalNoOfDevicesToBeAssigned) {
            flashScope.setError(new YukonMessageSourceResolvable(baseKey + ".insufficientDevices"));
            return "redirect:viewDeviceGroupSimulator";
        }

        for (int i = 1; i <= noOfGroups; i++) {
            String childGrpName = RandomStringUtils.randomAlphanumeric(10);
            childGroupNames.add(childGrpName);
        }

        // create groups
        List<StoredDeviceGroup> newGroupsCreated = null;
        if (useNestedGroups) {
            newGroupsCreated = createNestedGroups(user, childGroupNames);
        } else {
            newGroupsCreated = createBasicGroups(user, childGroupNames);
        }

        assignDevicesToGroups(noOfDevicesPerGroup, newGroupsCreated, paoList);
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

    private List<StoredDeviceGroup> createBasicGroups(LiteYukonUser user, List<String> childGroupNames) {
        List<StoredDeviceGroup> newGroups = new ArrayList<StoredDeviceGroup>();
        rolePropertyDao.verifyProperty(YukonRoleProperty.DEVICE_GROUP_MODIFY, user);

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

    private List<StoredDeviceGroup> createNestedGroups(LiteYukonUser user, List<String> childGroupNames) {
        rolePropertyDao.verifyProperty(YukonRoleProperty.DEVICE_GROUP_MODIFY, user);
        List<StoredDeviceGroup> newGroups = new ArrayList<StoredDeviceGroup>();

        List<DeviceGroupNode> parentNodeList = new ArrayList<>();
        if (childGroupNames.size() > 0) {
            // First Device group will have root group as the parent with name "/"
            DeviceGroupNode firstNode = new DeviceGroupNode("1", "/", "/" + childGroupNames.get(0));
            parentNodeList.add(firstNode);
            StoredDeviceGroup firstGroup = createNestedDeviceGroup("/", childGroupNames.get(0));
            newGroups.add(firstGroup);
        }

        for (int i = 1; i < childGroupNames.size(); i++) {
            for (int j = 0; j < parentNodeList.size(); j++) {
                // if (node == null) {
                DeviceGroupNode parentGroupNode = parentNodeList.get(j);
                if (!parentGroupNode.hasLeftChild()) {
                    // attach the child group to the left of the parent node
                    attachChildGroup(true, i + 1, parentGroupNode, childGroupNames.get(i), parentNodeList, newGroups);
                    break;
                } else if (!parentGroupNode.hasRightChild()) {
                    // attach the child group to the right of the parent node
                    attachChildGroup(false, i + 1, parentGroupNode, childGroupNames.get(i), parentNodeList, newGroups);
                    break;
                }
                // }
            }
        }
        return newGroups;
    }

    private void attachChildGroup(boolean isLeft, Integer id, DeviceGroupNode parentNode, String childGroupName,
            List<DeviceGroupNode> parentNodeList, List<StoredDeviceGroup> newGroups) {
        DeviceGroupNode node =
            new DeviceGroupNode(id.toString(), parentNode.getId(), parentNode.getRootName() + "/" + childGroupName);
        if (isLeft) {
            parentNode.setLeftChild();
        } else {
            parentNode.setRightChild();
        }
        String parent = parentNode.getRootName();
        parentNodeList.add(node);
        StoredDeviceGroup newGroup = createNestedDeviceGroup(parent, childGroupName);
        newGroups.add(newGroup);
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
