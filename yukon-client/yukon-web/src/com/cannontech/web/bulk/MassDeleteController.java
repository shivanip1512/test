package com.cannontech.web.bulk;

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
import com.cannontech.common.bulk.callbackResult.BackgroundProcessResultHolder;
import com.cannontech.common.bulk.callbackResult.MassDeleteCallbackResult;
import com.cannontech.common.bulk.collection.DeviceCollection;
import com.cannontech.common.bulk.collection.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.mapper.PassThroughMapper;
import com.cannontech.common.bulk.processor.ProcessingException;
import com.cannontech.common.bulk.processor.SingleProcessor;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.PaoLoadingService;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@CheckRoleProperty(YukonRoleProperty.MASS_DELETE)
public class MassDeleteController extends BulkControllerBase {

    private DeviceDao deviceDao;
    private RecentResultsCache<BackgroundProcessResultHolder> recentResultsCache;
    private BulkProcessor bulkProcessor;
    private TemporaryDeviceGroupService temporaryDeviceGroupService;
    private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
    private PaoLoadingService paoLoadingService;
    
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
        
        // DO DELETE
        } else {
            
            // CALLBACK
        	String resultsId = StringUtils.replace(UUID.randomUUID().toString(), "-", "");
            StoredDeviceGroup processingExceptionGroup = temporaryDeviceGroupService.createTempGroup(null);
            
            MassDeleteCallbackResult callbackResult = new MassDeleteCallbackResult(resultsId,
																			deviceCollection, 
																			processingExceptionGroup, 
																			deviceGroupMemberEditorDao,
																			deviceGroupCollectionHelper);
            
            // CACHE
            recentResultsCache.addResult(resultsId, callbackResult);
            
            // PROCESS
            SingleProcessor<SimpleDevice> bulkUpdater = new SingleProcessor<SimpleDevice>() {

                @Override
                public void process(SimpleDevice device) throws ProcessingException {
                    processDeviceDelete(device);
                }
            };
            
            ObjectMapper<SimpleDevice, SimpleDevice> mapper = new PassThroughMapper<SimpleDevice>();
            bulkProcessor.backgroundBulkProcess(deviceCollection.iterator(), mapper, bulkUpdater, callbackResult);
            
            mav = new ModelAndView("redirect:massDeleteResults");
            mav.addObject("resultsId", resultsId);
        }
        
        return mav;
    }
    
    private void processDeviceDelete(SimpleDevice device) {

        try {
            deviceDao.removeDevice(device);
        } catch (DataRetrievalFailureException e) {
            throw new ProcessingException("Could not find device: " + paoLoadingService.getDisplayablePao(device).getName() + " (id=" + device.getDeviceId() + ")", e);
        } catch (Exception e) {
            throw new ProcessingException("Could not delete device: " + paoLoadingService.getDisplayablePao(device).getName() + " (id=" + device.getDeviceId() + ")", e);
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
        MassDeleteCallbackResult callbackResult = (MassDeleteCallbackResult)recentResultsCache.getResult(resultsId);
        mav.addObject("callbackResult", callbackResult);

        return mav;
    }

    
    @Required
    public void setDeviceDao(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
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
    public void setPaoLoadingService(PaoLoadingService paoLoadingService) {
		this.paoLoadingService = paoLoadingService;
	}
}
