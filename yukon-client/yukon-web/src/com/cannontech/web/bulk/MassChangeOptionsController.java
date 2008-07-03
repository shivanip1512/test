package com.cannontech.web.bulk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.common.bulk.BulkProcessor;
import com.cannontech.common.bulk.collection.DeviceCollection;
import com.cannontech.common.bulk.collection.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.field.BulkField;
import com.cannontech.common.bulk.field.BulkFieldColumnHeader;
import com.cannontech.common.bulk.field.BulkFieldService;
import com.cannontech.common.bulk.field.impl.BulkYukonDeviceFieldFactory;
import com.cannontech.common.bulk.field.impl.YukonDeviceDto;
import com.cannontech.common.bulk.field.processor.impl.BulkYukonDeviceFieldProcessor;
import com.cannontech.common.bulk.mapper.PassThroughMapper;
import com.cannontech.common.bulk.processor.ProcessingException;
import com.cannontech.common.bulk.processor.Processor;
import com.cannontech.common.bulk.processor.SingleProcessor;
import com.cannontech.common.bulk.service.BulkOperationCallbackResults;
import com.cannontech.common.bulk.service.BulkOperationTypeEnum;
import com.cannontech.common.bulk.service.MassChangeCallbackResults;
import com.cannontech.common.bulk.service.MassChangeFileInfo;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.web.bulk.model.DeviceCollectionFactory;
import com.cannontech.web.input.Input;
import com.cannontech.web.input.InputFormController;
import com.cannontech.web.input.InputRoot;


public class MassChangeOptionsController extends InputFormController {

    private BulkYukonDeviceFieldFactory bulkYukonDeviceFieldFactory = null;
    private BulkProcessor bulkProcessor = null;
    private DeviceCollectionFactory deviceCollectionFactory = null;
    private BulkFieldService bulkFieldService = null;
    
    private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao = null;
    private TemporaryDeviceGroupService temporaryDeviceGroupService = null;
    private DeviceGroupCollectionHelper deviceGroupCollectionHelper = null;
    private RecentResultsCache<BulkOperationCallbackResults<?>> recentResultsCache = null;
    
    @Override
    public InputRoot getInputRoot(HttpServletRequest request) throws Exception {
        
        String massChangeBulkFieldName = ServletRequestUtils.getRequiredStringParameter(request, "massChangeBulkFieldName");
        BulkField<?, YukonDevice> bulkField = bulkYukonDeviceFieldFactory.getBulkField(massChangeBulkFieldName);
        
        Input<?> inputSource = bulkField.getInputSource();
        
        List<Input<?>> inputList = new ArrayList<Input<?>>();
        inputList.add(inputSource);
        
        InputRoot inputRoot = new InputRoot();
        inputRoot.setInputList(inputList);
        
        return inputRoot;
    }
    
    @Override
    protected Map<String, Object> referenceData(HttpServletRequest request) throws Exception {

        Map<String, Object> refData = new HashMap<String, Object>();
        
        // renderer model map
        String massChangeBulkFieldName = ServletRequestUtils.getRequiredStringParameter(request, "massChangeBulkFieldName");
        BulkField<?, YukonDevice> bulkField = bulkYukonDeviceFieldFactory.getBulkField(massChangeBulkFieldName);
        
        // bulk field name
        refData.put("massChangeBulkFieldName", bulkField.getInputSource().getField());
        
        // input root
        refData.put("inputRoot", getInputRoot(request));
        
        // pass along deviceCollection
        DeviceCollection deviceCollection = this.deviceCollectionFactory.createDeviceCollection(request);
        refData.put("deviceCollection", deviceCollection);
        
        return refData;
    }
    
    @Override
    protected ModelAndView onSubmit(HttpServletRequest request,  HttpServletResponse response, Object command, BindException errors) throws Exception {
        
        // GET COMMAND, FIELD, AND COLLECTION
        //-------------------------------------------------------------------------------
        
        // cast command obj into YukonDeviceDto
        YukonDeviceDto yukonDeviceDtoObj = (YukonDeviceDto) command;
        
        // deviceCollection
        DeviceCollection deviceCollection = this.deviceCollectionFactory.createDeviceCollection(request);
        
        // get selected bulk field
        String massChangeBulkFieldName = ServletRequestUtils.getRequiredStringParameter(request, "massChangeBulkFieldName");
        BulkField<?, YukonDevice> bulkField = bulkYukonDeviceFieldFactory.getBulkField(massChangeBulkFieldName);
        BulkFieldColumnHeader bulkFieldColumnHeader = bulkFieldService.getColumnHeaderForFieldName(bulkField.getInputSource().getField());
        
        // PROCESS
        //-------------------------------------------------------------------------------

        // mapper
        ObjectMapper<YukonDevice, YukonDevice> mapper = new PassThroughMapper<YukonDevice>();
        
        // looking for a processor set that contains just the processor for this field
        BulkYukonDeviceFieldProcessor bulkFieldProcessor = findYukonDeviceFieldProcessor(bulkField);
        
        // create an instace of a Bulk Processor to run our fieldProcessor on the dto obj
        Processor<YukonDevice> bulkUpdater = getBulkProcessor(bulkFieldProcessor, yukonDeviceDtoObj);
        
        
        // CALL BACK SETUP
        // going to use a DeviceGroupAddingBulkProcessorCallback to add success/fail devices to temp groups
        
        // create a temp group
        final StoredDeviceGroup successGroup = temporaryDeviceGroupService.createTempGroup(null);
        final StoredDeviceGroup processingExceptionGroup = temporaryDeviceGroupService.createTempGroup(null);
        
        // init callcback, use a TranslatingBulkProcessorCallback to get from UpdateableDevice to YukonDevice
        MassChangeCallbackResults bulkOperationCallbackResults = new MassChangeCallbackResults(successGroup, processingExceptionGroup, deviceGroupMemberEditorDao, deviceGroupCollectionHelper, Collections.singletonList(bulkFieldColumnHeader), BulkOperationTypeEnum.MASS_CHANGE);
        
        ModelAndView mav = new ModelAndView("massChange/massChangeResults.jsp");
        mav.addObject("deviceCollection", deviceCollection);
        mav.addObject("massChangeBulkFieldName", massChangeBulkFieldName);
        
        
        // STORE RESULTS INFO TO CACHE
        String id = StringUtils.replace(UUID.randomUUID().toString(), "-", "");
        bulkOperationCallbackResults.setResultsId(id);
        MassChangeFileInfo massChangeFileInfo = new MassChangeFileInfo(deviceCollection, massChangeBulkFieldName);
        bulkOperationCallbackResults.setBulkFileInfo(massChangeFileInfo);
        recentResultsCache.addResult(id, bulkOperationCallbackResults);
        
        mav.addObject("bulkUpdateOperationResults", bulkOperationCallbackResults);
        
        // PROCESS
        bulkProcessor.backgroundBulkProcess(deviceCollection.iterator(), mapper, bulkUpdater, bulkOperationCallbackResults);
        
        return mav;
    }
    
    

    private BulkYukonDeviceFieldProcessor findYukonDeviceFieldProcessor(BulkField<?, YukonDevice> bulkField) {
        
        Set<BulkField<?, YukonDevice>> requiredSet = new HashSet<BulkField<?, YukonDevice>>(1);
        requiredSet.add(bulkField);
        
        // the following is a naive implementation and should be changed when more
        // complete processors are added
        List<BulkYukonDeviceFieldProcessor> allBulkFieldProcessors = bulkFieldService.getBulkFieldProcessors();
        BulkYukonDeviceFieldProcessor bulkFieldProcessor = null;
        for (BulkYukonDeviceFieldProcessor processor : allBulkFieldProcessors) {
            if (requiredSet.equals(processor.getUpdatableFields())) {
                bulkFieldProcessor = processor;
            }
        }
        
        return bulkFieldProcessor;
    }

    private Processor<YukonDevice> getBulkProcessor(final BulkYukonDeviceFieldProcessor bulkFieldProcessor, final YukonDeviceDto yukonDeviceDtoObj) {
        
        return new SingleProcessor<YukonDevice>() {
            
            @Override
            public void process(YukonDevice device) throws ProcessingException {
                bulkFieldProcessor.updateField(device, yukonDeviceDtoObj);
            }
        };
    }

    @Override
    protected Object formBackingObject(HttpServletRequest request) throws Exception {
        
        return new YukonDeviceDto();
    }
    
    @Required
    public void setBulkYukonDeviceFieldFactory(BulkYukonDeviceFieldFactory bulkYukonDeviceFieldFactory) {
        this.bulkYukonDeviceFieldFactory = bulkYukonDeviceFieldFactory;
    }


    @Required
    public void setDeviceCollectionFactory(
            DeviceCollectionFactory deviceCollectionFactory) {
        this.deviceCollectionFactory = deviceCollectionFactory;
    }

    @Required
    public void setBulkProcessor(BulkProcessor bulkProcessor) {
        this.bulkProcessor = bulkProcessor;
    }
    
    @Required
    public void setBulkFieldService(BulkFieldService bulkFieldService) {
        this.bulkFieldService = bulkFieldService;
    }

    @Required
    public void setDeviceGroupCollectionHelper(
            DeviceGroupCollectionHelper deviceGroupCollectionHelper) {
        this.deviceGroupCollectionHelper = deviceGroupCollectionHelper;
    }

    @Required
    public void setTemporaryDeviceGroupService(
            TemporaryDeviceGroupService temporaryDeviceGroupService) {
        this.temporaryDeviceGroupService = temporaryDeviceGroupService;
    }

    @Required
    public void setDeviceGroupMemberEditorDao(
            DeviceGroupMemberEditorDao deviceGroupMemberEditorDao) {
        this.deviceGroupMemberEditorDao = deviceGroupMemberEditorDao;
    }
    
    @Required
    public void setRecentBulkOperationResultsCache(
            RecentResultsCache<BulkOperationCallbackResults<?>> recentResultsCache) {
        this.recentResultsCache = recentResultsCache;
    }

}
