package com.cannontech.web.bulk;

import java.util.ArrayList;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.UncategorizedSQLException;
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
import com.cannontech.common.bulk.service.MassDeleteCallbackResults;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.roles.operator.DeviceActionsRole;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@CheckRole(DeviceActionsRole.ROLEID)
@CheckRoleProperty(DeviceActionsRole.MASS_DELETE)
public class MassDeleteController extends BulkControllerBase {

    private DeviceDao deviceDao = null;
    private RecentResultsCache<BulkOperationCallbackResults<?>> recentBulkOperationResultsCache = null;
    
    private BulkProcessor bulkProcessor = null;
    private TemporaryDeviceGroupService temporaryDeviceGroupService;
    private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao = null;
    private DeviceGroupCollectionHelper deviceGroupCollectionHelper = null;
    
    /**
     * CONFIRM MASS DELETE
     * @param request
     * @param response
     * @return
     * @throws ServletException
     */
    public ModelAndView massDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException {

        ModelAndView mav = new ModelAndView("massDelete/massDeleteConfirm.jsp");
        
        // pass along deviceCollection
        DeviceCollection deviceCollection = this.deviceCollectionFactory.createDeviceCollection(request);
        mav.addObject("deviceCollection", deviceCollection);
        
        long deviceCount = deviceCollection.getDeviceCount();
        mav.addObject("deviceCount", deviceCount);
        
        
        return mav;
    }
    
    
    /**
     * DO MASS DELETE
     * @param request
     * @param response
     * @return
     * @throws ServletException
     */
    public ModelAndView doMassDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException {

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
            
            // create a temp group
            final StoredDeviceGroup processingExceptionGroup = temporaryDeviceGroupService.createTempGroup(null);
            
            // init callcback, use a TranslatingBulkProcessorCallback to get from UpdateableDevice to YukonDevice
            MassDeleteCallbackResults bulkOperationCallbackResults = new MassDeleteCallbackResults(processingExceptionGroup, deviceGroupMemberEditorDao, deviceGroupCollectionHelper, new ArrayList<BulkFieldColumnHeader>(0), BulkOperationTypeEnum.MASS_DELETE, deviceCollection.getDeviceCount());
            
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
                    processDeviceDelete(device);
                }
            };
            
            ObjectMapper<YukonDevice, YukonDevice> mapper = new PassThroughMapper<YukonDevice>();
            bulkProcessor.backgroundBulkProcess(deviceCollection.iterator(), mapper, bulkUpdater, bulkOperationCallbackResults);
            
            mav = new ModelAndView("redirect:massDeleteResults");
            mav.addObject("resultsId", id);
        }
        
        return mav;
    }
    
    private void processDeviceDelete(YukonDevice device) {

        try {
            deviceDao.removeDevice(device);
        } catch (DataRetrievalFailureException e) {
            throw new ProcessingException("Could not find device with id: " + device.getDeviceId(), e);
        } catch (UncategorizedSQLException e) {
            throw new ProcessingException("Could not delete device with id: " + device.getDeviceId(), e);
        }
    }
    
    /**
     * MASS DELETE RESULTS
     * @param request
     * @param response
     * @return
     * @throws ServletException
     */
    public ModelAndView massDeleteResults(HttpServletRequest request, HttpServletResponse response) throws ServletException {

        ModelAndView mav = new ModelAndView("massDelete/massDeleteResults.jsp");

        // result info
        String resultsId = ServletRequestUtils.getRequiredStringParameter(request, "resultsId");
        MassDeleteCallbackResults bulkOperationCallbackResults = (MassDeleteCallbackResults)recentBulkOperationResultsCache.getResult(resultsId);
        mav.addObject("bulkUpdateOperationResults", bulkOperationCallbackResults);

        return mav;
    }

    
    @Required
    public void setDeviceDao(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
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
