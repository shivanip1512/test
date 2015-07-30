package com.cannontech.web.bulk;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cannontech.common.bulk.BulkProcessor;
import com.cannontech.common.bulk.callbackResult.BackgroundProcessResultHolder;
import com.cannontech.common.bulk.callbackResult.ChangeDeviceTypeCallbackResult;
import com.cannontech.common.bulk.collection.device.DeviceCollectionFactory;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.mapper.PassThroughMapper;
import com.cannontech.common.bulk.processor.ProcessingException;
import com.cannontech.common.bulk.processor.SingleProcessor;
import com.cannontech.common.bulk.service.ChangeDeviceTypeService;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.model.PaoDefinition;
import com.cannontech.common.pao.definition.service.PaoDefinitionService;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.util.ServletRequestEnumUtils;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@CheckRoleProperty(YukonRoleProperty.MASS_CHANGE)
@Controller
@RequestMapping("changeDeviceType/*")
public class ChangeDeviceTypeController {

    @Resource(name="recentResultsCache") private RecentResultsCache<BackgroundProcessResultHolder> recentResultsCache;
    @Resource(name="resubmittingBulkProcessor") private BulkProcessor bulkProcessor;
    
    @Autowired private PaoDefinitionService paoDefinitionService;
    @Autowired private TemporaryDeviceGroupService temporaryDeviceGroupService;
    @Autowired private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    @Autowired private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
    @Autowired private ChangeDeviceTypeService changeDeviceTypeService;
    @Autowired private DeviceCollectionFactory deviceCollectionFactory;
    
    /**
     * CHOOSE DEVICE TYPE TO CHANGE TO
     */
    @RequestMapping("chooseDeviceType")
    public String chooseDeviceType(ModelMap model, HttpServletRequest request,
            @RequestParam(value = "errorDevices", required = false) Set<String> errors) throws ServletException {

        DeviceCollection deviceCollection = this.deviceCollectionFactory.createDeviceCollection(request);
        model.addAttribute("deviceCollection", deviceCollection);

        Set<PaoType> paoTypes = Sets.newHashSet();
        for (SimpleDevice device : deviceCollection.getDeviceList()) {
            paoTypes.add(device.getDeviceType());
        }

        // Only add device types that are valid for the collection 
        Map<String, PaoType> deviceTypes = Maps.newTreeMap();
        for (PaoType paoType : paoTypes) {
            Set<PaoDefinition> changeablePaos = paoDefinitionService.getChangeablePaos(paoType);
            for (PaoDefinition paoDefinition : changeablePaos) {
                /*
                 * To change between MCT < > RFN requires additional data per meter being collected. We are not going to
                 * support that with the Change Type collection action, therefore, we are explicitly not including the
                 * opposite types for this feature.
                 */
                if (paoType.isMct()) {
                    if (paoDefinition.getType().isMct()) {
                        deviceTypes.put(paoDefinition.getDisplayName(), paoDefinition.getType());
                    }
                } else if (paoType.isRfMeter()) {
                    if (paoDefinition.getType().isRfMeter()) {
                        deviceTypes.put(paoDefinition.getDisplayName(), paoDefinition.getType());
                    }
                } else {
                    deviceTypes.put(paoDefinition.getDisplayName(), paoDefinition.getType());
                }
            }
        }
        model.addAttribute("deviceTypes", deviceTypes);
        model.addAttribute("deviceErrors", errors);
        if (null != errors)
            model.addAttribute("deviceErrorCount", errors.size());
        return "changeDeviceType/chooseDeviceType.jsp";
    }
    
    /**
     * DO DEVICE TYPE CHANGE
     */
    @RequestMapping("changeDeviceType")
    public String changeDeviceType(ModelMap model, HttpServletRequest request) throws ServletException {

        DeviceCollection deviceCollection = this.deviceCollectionFactory.createDeviceCollection(request);
        
        // CALLBACK
    	String resultsId = StringUtils.replace(UUID.randomUUID().toString(), "-", "");
        StoredDeviceGroup successGroup = temporaryDeviceGroupService.createTempGroup();
        StoredDeviceGroup processingExceptionGroup = temporaryDeviceGroupService.createTempGroup();
        
        ChangeDeviceTypeCallbackResult callbackResult = new ChangeDeviceTypeCallbackResult(resultsId,
																					deviceCollection, 
																					successGroup, 
																					processingExceptionGroup, 
																					deviceGroupMemberEditorDao,
																					deviceGroupCollectionHelper);
        
        // CACHE
        recentResultsCache.addResult(resultsId, callbackResult);
        
        // PROCESS
        final PaoType selectedDeviceType = ServletRequestEnumUtils.getRequiredEnumParameter(request, PaoType.class, "deviceTypes"); 
        SingleProcessor<SimpleDevice> bulkUpdater = new SingleProcessor<SimpleDevice>() {
            @Override
            public void process(SimpleDevice device) throws ProcessingException {
                changeDeviceTypeService.changeDeviceType(device, selectedDeviceType, null);
            }
        };
        
        ObjectMapper<SimpleDevice, SimpleDevice> mapper = new PassThroughMapper<SimpleDevice>();
        bulkProcessor.backgroundBulkProcess(deviceCollection.iterator(), mapper, bulkUpdater, callbackResult);
        
        model.addAttribute("resultsId", resultsId);
        
        return "redirect:changeDeviceTypeResults";
    }
    
    /**
     * CHANGE DEVICE TYPE RESULTS
     */
    @RequestMapping("changeDeviceTypeResults")
    public String changeDeviceTypeResults(ModelMap model, HttpServletRequest request) throws ServletException {

        // result info
        String resultsId = ServletRequestUtils.getRequiredStringParameter(request, "resultsId");
        ChangeDeviceTypeCallbackResult callbackResult = (ChangeDeviceTypeCallbackResult)recentResultsCache.getResult(resultsId);
        
        model.addAttribute("deviceCollection", callbackResult.getDeviceCollection());
        model.addAttribute("callbackResult", callbackResult);

        return "changeDeviceType/changeDeviceTypeResults.jsp";
    }
    
}