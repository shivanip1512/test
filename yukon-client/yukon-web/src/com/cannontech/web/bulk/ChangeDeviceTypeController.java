package com.cannontech.web.bulk;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataRetrievalFailureException;
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
import com.cannontech.common.device.definition.dao.DeviceDefinitionDao;
import com.cannontech.common.device.definition.model.DeviceDefinition;
import com.cannontech.common.device.definition.service.DeviceDefinitionService;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PersistenceException;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.PaoGroupsWrapper;
import com.cannontech.roles.operator.DeviceActionsRole;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@CheckRole(DeviceActionsRole.ROLEID)
@CheckRoleProperty(DeviceActionsRole.MASS_CHANGE)
public class ChangeDeviceTypeController extends BulkControllerBase {

    private PaoDao paoDao = null;
    private RecentResultsCache<BulkOperationCallbackResults<?>> recentBulkOperationResultsCache = null;
    private BulkProcessor bulkProcessor = null;
    private PaoGroupsWrapper paoGroupsWrapper = null;
    private DeviceDefinitionService deviceDefinitionService = null;
    private DeviceDefinitionDao deviceDefinitionDao = null;
    private TemporaryDeviceGroupService temporaryDeviceGroupService;
    private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao = null;
    private DeviceGroupCollectionHelper deviceGroupCollectionHelper = null;
    
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
            
            mav = new ModelAndView("collectionActions.jsp");
            mav.addObject("deviceCollection", deviceCollection);
        
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
                    processDeviceTypeChange(device, selectedDeviceType);
                }
            };
            
            ObjectMapper<YukonDevice, YukonDevice> mapper = new PassThroughMapper<YukonDevice>();
            bulkProcessor.backgroundBulkProcess(deviceCollection.iterator(), mapper, bulkUpdater, bulkOperationCallbackResults);
            
            mav = new ModelAndView("redirect:changeDeviceTypeResults");
            mav.addObject("resultsId", id);
        }
        
        return mav;
    }
    
    private YukonDevice processDeviceTypeChange(YukonDevice device, int newDeviceType ) {

        try {

            // get the definition for the type selected
            if (newDeviceType == device.getType()) {
                return device;
            }

            DeviceDefinition selectedDeviceDefinition = deviceDefinitionDao.getDeviceDefinition(newDeviceType);

            // get set of all definition applicable for this device to be changed to
            Set<DeviceDefinition> applicableDefinitions = deviceDefinitionService.getChangeableDevices(device);

            // if its not in the set, throw a processing error
            if (!applicableDefinitions.contains(selectedDeviceDefinition)) {

                LiteYukonPAObject devicePao = paoDao.getLiteYukonPAO(device.getDeviceId());
                String errorMsg = selectedDeviceDefinition.getDisplayName() + " is not an applicable type for device: " + devicePao.getPaoName();
                throw new ProcessingException(errorMsg);
            }
            else {
                return deviceDefinitionService.changeDeviceType(device, selectedDeviceDefinition);
            }

        }
        catch (IllegalArgumentException e) {
            throw new ProcessingException("Invalid device type: " + paoGroupsWrapper.getPAOTypeString(newDeviceType));
        } catch (DataRetrievalFailureException e) {
            throw new ProcessingException("Could not find device with id: " + device.getDeviceId(),
                                          e);
        } catch (PersistenceException e) {
            throw new ProcessingException("Could not change device type for device with id: " + device.getDeviceId(), e);
        }
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
    
    @Autowired
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
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
    public void setPaoGroupsWrapper(PaoGroupsWrapper paoGroupsWrapper) {
        this.paoGroupsWrapper = paoGroupsWrapper;
    }
    
    @Autowired
    public void setDeviceDefinitionService(DeviceDefinitionService deviceDefinitionService) {
        this.deviceDefinitionService = deviceDefinitionService;
    }
    
    @Autowired
    public void setDeviceDefinitionDao(DeviceDefinitionDao deviceDefinitionDao) {
        this.deviceDefinitionDao = deviceDefinitionDao;
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
}
