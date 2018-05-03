package com.cannontech.web.bulk;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.bulk.callbackResult.BackgroundProcessTypeEnum;
import com.cannontech.common.bulk.collection.device.DeviceCollectionFactory;
import com.cannontech.common.bulk.collection.device.model.CollectionAction;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.processor.Processor;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.definition.model.PointTemplate;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.bulk.model.PaoTypeMasks;
import com.cannontech.web.bulk.model.UpdatePointsFieldType;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/updatePoints/*")
public class UpdatePointsController extends AddRemovePointsControllerBase {

    @Autowired private DeviceCollectionFactory deviceCollectionFactory;
    @Autowired private UpdatePointsProcessorFactory updatePointsProcessorFactory;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;

    // HOME
    @Override
    @RequestMapping(value = "home", method = RequestMethod.GET)
    public String home(ModelMap model, HttpServletRequest request) throws Exception, ServletException {
        setupModel(model, request);
        model.addAttribute("action", CollectionAction.UPDATE_POINTS);
        model.addAttribute("actionInputs", "/WEB-INF/pages/bulk/updatePoints/updatePointsHome.jsp");
        return "../collectionActions/collectionActionsHome.jsp";    }
    
    @RequestMapping(value = "updatePointsInputs", method = RequestMethod.GET)
    public String updatePointsInputs(ModelMap model, HttpServletRequest request) throws Exception, ServletException {
        setupModel(model, request);
        return "updatePoints/updatePointsHome.jsp";
    }
    
    private void setupModel(ModelMap model, HttpServletRequest request) throws ServletException {
        // device collection
        DeviceCollection deviceCollection = deviceCollectionFactory.createDeviceCollection(request);
        model.addAttribute("deviceCollection", deviceCollection);

        // options
        boolean sharedPoints = ServletRequestUtils.getBooleanParameter(request, "sharedPoints", true);
        boolean maskExistingPoints = ServletRequestUtils.getBooleanParameter(request, "maskExistingPoints", true);
        model.addAttribute("sharedPoints", sharedPoints);
        model.addAttribute("maskExistingPoints", maskExistingPoints);

        List<UpdatePointsFieldType> pointFields = Lists.newArrayList(UpdatePointsFieldType.values());
        model.addAttribute("pointFields", pointFields);

        // device types set
        Set<PaoType> deviceTypeSet = getDeviceTypesSet(deviceCollection);

        Map<PaoType, DeviceCollection> deviceTypeDeviceCollectionMap =
            getDeviceTypeDeviceCollectionMap(deviceTypeSet, deviceCollection);
        model.addAttribute("deviceTypeDeviceCollectionMap", deviceTypeDeviceCollectionMap);

        // device type points map
        List<PaoTypeMasks> paoTypeMasksList =
            createExistsPointsMap(deviceTypeSet, maskExistingPoints, false, deviceCollection);
        model.addAttribute("paoTypeMasksList", paoTypeMasksList);

        // shared points map
        Map<PointTemplate, Boolean> sharedPointTemplateMaskMap = createSharedPointsTemplateMap(paoTypeMasksList);

        PaoTypeMasks sharedPaoTypeMasks = new PaoTypeMasks();
        sharedPaoTypeMasks.setPointTemplateMaskMap(sharedPointTemplateMaskMap);
        model.addAttribute("sharedPaoTypeMasks", sharedPaoTypeMasks);
    }

    // EXECUTE ADD
    @Override
    @RequestMapping(value = "execute", method = RequestMethod.POST)
    public String execute(ModelMap model, HttpServletRequest request, YukonUserContext userContext, HttpServletResponse resp) throws ServletException, Exception {
        
        // device collection
        DeviceCollection deviceCollection = this.deviceCollectionFactory.createDeviceCollection(request);
        MessageSourceAccessor messageSourceAccessor = messageResolver.getMessageSourceAccessor(userContext);

        // options
        boolean sharedPoints = ServletRequestUtils.getRequiredBooleanParameter(request, "sharedPoints");
        boolean maskExistingPoints = ServletRequestUtils.getRequiredBooleanParameter(request, "maskExistingPoints");
        model.addAttribute("sharedPoints", sharedPoints);
        model.addAttribute("maskExistingPoints", maskExistingPoints);

        // mask existing points redirect
        String maskExistingPointsSubmitButton =
            ServletRequestUtils.getStringParameter(request, "maskExistingPointsSubmitButton");
        if (maskExistingPointsSubmitButton != null) {
            model.addAllAttributes(deviceCollection.getCollectionParameters());
            model.addAttribute("maskExistingPoints", !maskExistingPoints); // toggle it!
            return "redirect:home";
        }

        String modifyField = ServletRequestUtils.getStringParameter(request, "fieldToModify");
        UpdatePointsFieldType updateField = UpdatePointsFieldType.valueOf(modifyField);
        String setValue = ServletRequestUtils.getStringParameter(request, "setValue");

        Map<PaoType, Set<PointTemplate>> pointTemplatesMap =
            extractPointTemplatesMapFromParameters(request, deviceCollection, sharedPoints);

        if (pointTemplatesMap.isEmpty()) {
            String errorMsg = messageSourceAccessor.getMessage("yukon.common.device.bulk.updatePointsHome.noPointsSuppliedMsg");
            setupModel(model, request);
            return redirectWithError(model, errorMsg, deviceCollection, "updatePoints/updatePointsHome.jsp", resp);
        } else {
            String errorKey = validateInput(updateField, setValue, userContext);
            if (StringUtils.isNotBlank(errorKey)) {
                String errorMsg = messageSourceAccessor.getMessage("yukon.common.device.bulk.updatePointsHome." + errorKey);
                setupModel(model, request);
                return redirectWithError(model, errorMsg, deviceCollection, "updatePoints/updatePointsHome.jsp", resp);
            }
        }

        // create processor
        Processor<YukonDevice> updatePointsProcessor = null;
        if (updateField == UpdatePointsFieldType.ADJUSTED_MULTIPLIER) {
            Double value = Double.valueOf(setValue);
            updatePointsProcessor = updatePointsProcessorFactory.getAdjustMultiplierProcessor(pointTemplatesMap, value);
        } else if (updateField == UpdatePointsFieldType.EXPLICIT_MULTIPLIER) {
            Double value = Double.valueOf(setValue);
            updatePointsProcessor =
                updatePointsProcessorFactory.getExplicitMultiplierProcessor(pointTemplatesMap, value);
        } else if (updateField == UpdatePointsFieldType.DECIMAL_PLACES) {
            Integer value = Integer.valueOf(setValue);
            updatePointsProcessor = updatePointsProcessorFactory.getDecimalPlacesProcessor(pointTemplatesMap, value);
        }

        // start processor
        LinkedHashMap<String, String> userInputs = new LinkedHashMap<>();
        for (Map.Entry<PaoType, Set<PointTemplate>> entry : pointTemplatesMap.entrySet()) {
            List<String> points = new ArrayList<>();
            entry.getValue().forEach(template -> {
                points.add(template.getName());
            });
            userInputs.put(entry.getKey().getDbString(), String.join(", ", points));
        }
        userInputs.put("Update Field", updateField.getDisplayValue() + ": " + setValue);
        int key  =
            startBulkProcessor(CollectionAction.UPDATE_POINTS, deviceCollection, updatePointsProcessor, BackgroundProcessTypeEnum.UPDATE_POINTS, userContext, userInputs);

        return "redirect:/collectionActions/progressReport/detail?key=" + key;
    }

    /**
     * This method handles the special masking used for the shared update points set.
     */
    @Override
    protected boolean isSharedPointTemplateMasked(Iterable<Boolean> pointTemplateMaskSet) {
        for (Boolean pointTemplateMask : pointTemplateMaskSet) {
            if (pointTemplateMask == true) {
                return true;
            }
        }
        return false;
    }

    private String validateInput(UpdatePointsFieldType updateField, String setValue, YukonUserContext userContext) {
        String errorMsg = null;
        switch (updateField) {
        case EXPLICIT_MULTIPLIER:
        case ADJUSTED_MULTIPLIER:
            try {
                Double.parseDouble(setValue);
            } catch (NumberFormatException e) {
                errorMsg = "validDecimalNumberMsg";
            }
            break;

        case DECIMAL_PLACES:
            try {
                int intValue = Integer.parseInt(setValue);
                if (intValue < 0) {
                    errorMsg = "numberGreaterThanZeroMsg";
                }
            } catch (NumberFormatException e) {
                errorMsg = "validIntegerNumberMsg";
            }
            break;
        default:
            throw new IllegalArgumentException("The field type " + updateField + " is not supported.");
        }

        return errorMsg;
    }

}