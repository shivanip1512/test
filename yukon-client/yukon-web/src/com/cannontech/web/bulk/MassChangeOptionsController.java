package com.cannontech.web.bulk;

import java.util.ArrayList;
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
import com.cannontech.common.bulk.callbackResult.BackgroundProcessResultHolder;
import com.cannontech.common.bulk.callbackResult.MassChangeCallbackResult;
import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.bulk.collection.device.DeviceCollectionFactory;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
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
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.web.input.Input;
import com.cannontech.web.input.InputFormController;
import com.cannontech.web.input.InputRoot;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@CheckRoleProperty(YukonRoleProperty.MASS_CHANGE)
public class MassChangeOptionsController extends InputFormController {

    private BulkYukonDeviceFieldFactory bulkYukonDeviceFieldFactory;
    private BulkProcessor bulkProcessor;
    private DeviceCollectionFactory deviceCollectionFactory;
    private BulkFieldService bulkFieldService;
    private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
    private TemporaryDeviceGroupService temporaryDeviceGroupService;
    private RecentResultsCache<BackgroundProcessResultHolder> recentResultsCache;
    
    @Override
    public InputRoot getInputRoot(HttpServletRequest request) throws Exception {
        
        String massChangeBulkFieldName = ServletRequestUtils.getRequiredStringParameter(request, "massChangeBulkFieldName");
        BulkField<?, SimpleDevice> bulkField = bulkYukonDeviceFieldFactory.getBulkField(massChangeBulkFieldName);
        
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
        BulkField<?, SimpleDevice> bulkField = bulkYukonDeviceFieldFactory.getBulkField(massChangeBulkFieldName);
        
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
        BulkField<?, SimpleDevice> bulkField = bulkYukonDeviceFieldFactory.getBulkField(massChangeBulkFieldName);
        BulkFieldColumnHeader bulkFieldColumnHeader = bulkFieldService.getColumnHeaderForFieldName(bulkField.getInputSource().getField());
        
        // PROCESS
        //-------------------------------------------------------------------------------

        // CALLBACK
        String resultsId = StringUtils.replace(UUID.randomUUID().toString(), "-", "");
        StoredDeviceGroup successGroup = temporaryDeviceGroupService.createTempGroup(null);
        StoredDeviceGroup processingExceptionGroup = temporaryDeviceGroupService.createTempGroup(null);
        
        MassChangeCallbackResult callbackResult = new MassChangeCallbackResult(bulkFieldColumnHeader,
														        		resultsId,
														        		deviceCollection,
																		successGroup, 
																		processingExceptionGroup, 
																		deviceGroupMemberEditorDao,
																		deviceGroupCollectionHelper);
        
        // CACHE
        recentResultsCache.addResult(resultsId, callbackResult);
        
        // PROCESS
        BulkYukonDeviceFieldProcessor bulkFieldProcessor = findYukonDeviceFieldProcessor(bulkField);
        Processor<SimpleDevice> bulkUpdater = getBulkProcessor(bulkFieldProcessor, yukonDeviceDtoObj);
        ObjectMapper<SimpleDevice, SimpleDevice> mapper = new PassThroughMapper<SimpleDevice>();
        
        bulkProcessor.backgroundBulkProcess(deviceCollection.iterator(), mapper, bulkUpdater, callbackResult);
        
        ModelAndView mav = new ModelAndView("redirect:massChange/massChangeResults");
        mav.addObject("resultsId", resultsId);
        return mav;
    }
    
    

    private BulkYukonDeviceFieldProcessor findYukonDeviceFieldProcessor(BulkField<?, SimpleDevice> bulkField) {
        
        Set<BulkField<?, SimpleDevice>> requiredSet = new HashSet<BulkField<?, SimpleDevice>>(1);
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

    private Processor<SimpleDevice> getBulkProcessor(final BulkYukonDeviceFieldProcessor bulkFieldProcessor, final YukonDeviceDto yukonDeviceDtoObj) {
        
        return new SingleProcessor<SimpleDevice>() {
            
            @Override
            public void process(SimpleDevice device) throws ProcessingException {
                bulkFieldProcessor.updateField(device, yukonDeviceDtoObj);
            }
        };
    }

    @Override
    protected Object formBackingObject(HttpServletRequest request) throws Exception {
        
        YukonDeviceDto yukonDeviceDto = new YukonDeviceDto();

        // for mass change options page, we'd like 'enable' to be the default selected value
        yukonDeviceDto.setEnable(true);
        
        return yukonDeviceDto;
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
    public void setDeviceGroupCollectionHelper(DeviceGroupCollectionHelper deviceGroupCollectionHelper) {
		this.deviceGroupCollectionHelper = deviceGroupCollectionHelper;
	}
    
    @Required
    public void setRecentResultsCache(RecentResultsCache<BackgroundProcessResultHolder> recentResultsCache) {
        this.recentResultsCache = recentResultsCache;
    }

}
