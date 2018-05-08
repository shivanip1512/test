package com.cannontech.web.bulk;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.bulk.BulkProcessor;
import com.cannontech.common.bulk.callbackResult.BackgroundProcessResultHolder;
import com.cannontech.common.bulk.collection.device.DeviceCollectionFactory;
import com.cannontech.common.bulk.collection.device.dao.CollectionActionDao;
import com.cannontech.common.bulk.collection.device.model.CollectionAction;
import com.cannontech.common.bulk.collection.device.model.CollectionActionBulkProcessorCallback;
import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.collection.device.service.CollectionActionService;
import com.cannontech.common.bulk.mapper.PassThroughMapper;
import com.cannontech.common.bulk.processor.ProcessingException;
import com.cannontech.common.bulk.processor.SingleProcessor;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.dao.PaoPersistenceDao;
import com.cannontech.common.pao.service.PaoPersistenceService;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.PaoLoadingService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@CheckRoleProperty(YukonRoleProperty.MASS_DELETE)
@Controller
@RequestMapping("massDelete/*")
public class MassDeleteController {

    @Autowired private DeviceCollectionFactory deviceCollectionFactory;
    @Autowired private DeviceDao deviceDao;
    @Autowired private PaoLoadingService paoLoadingService;
    @Autowired private PaoPersistenceDao paoPersistenceDao;
    @Autowired private PaoPersistenceService paoPersistenceService;
    @Autowired private CollectionActionService collectionActionService;
    @Autowired private CollectionActionDao collectionActionDao;
    
    @Resource(name="recentResultsCache") private RecentResultsCache<BackgroundProcessResultHolder> recentResultsCache;
    @Resource(name="resubmittingBulkProcessor") private BulkProcessor bulkProcessor;
    
    /**
     * CONFIRM MASS DELETE
     */
    @RequestMapping(value = "massDelete", method = RequestMethod.GET)
    public String massDelete(ModelMap model, HttpServletRequest request) throws ServletException {
        setupModel(model, request);
        model.addAttribute("action", CollectionAction.MASS_DELETE);
        model.addAttribute("actionInputs", "/WEB-INF/pages/bulk/massDelete/massDeleteConfirm.jsp");
        return "../collectionActions/collectionActionsHome.jsp";    
    }
    
    @RequestMapping(value = "massDeleteInputs", method = RequestMethod.GET)
    public String massDeleteInputs(ModelMap model, HttpServletRequest request) throws ServletException {
        setupModel(model, request);
        return "massDelete/massDeleteConfirm.jsp";
    }
    
    private void setupModel(ModelMap model, HttpServletRequest request) throws ServletException {
        // pass along deviceCollection
        DeviceCollection deviceCollection = this.deviceCollectionFactory.createDeviceCollection(request);
        model.addAttribute("deviceCollection", deviceCollection);
        
        long deviceCount = deviceCollection.getDeviceCount();
        model.addAttribute("deviceCount", deviceCount);
    }
    
    /**
     * DO MASS DELETE
     */
    @RequestMapping(value = "doMassDelete", method = RequestMethod.POST)
    public String doMassDelete(ModelMap model, HttpServletRequest request, YukonUserContext context) throws ServletException {

        DeviceCollection deviceCollection = this.deviceCollectionFactory.createDeviceCollection(request);
        boolean isFileUpload = !StringUtils.isEmpty(deviceCollection.getUploadFileName());
        CollectionActionResult result = collectionActionService.createResult(CollectionAction.MASS_DELETE, null,
            deviceCollection, context);
        // PROCESS
        SingleProcessor<SimpleDevice> bulkUpdater = new SingleProcessor<SimpleDevice>() {
            @Override
            public void process(SimpleDevice device) throws ProcessingException {
                processDeviceDelete(device, isFileUpload);
            }
        };
        
        ObjectMapper<SimpleDevice, SimpleDevice> mapper = new PassThroughMapper<>();
        bulkProcessor.backgroundBulkProcess(deviceCollection.iterator(), mapper, bulkUpdater,
            new CollectionActionBulkProcessorCallback(result, collectionActionService, collectionActionDao));
   
        return "redirect:/collectionActions/progressReport/view?key=" + result.getCacheKey();
    }
    
    private void processDeviceDelete(SimpleDevice device, boolean isFileUpload) {
        try {
            if (paoPersistenceDao.supports(device)) {
                paoPersistenceService.deletePao(device);
            } else {
                deviceDao.removeDevice(device);
            }
        } catch (DataRetrievalFailureException e) {
            String displayablePao = paoLoadingService.getDisplayablePao(device).getName();
            if (isFileUpload) {
                throw new ProcessingException("Could not find device: " + displayablePao + " (id="
                    + device.getDeviceId() + ")", "paoNotFound", e, displayablePao, device.getDeviceId());
            } else {
                throw new ProcessingException("Could not find device: " + displayablePao + " (id="
                    + device.getDeviceId() + ")", "paoNotFoundForSelectedDevices", e, device.getDeviceType(), displayablePao,
                    device.getDeviceId());
            }
        } catch (Exception e) {
            String displayablePao = paoLoadingService.getDisplayablePao(device).getName();
            if (isFileUpload) {
                throw new ProcessingException("Could not delete device: " + displayablePao + " (id="
                    + device.getDeviceId() + ")", "deleteDevice", e, displayablePao, device.getDeviceId());
            } else {
                throw new ProcessingException("Could not delete device: " + displayablePao + " (id="
                    + device.getDeviceId() + ")", "deleteSelectedDevices", e, device.getDeviceType(), displayablePao,
                    device.getDeviceId());
            }
        }
    }

}