package com.cannontech.web.bulk;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.common.bulk.BulkProcessor;
import com.cannontech.common.bulk.collection.DeviceCollection;
import com.cannontech.common.bulk.collection.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.field.BulkFieldColumnHeader;
import com.cannontech.common.bulk.mapper.PassThroughMapper;
import com.cannontech.common.bulk.processor.ProcessingException;
import com.cannontech.common.bulk.processor.SingleProcessor;
import com.cannontech.common.bulk.service.BulkOperationCallbackResults;
import com.cannontech.common.bulk.service.BulkOperationTypeEnum;
import com.cannontech.common.bulk.service.DeviceCollectionContainingFileInfo;
import com.cannontech.common.bulk.service.MassChangeCallbackResults;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.definition.model.DeviceDefinition;
import com.cannontech.common.device.definition.service.DeviceDefinitionService;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.roles.operator.DeviceActionsRole;
import com.cannontech.web.bulk.service.ChangeDeviceTypeService;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@CheckRole(DeviceActionsRole.ROLEID)
@CheckRoleProperty(DeviceActionsRole.MASS_CHANGE)
public class ChangeDeviceTypeController extends BulkControllerBase {

    private RecentResultsCache<BulkOperationCallbackResults<?>> recentBulkOperationResultsCache = null;
    private BulkProcessor bulkProcessor = null;
    private DeviceDefinitionService deviceDefinitionService = null;
    private TemporaryDeviceGroupService temporaryDeviceGroupService;
    private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao = null;
    private DeviceGroupCollectionHelper deviceGroupCollectionHelper = null;
    private ChangeDeviceTypeService changeDeviceTypeService;
    
    /**
     * CHOOSE DEVICE TYPE TO CHANGE TO
     * @param request
     * @param response
     * @return
     * @throws ServletException
     */
    public ModelAndView chooseDeviceType(HttpServletRequest request, HttpServletResponse response) throws ServletException {

        ModelAndView mav = new ModelAndView("changeDeviceType/chooseDeviceType.jsp");
        
        DeviceCollection deviceCollection = this.deviceCollectionFactory.createDeviceCollection(request);
        mav.addObject("deviceCollection", deviceCollection);
        
        Map<String, Integer> deviceTypes = new LinkedHashMap<String, Integer>();
        Map<String, List<DeviceDefinition>> deviceGroupMap = deviceDefinitionService.getDeviceDisplayGroupMap();
        for (String key : deviceGroupMap.keySet()) {
            for (DeviceDefinition def :  deviceGroupMap.get(key)) {
                deviceTypes.put(def.getDisplayName(), def.getType());
            }
        }
        mav.addObject("deviceTypes", deviceTypes);

        return mav;
    }
    
    /**
     * DO DEVICE TYPE CHANGE
     * @param request
     * @param response
     * @return
     * @throws ServletException
     */
    public ModelAndView changeDeviceType(HttpServletRequest request, HttpServletResponse response) throws ServletException {

        ModelAndView mav = null;
        String cancelButton = ServletRequestUtils.getStringParameter(request, "cancelButton", null);
        DeviceCollection deviceCollection = this.deviceCollectionFactory.createDeviceCollection(request);
        
        // CANCEL
        if (cancelButton != null) {
            
            // redirect
            mav = new ModelAndView("redirect:/spring/bulk/collectionActions");
            mav.addAllObjects(deviceCollection.getCollectionParameters());
            return mav;
        
        // DO CHANGE
        } else {
            
            final int selectedDeviceType = ServletRequestUtils.getRequiredIntParameter(request, "deviceTypes"); 
            
            // create a temp group
            final StoredDeviceGroup successGroup = temporaryDeviceGroupService.createTempGroup(null);
            final StoredDeviceGroup processingExceptionGroup = temporaryDeviceGroupService.createTempGroup(null);
            
            // init callcback, use a TranslatingBulkProcessorCallback to get from UpdateableDevice to YukonDevice
            MassChangeCallbackResults bulkOperationCallbackResults = new MassChangeCallbackResults(successGroup, processingExceptionGroup, deviceGroupMemberEditorDao, deviceGroupCollectionHelper, Collections.singletonList(BulkFieldColumnHeader.DEVICE_TYPE), BulkOperationTypeEnum.MASS_CHANGE);
            
            // STORE RESULTS INFO TO CACHE
            String id = StringUtils.replace(UUID.randomUUID().toString(), "-", "");
            bulkOperationCallbackResults.setResultsId(id);
            DeviceCollectionContainingFileInfo deviceCollectionContainingFileInfo = new DeviceCollectionContainingFileInfo(deviceCollection);
            bulkOperationCallbackResults.setBulkFileInfo(deviceCollectionContainingFileInfo);
            recentBulkOperationResultsCache.addResult(id, bulkOperationCallbackResults);
            
            // PROCESS
            SingleProcessor<YukonDevice> bulkUpdater = new SingleProcessor<YukonDevice>() {

                @Override
                public void process(YukonDevice device) throws ProcessingException {
                    changeDeviceTypeService.processDeviceTypeChange(device, selectedDeviceType);
                }
            };
            
            ObjectMapper<YukonDevice, YukonDevice> mapper = new PassThroughMapper<YukonDevice>();
            bulkProcessor.backgroundBulkProcess(deviceCollection.iterator(), mapper, bulkUpdater, bulkOperationCallbackResults);
            
            mav = new ModelAndView("redirect:changeDeviceTypeResults");
            mav.addObject("resultsId", id);
        }
        
        return mav;
    }
    
    /**
     * CHANGE DEVICE TYPE RESULTS
     * @param request
     * @param response
     * @return
     * @throws ServletException
     */
    public ModelAndView changeDeviceTypeResults(HttpServletRequest request, HttpServletResponse response) throws ServletException {

        ModelAndView mav = new ModelAndView("changeDeviceType/changeDeviceTypeResults.jsp");

        // result info
        String resultsId = ServletRequestUtils.getRequiredStringParameter(request, "resultsId");
        BulkOperationCallbackResults<?> bulkOperationCallbackResults = recentBulkOperationResultsCache.getResult(resultsId);
        
        // file info
        DeviceCollectionContainingFileInfo deviceCollectionContainingFileInfo = (DeviceCollectionContainingFileInfo)bulkOperationCallbackResults.getBulkFileInfo();
        
        mav.addObject("deviceCollection", deviceCollectionContainingFileInfo.getDeviceCollection());
        mav.addObject("bulkUpdateOperationResults", bulkOperationCallbackResults);

        return mav;
    }
    
    @Required
    public void setRecentBulkOperationResultsCache(
            RecentResultsCache<BulkOperationCallbackResults<?>> recentBulkOperationResultsCache) {
        this.recentBulkOperationResultsCache = recentBulkOperationResultsCache;
    }
    
    @Required
    public void setBulkProcessor(BulkProcessor bulkProcessor) {
        this.bulkProcessor = bulkProcessor;
    }
    
    @Autowired
    public void setDeviceDefinitionService(DeviceDefinitionService deviceDefinitionService) {
        this.deviceDefinitionService = deviceDefinitionService;
    }
    
    @Autowired
    public void setTemporaryDeviceGroupService(
            TemporaryDeviceGroupService temporaryDeviceGroupService) {
        this.temporaryDeviceGroupService = temporaryDeviceGroupService;
    }
    
    @Autowired
    public void setDeviceGroupMemberEditorDao(
            DeviceGroupMemberEditorDao deviceGroupMemberEditorDao) {
        this.deviceGroupMemberEditorDao = deviceGroupMemberEditorDao;
    }
    
    @Autowired
    public void setDeviceGroupCollectionHelper(
            DeviceGroupCollectionHelper deviceGroupCollectionHelper) {
        this.deviceGroupCollectionHelper = deviceGroupCollectionHelper;
    }
    
    @Autowired
    public void setChangeDeviceTypeService(
            ChangeDeviceTypeService changeDeviceTypeService) {
        this.changeDeviceTypeService = changeDeviceTypeService;
    }
}
