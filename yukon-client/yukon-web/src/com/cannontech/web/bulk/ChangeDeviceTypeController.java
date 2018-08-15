package com.cannontech.web.bulk;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.common.bulk.BulkProcessor;
import com.cannontech.common.bulk.collection.device.DeviceCollectionFactory;
import com.cannontech.common.bulk.collection.device.dao.CollectionActionDao;
import com.cannontech.common.bulk.collection.device.model.CollectionAction;
import com.cannontech.common.bulk.collection.device.model.CollectionActionBulkProcessorCallback;
import com.cannontech.common.bulk.collection.device.model.CollectionActionInput;
import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.collection.device.service.CollectionActionService;
import com.cannontech.common.bulk.mapper.PassThroughMapper;
import com.cannontech.common.bulk.processor.ProcessingException;
import com.cannontech.common.bulk.processor.SingleProcessor;
import com.cannontech.common.bulk.service.ChangeDeviceTypeService;
import com.cannontech.common.bulk.service.ChangeDeviceTypeService.ChangeDeviceTypeInfo;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.model.PaoDefinition;
import com.cannontech.common.pao.definition.service.PaoDefinitionService;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.model.RfnManufacturerModel;
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
    @Autowired private CollectionActionService collectionActionService;
    @Autowired private CollectionActionDao collectionActionDao;
    @Autowired private RfnDeviceDao rfnDeviceDao;

    /**
     * CHOOSE DEVICE TYPE TO CHANGE TO
     */
    @RequestMapping(value = "chooseDeviceType", method = RequestMethod.GET)
    public String chooseDeviceType(ModelMap model, HttpServletRequest request) throws ServletException {
        setupModel(model, request);
        model.addAttribute("action", CollectionAction.CHANGE_TYPE);
        model.addAttribute("actionInputs", "/WEB-INF/pages/bulk/changeDeviceType/chooseDeviceType.jsp");
        return "../collectionActions/collectionActionsHome.jsp";
    }
    
    @RequestMapping(value = "chooseDeviceTypeInputs", method = RequestMethod.GET)
    public String chooseDeviceTypeInputs(ModelMap model, HttpServletRequest request) throws ServletException {
        setupModel(model, request);
        return "changeDeviceType/chooseDeviceType.jsp";
    }
    
    private void setupModel(ModelMap model, HttpServletRequest request) throws ServletException {
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
    }
    
    /**
     * DO DEVICE TYPE CHANGE
     */
    @RequestMapping(value = "changeDeviceType", method = RequestMethod.POST)
    public String changeDeviceType(ModelMap model, HttpServletRequest request, YukonUserContext context) throws ServletException {

        DeviceCollection deviceCollection = this.deviceCollectionFactory.createDeviceCollection(request);
        
        final PaoType selectedDeviceType = ServletRequestEnumUtils.getRequiredEnumParameter(request, PaoType.class, "deviceTypes"); 
        LinkedHashMap<String, String> userInputs = new LinkedHashMap<>();
        userInputs.put(CollectionActionInput.DEVICE_TYPE.name(),  selectedDeviceType.getDbString());
        CollectionActionResult result = collectionActionService.createResult(CollectionAction.CHANGE_TYPE, userInputs,
            deviceCollection, context);
        
        // PROCESS
        SingleProcessor<SimpleDevice> bulkUpdater = new SingleProcessor<SimpleDevice>() {
            @Override
            public void process(SimpleDevice device) throws ProcessingException {
                ChangeDeviceTypeInfo info = null;
                if (device.getPaoIdentifier().getPaoType().isRfn() && selectedDeviceType.isRfn()) {
                    String sn = rfnDeviceDao.getDevice(device).getRfnIdentifier().getSensorSerialNumber();
                    RfnManufacturerModel rfnModel = RfnManufacturerModel.getForType(selectedDeviceType).get(0);
                    RfnIdentifier rfnIdentifier = new RfnIdentifier(sn, rfnModel.getManufacturer(), rfnModel.getModel());
                    info = new ChangeDeviceTypeInfo(rfnIdentifier);
                }
                changeDeviceTypeService.changeDeviceType(device, selectedDeviceType, info);
            }
        };
        
        ObjectMapper<SimpleDevice, SimpleDevice> mapper = new PassThroughMapper<>();
        bulkProcessor.backgroundBulkProcess(deviceCollection.iterator(), mapper, bulkUpdater,
            new CollectionActionBulkProcessorCallback(result, collectionActionService, collectionActionDao));
        
        return "redirect:/collectionActions/progressReport/detail?key=" + result.getCacheKey();
    }
    
}