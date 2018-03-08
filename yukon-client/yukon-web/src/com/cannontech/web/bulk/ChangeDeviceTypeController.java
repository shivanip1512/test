package com.cannontech.web.bulk;

import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.BulkProcessor;
import com.cannontech.common.bulk.collection.device.DeviceCollectionFactory;
import com.cannontech.common.bulk.collection.device.model.CollectionAction;
import com.cannontech.common.bulk.collection.device.model.CollectionActionBulkProcessorCallback;
import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.collection.device.service.CollectionActionService;
import com.cannontech.common.bulk.mapper.PassThroughMapper;
import com.cannontech.common.bulk.processor.ProcessingException;
import com.cannontech.common.bulk.processor.SingleProcessor;
import com.cannontech.common.bulk.service.ChangeDeviceTypeService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.model.PaoDefinition;
import com.cannontech.common.pao.definition.service.PaoDefinitionService;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.util.ServletRequestEnumUtils;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@CheckRoleProperty(YukonRoleProperty.MASS_CHANGE)
@Controller
@RequestMapping("changeDeviceType/*")
public class ChangeDeviceTypeController {

    @Resource(name="resubmittingBulkProcessor") private BulkProcessor bulkProcessor;
    @Autowired private PaoDefinitionService paoDefinitionService;
    @Autowired private ChangeDeviceTypeService changeDeviceTypeService;
    @Autowired private DeviceCollectionFactory deviceCollectionFactory;
    @Autowired protected CollectionActionService collectionActionService;
    private Logger log = YukonLogManager.getLogger(ChangeDeviceTypeController.class);
    
    /**
     * CHOOSE DEVICE TYPE TO CHANGE TO
     */
    @RequestMapping("chooseDeviceType")
    public String chooseDeviceType(ModelMap model, HttpServletRequest request) throws ServletException {

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
                    // skip all RFN types. We do not have a way to pass in Model/Manufacturer in the collection action for Change Type.
                } else {
                    deviceTypes.put(paoDefinition.getDisplayName(), paoDefinition.getType());
                }
            }
        }
        model.addAttribute("deviceTypes", deviceTypes);

        return "changeDeviceType/chooseDeviceType.jsp";
    }
    
    /**
     * DO DEVICE TYPE CHANGE
     */
    @RequestMapping("changeDeviceType")
    public String changeDeviceType(ModelMap model, HttpServletRequest request, YukonUserContext context) throws ServletException {

        DeviceCollection deviceCollection = this.deviceCollectionFactory.createDeviceCollection(request);
        
        CollectionActionResult result = collectionActionService.createResult(CollectionAction.CHANGE_TYPE, null,
            deviceCollection, context);
        
        // PROCESS
        final PaoType selectedDeviceType = ServletRequestEnumUtils.getRequiredEnumParameter(request, PaoType.class, "deviceTypes"); 
        SingleProcessor<SimpleDevice> bulkUpdater = new SingleProcessor<SimpleDevice>() {
            @Override
            public void process(SimpleDevice device) throws ProcessingException {
                changeDeviceTypeService.changeDeviceType(device, selectedDeviceType, null);
            }
        };
        
        ObjectMapper<SimpleDevice, SimpleDevice> mapper = new PassThroughMapper<>();
        bulkProcessor.backgroundBulkProcess(deviceCollection.iterator(), mapper, bulkUpdater,
            new CollectionActionBulkProcessorCallback(result, collectionActionService, log));
        
        return "redirect:/bulk/progressReport/detail?key=" + result.getCacheKey();
    }
    
    /**
     * CHANGE DEVICE TYPE RESULTS
     */
    @RequestMapping("changeDeviceTypeResults")
    public String changeDeviceTypeResults(ModelMap model, HttpServletRequest request) throws ServletException {

        // result info
      /*  String resultsId = ServletRequestUtils.getRequiredStringParameter(request, "resultsId");
        ChangeDeviceTypeCallbackResult callbackResult = (ChangeDeviceTypeCallbackResult)recentResultsCache.getResult(resultsId);
        
        model.addAttribute("deviceCollection", callbackResult.getDeviceCollection());
        model.addAttribute("callbackResult", callbackResult);
        model.addAttribute("fileName", callbackResult.getDeviceCollection().getUploadFileName());*/
        return "changeDeviceType/changeDeviceTypeResults.jsp";
    }
    
}