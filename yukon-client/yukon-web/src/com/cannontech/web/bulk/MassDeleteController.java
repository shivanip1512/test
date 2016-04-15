package com.cannontech.web.bulk;

import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.bulk.BulkProcessor;
import com.cannontech.common.bulk.callbackResult.BackgroundProcessResultHolder;
import com.cannontech.common.bulk.callbackResult.MassDeleteCallbackResult;
import com.cannontech.common.bulk.collection.device.DeviceCollectionFactory;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.mapper.PassThroughMapper;
import com.cannontech.common.bulk.processor.ProcessingException;
import com.cannontech.common.bulk.processor.SingleProcessor;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.dao.PaoPersistenceDao;
import com.cannontech.common.pao.service.PaoPersistenceService;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.PaoLoadingService;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@CheckRoleProperty(YukonRoleProperty.MASS_DELETE)
@Controller
@RequestMapping("massDelete/*")
public class MassDeleteController {

    @Autowired private DeviceCollectionFactory deviceCollectionFactory;
    @Autowired private DeviceDao deviceDao;
    @Autowired private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
    @Autowired private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    @Autowired private PaoLoadingService paoLoadingService;
    @Autowired private PaoPersistenceDao paoPersistenceDao;
    @Autowired private PaoPersistenceService paoPersistenceService;
    @Autowired private TemporaryDeviceGroupService temporaryDeviceGroupService;    
    
    @Resource(name="recentResultsCache") private RecentResultsCache<BackgroundProcessResultHolder> recentResultsCache;
    @Resource(name="resubmittingBulkProcessor") private BulkProcessor bulkProcessor;
    
    /**
     * CONFIRM MASS DELETE
     */
    @RequestMapping("massDelete")
    public String massDelete(ModelMap model, HttpServletRequest request) throws ServletException {

        // pass along deviceCollection
        DeviceCollection deviceCollection = this.deviceCollectionFactory.createDeviceCollection(request);
        model.addAttribute("deviceCollection", deviceCollection);
        
        long deviceCount = deviceCollection.getDeviceCount();
        model.addAttribute("deviceCount", deviceCount);
        
        return "massDelete/massDeleteConfirm.jsp";
    }
    
    /**
     * DO MASS DELETE
     */
    @RequestMapping("doMassDelete")
    public String doMassDelete(ModelMap model, HttpServletRequest request) throws ServletException {

        DeviceCollection deviceCollection = this.deviceCollectionFactory.createDeviceCollection(request);
        
        // CALLBACK
    	String resultsId = StringUtils.replace(UUID.randomUUID().toString(), "-", "");
        StoredDeviceGroup processingExceptionGroup = temporaryDeviceGroupService.createTempGroup();
        
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
        
        model.addAttribute("resultsId", resultsId);
        
        return "redirect:massDeleteResults";
    }
    
    private void processDeviceDelete(SimpleDevice device) {
        try {
            if (paoPersistenceDao.supports(device)) {
                paoPersistenceService.deletePao(device);
            } else {
                deviceDao.removeDevice(device);
            }
        } catch (DataRetrievalFailureException e) {
            String displayablePao = paoLoadingService.getDisplayablePao(device).getName();
            throw new ProcessingException("Could not find device: " +displayablePao+ " (id=" + device.getDeviceId() + ")",
                                          "paoNotFound",
                                          e,
                                          displayablePao,
                                          device.getDeviceId());
        } catch (Exception e) {
            String displayablePao = paoLoadingService.getDisplayablePao(device).getName();
            throw new ProcessingException("Could not delete device: " + displayablePao + " (id=" + device.getDeviceId() + ")",
                                          "deleteDevice",
                                          e,
                                          displayablePao,
                                          device.getDeviceId());
        }
    }
    
    /**
     * MASS DELETE RESULTS
     */
    @RequestMapping("massDeleteResults")
    public String massDeleteResults(ModelMap model, HttpServletRequest request) throws ServletException {

        // result info
        String resultsId = ServletRequestUtils.getRequiredStringParameter(request, "resultsId");
        MassDeleteCallbackResult callbackResult = (MassDeleteCallbackResult)recentResultsCache.getResult(resultsId);
        model.addAttribute("callbackResult", callbackResult);

        return "massDelete/massDeleteResults.jsp";
    }

}