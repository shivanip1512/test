package com.cannontech.web.stars.gateway.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cannontech.common.YukonColorPalette;
import com.cannontech.common.bulk.collection.DeviceMemoryCollectionProducer;
import com.cannontech.common.bulk.collection.device.model.CollectionActionUrl;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.core.dao.StateGroupDao;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.db.point.stategroup.CommStatusState;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.tools.mapping.MappingColorCollection;

public class ConnectedDevicesHelper {
    
    @Autowired private StateGroupDao stateGroupDao;
    @Autowired private DeviceMemoryCollectionProducer producer;
    @Autowired private TemporaryDeviceGroupService tempDeviceGroupService;
    @Autowired private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    @Autowired private RfnGatewayService rfnGatewayService;

    public String getConnectedDevicesMappingData(Integer[] connectedIds, Integer[] disconnectedIds, Integer[] filteredCommStatus, 
                                                 YukonUserContext userContext, RedirectAttributes attrs) {
        List<MappingColorCollection> colorCollections = new ArrayList<MappingColorCollection>();
        Map<String, List<Integer>> mappingMap = new HashMap<String, List<Integer>>();
        List<Integer> selectedCommStatuses = Arrays.asList(filteredCommStatus);
        boolean includeConnected = selectedCommStatuses.isEmpty() || selectedCommStatuses.contains(CommStatusState.CONNECTED.getRawState());
        boolean includeDisconnected = selectedCommStatuses.isEmpty() || selectedCommStatuses.contains(CommStatusState.DISCONNECTED.getRawState());
        List<Integer> allDevices = new ArrayList<>();
        LiteStateGroup phaseStateGroup = stateGroupDao.getStateGroup("Comm Status State");

        if (includeConnected) {
            List<Integer> connectedList = Arrays.asList(connectedIds);
            String connectedColor = YukonColorPalette.GREEN.getHexValue();
            DeviceCollection connectedCollection = producer.createDeviceCollection(connectedList);
            MappingColorCollection mapCollection = new MappingColorCollection(connectedCollection, connectedColor, null);
            Optional <LiteState> connectedState = phaseStateGroup.getStatesList().stream()
                    .filter(state -> state.getStateRawState() == CommStatusState.CONNECTED.getRawState()).findFirst();
            if (connectedState.isPresent()) {
                mapCollection.setLabelText(connectedState.get().getStateText());
            }
            colorCollections.add(mapCollection);
            mappingMap.put(connectedColor, connectedList);
            allDevices.addAll(connectedList);
        }
        
        if (includeDisconnected) {
            List<Integer> disconnectedList = Arrays.asList(disconnectedIds);
            String disconnectedColor = YukonColorPalette.RED.getHexValue();
            DeviceCollection disconnectedCollection = producer.createDeviceCollection(disconnectedList);
            MappingColorCollection disconnectedMapCollection = new MappingColorCollection(disconnectedCollection, disconnectedColor, null);
            Optional <LiteState> disconnectedState = phaseStateGroup.getStatesList().stream()
                    .filter(state -> state.getStateRawState() == CommStatusState.DISCONNECTED.getRawState()).findFirst();
            if (disconnectedState.isPresent()) {
                disconnectedMapCollection.setLabelText(disconnectedState.get().getStateText());
            }
            colorCollections.add(disconnectedMapCollection);
            mappingMap.put(disconnectedColor, disconnectedList);
            allDevices.addAll(disconnectedList);
        }
        
        DeviceCollection allCollection = producer.createDeviceCollection(allDevices);
        List<SimpleDevice> deviceList = allCollection.getDeviceList();
        StoredDeviceGroup tempGroup = tempDeviceGroupService.createTempGroup();
        deviceGroupMemberEditorDao.addDevices(tempGroup, deviceList);

        attrs.addFlashAttribute("mappingColors", mappingMap);
        attrs.addFlashAttribute("colorCollections", colorCollections);
        
        return "redirect:" + CollectionActionUrl.MAPPING.getUrl() + "?collectionType=group&group.name=" + tempGroup.getFullName();
    }
    
    public void setupConnectedDevicesModel(Integer gatewayId, ModelMap model) {
        //This is needed for breadcrumbs
        RfnGateway gateway = rfnGatewayService.getGatewayByPaoId(gatewayId);
        model.addAttribute("gateway", gateway);
        
        LiteStateGroup phaseStateGroup = stateGroupDao.getStateGroup("Comm Status State");
        //remove Any state
        List<LiteState> states = phaseStateGroup.getStatesList().stream()
                .filter(state -> state.getLiteID() > -1).collect(Collectors.toList());
        model.addAttribute("commStatusValues", states);
        model.addAttribute("connectedStatusValue", CommStatusState.CONNECTED.getRawState());
        model.addAttribute("cellTypes", PaoType.getCellularTypes());
    }
}
