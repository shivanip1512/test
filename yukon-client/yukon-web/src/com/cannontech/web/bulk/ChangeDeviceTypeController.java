package com.cannontech.web.bulk;

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
import com.cannontech.common.bulk.callbackResult.BackgroundProcessResultHolder;
import com.cannontech.common.bulk.callbackResult.ChangeDeviceTypeCallbackResult;
import com.cannontech.common.bulk.collection.DeviceCollection;
import com.cannontech.common.bulk.collection.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.mapper.PassThroughMapper;
import com.cannontech.common.bulk.processor.ProcessingException;
import com.cannontech.common.bulk.processor.SingleProcessor;
import com.cannontech.common.bulk.service.ChangeDeviceTypeService;
import com.cannontech.common.device.definition.model.DeviceDefinition;
import com.cannontech.common.device.definition.service.DeviceDefinitionService;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@CheckRoleProperty(YukonRoleProperty.MASS_CHANGE)
public class ChangeDeviceTypeController extends BulkControllerBase {

    private RecentResultsCache<BackgroundProcessResultHolder> recentResultsCache;
    private BulkProcessor bulkProcessor;
    private DeviceDefinitionService deviceDefinitionService;
    private TemporaryDeviceGroupService temporaryDeviceGroupService;
    private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
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
                deviceTypes.put(def.getDisplayName(), def.getType().getDeviceTypeId());
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
            
            // CALLBACK
        	String resultsId = StringUtils.replace(UUID.randomUUID().toString(), "-", "");
            StoredDeviceGroup successGroup = temporaryDeviceGroupService.createTempGroup(null);
            StoredDeviceGroup processingExceptionGroup = temporaryDeviceGroupService.createTempGroup(null);
            
            ChangeDeviceTypeCallbackResult callbackResult = new ChangeDeviceTypeCallbackResult(resultsId,
																						deviceCollection, 
																						successGroup, 
																						processingExceptionGroup, 
																						deviceGroupMemberEditorDao,
																						deviceGroupCollectionHelper);
            
            // CACHE
            recentResultsCache.addResult(resultsId, callbackResult);
            
            // PROCESS
            final int selectedDeviceType = ServletRequestUtils.getRequiredIntParameter(request, "deviceTypes"); 
            SingleProcessor<SimpleDevice> bulkUpdater = new SingleProcessor<SimpleDevice>() {

                @Override
                public void process(SimpleDevice device) throws ProcessingException {
                    changeDeviceTypeService.changeDeviceType(device, PaoType.getForId(selectedDeviceType));
                }
            };
            
            ObjectMapper<SimpleDevice, SimpleDevice> mapper = new PassThroughMapper<SimpleDevice>();
            bulkProcessor.backgroundBulkProcess(deviceCollection.iterator(), mapper, bulkUpdater, callbackResult);
            
            mav = new ModelAndView("redirect:changeDeviceTypeResults");
            mav.addObject("resultsId", resultsId);
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
        ChangeDeviceTypeCallbackResult callbackResult = (ChangeDeviceTypeCallbackResult)recentResultsCache.getResult(resultsId);
        
        mav.addObject("deviceCollection", callbackResult.getDeviceCollection());
        mav.addObject("callbackResult", callbackResult);

        return mav;
    }
    
    @Required
    public void setRecentResultsCache(RecentResultsCache<BackgroundProcessResultHolder> recentResultsCache) {
        this.recentResultsCache = recentResultsCache;
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
    public void setTemporaryDeviceGroupService(TemporaryDeviceGroupService temporaryDeviceGroupService) {
        this.temporaryDeviceGroupService = temporaryDeviceGroupService;
    }
    
    @Autowired
    public void setDeviceGroupMemberEditorDao(DeviceGroupMemberEditorDao deviceGroupMemberEditorDao) {
        this.deviceGroupMemberEditorDao = deviceGroupMemberEditorDao;
    }
    
    @Autowired
    public void setDeviceGroupCollectionHelper(DeviceGroupCollectionHelper deviceGroupCollectionHelper) {
		this.deviceGroupCollectionHelper = deviceGroupCollectionHelper;
	}
    
    @Autowired
    public void setChangeDeviceTypeService(ChangeDeviceTypeService changeDeviceTypeService) {
        this.changeDeviceTypeService = changeDeviceTypeService;
    }
}
