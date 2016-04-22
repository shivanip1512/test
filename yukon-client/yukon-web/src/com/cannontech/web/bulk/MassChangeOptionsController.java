package com.cannontech.web.bulk;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.common.bulk.BulkProcessor;
import com.cannontech.common.bulk.callbackResult.BackgroundProcessResultHolder;
import com.cannontech.common.bulk.callbackResult.MassChangeCallbackResult;
import com.cannontech.common.bulk.collection.device.DeviceCollectionFactory;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
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
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.input.Input;
import com.cannontech.web.input.InputRoot;
import com.cannontech.web.input.InputSource;
import com.cannontech.web.input.type.InputType;
import com.cannontech.web.input.validate.InputBindingErrorProcessor;
import com.cannontech.web.input.validate.InputValidator;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.collect.ImmutableList;

@Controller
@CheckRoleProperty(YukonRoleProperty.MASS_CHANGE)
public class MassChangeOptionsController {

    @Autowired private BulkYukonDeviceFieldFactory bulkYukonDeviceFieldFactory;
    private @Resource(name = "resubmittingBulkProcessor") BulkProcessor bulkProcessor;
    @Autowired private DeviceCollectionFactory deviceCollectionFactory;
    @Autowired private BulkFieldService bulkFieldService;
    @Autowired private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    @Autowired private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
    @Autowired private TemporaryDeviceGroupService temporaryDeviceGroupService;
    private @Resource(name = "recentResultsCache") RecentResultsCache<BackgroundProcessResultHolder> recentResultsCache;

    @RequestMapping("massChangeOptions")
    protected String massChangeOptions(ModelMap model, HttpServletRequest request) throws Exception {

        YukonDeviceDto yukonDeviceDto = new YukonDeviceDto();
        // for mass change options page, we'd like 'enable' to be the default selected value
        yukonDeviceDto.setEnable(true);
        // renderer model map
        String massChangeBulkFieldName =
            ServletRequestUtils.getRequiredStringParameter(request, "massChangeBulkFieldName");
        BulkField<?, SimpleDevice> bulkField = bulkYukonDeviceFieldFactory.getBulkField(massChangeBulkFieldName);

        // bulk field name
        model.put("massChangeBulkFieldName", bulkField.getInputSource().getField());

        // input root
        model.put("inputRoot", getInputRoot(request));

        // pass along deviceCollection
        DeviceCollection deviceCollection = this.deviceCollectionFactory.createDeviceCollection(request);
        model.put("deviceCollection", deviceCollection);
        model.put("deviceCollection", deviceCollection);
        model.put("massChangeOptions", yukonDeviceDto);

        return "massChange/massChangeOptions.jsp";
    }

    @RequestMapping(value = "massChangeOptions", method = RequestMethod.POST)
    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response,
            @ModelAttribute("massChangeOptions") YukonDeviceDto yukonDeviceDtoObj, BindingResult result)
            throws Exception {

        // deviceCollection
        validateHttpRequestParameters(request, yukonDeviceDtoObj, result);
        DeviceCollection deviceCollection = this.deviceCollectionFactory.createDeviceCollection(request);

        // get selected bulk field
        String massChangeBulkFieldName =
            ServletRequestUtils.getRequiredStringParameter(request, "massChangeBulkFieldName");
        BulkField<?, SimpleDevice> bulkField = bulkYukonDeviceFieldFactory.getBulkField(massChangeBulkFieldName);
        BulkFieldColumnHeader bulkFieldColumnHeader =
            bulkFieldService.getColumnHeaderForFieldName(bulkField.getInputSource().getField());

        // PROCESS
        // -------------------------------------------------------------------------------

        // CALLBACK
        String resultsId = StringUtils.replace(UUID.randomUUID().toString(), "-", "");
        StoredDeviceGroup successGroup = temporaryDeviceGroupService.createTempGroup();
        StoredDeviceGroup processingExceptionGroup = temporaryDeviceGroupService.createTempGroup();

        MassChangeCallbackResult callbackResult =
            new MassChangeCallbackResult(bulkFieldColumnHeader, resultsId, deviceCollection, successGroup,
                processingExceptionGroup, deviceGroupMemberEditorDao, deviceGroupCollectionHelper);

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

    private Processor<SimpleDevice> getBulkProcessor(final BulkYukonDeviceFieldProcessor bulkFieldProcessor,
            final YukonDeviceDto yukonDeviceDtoObj) {

        return new SingleProcessor<SimpleDevice>() {

            @Override
            public void process(SimpleDevice device) throws ProcessingException {
                bulkFieldProcessor.updateField(device, yukonDeviceDtoObj);
            }
        };
    }

    protected void validateHttpRequestParameters(HttpServletRequest request, Object command, BindingResult errors)
            throws Exception {

        Map<String, InputSource<?>> inputMap = getInputRoot(request).getInputMap();
        List<Input<?>> inputList = getInputRoot(request).getInputList();

        BeanWrapper beanWrapper = new BeanWrapperImpl(command);
        Object value = null;

        // Validate each of the input values individually
        for (String fieldPath : inputMap.keySet()) {

            Input<?> input = inputMap.get(fieldPath);

            // Get the submitted value
            value = beanWrapper.getPropertyValue(fieldPath);

            List<? extends InputValidator<?>> validatorList = input.getValidatorList();

            for (InputValidator validator : validatorList) {
                // Validate the value
                validator.validate(fieldPath, input.getDisplayName(), value, errors);
            }
        }

        for (InputValidator validator : getGlobalValidators()) {
            validator.validate(null, "", command, errors);
        }

    }

    protected List<InputValidator<?>> getGlobalValidators() {
        return ImmutableList.of();
    }

    @InitBinder
    protected void initBinder(HttpServletRequest request, WebDataBinder binder) throws Exception {

        Map<String, ? extends InputSource<?>> inputMap = getInputRoot(request).getInputMap();

        LiteYukonUser user = ServletUtil.getYukonUser(request);
        List<String> notEditableFields = new ArrayList<String>();

        // Initialize the binder for each input

        InputSource<?> input = null;
        InputType<?> inputType = null;
        for (String fieldPath : inputMap.keySet()) {

            input = inputMap.get(fieldPath);
            inputType = input.getType();

            // Register property editor for this input
            binder.registerCustomEditor(inputType.getTypeClass(), fieldPath, inputType.getPropertyEditor());

            // See if the input is editable
            if (!input.getSecurity().isEditable(user)) {
                notEditableFields.add(fieldPath);
            }

        }

        binder.setBindingErrorProcessor(new InputBindingErrorProcessor(inputMap));

        // Ignore binding for fields that are not editable
        binder.setDisallowedFields(notEditableFields.toArray(new String[] {}));

    }

    private InputRoot getInputRoot(HttpServletRequest request) throws Exception {

        String massChangeBulkFieldName =
            ServletRequestUtils.getRequiredStringParameter(request, "massChangeBulkFieldName");
        BulkField<?, SimpleDevice> bulkField = bulkYukonDeviceFieldFactory.getBulkField(massChangeBulkFieldName);

        Input<?> inputSource = bulkField.getInputSource();

        List<Input<?>> inputList = new ArrayList<Input<?>>();
        inputList.add(inputSource);

        InputRoot inputRoot = new InputRoot();
        inputRoot.setInputList(inputList);

        return inputRoot;
    }

}
