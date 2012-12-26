package com.cannontech.web.bulk;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.common.bulk.callbackResult.BackgroundProcessTypeEnum;
import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.bulk.processor.Processor;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.definition.model.PointTemplate;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.bulk.model.PaoTypeMasks;
import com.cannontech.web.bulk.model.UpdatePointsFieldType;
import com.cannontech.web.common.flashScope.FlashScope;
import com.google.common.collect.Lists;

public class UpdatePointsController extends AddRemovePointsControllerBase {
    
    private UpdatePointsProcessorFactory updatePointsProcessorFactory;

    // HOME
    public ModelAndView home(HttpServletRequest request, HttpServletResponse response) throws Exception, ServletException {
        
        ModelAndView mav = new ModelAndView("updatePoints/updatePointsHome.jsp");
        
        // device collection
        DeviceCollection deviceCollection = this.deviceCollectionFactory.createDeviceCollection(request);
        mav.addObject("deviceCollection", deviceCollection);
        
        // options
        boolean sharedPoints = ServletRequestUtils.getBooleanParameter(request, "sharedPoints", true);
        boolean maskExistingPoints = ServletRequestUtils.getBooleanParameter(request, "maskExistingPoints", true);
        mav.addObject("sharedPoints", sharedPoints);
        mav.addObject("maskExistingPoints", maskExistingPoints);

        String errorMsg = ServletRequestUtils.getStringParameter(request, "errorMsg");
        if(StringUtils.isNotBlank(errorMsg)){
        	FlashScope flashScope = new FlashScope(request);
        	flashScope.setError(new YukonMessageSourceResolvable("yukon.common.device.bulk.updatePointsHome."+errorMsg));
        }
        
        List<UpdatePointsFieldType> pointFields = Lists.newArrayList(UpdatePointsFieldType.values());
        mav.addObject("pointFields", pointFields);
        
        // device types set
        Set<PaoType> deviceTypeSet = getDeviceTypesSet(deviceCollection);
        
        Map<PaoType, DeviceCollection> deviceTypeDeviceCollectionMap = getDeviceTypeDeviceCollectionMap(deviceTypeSet, deviceCollection);
        mav.addObject("deviceTypeDeviceCollectionMap", deviceTypeDeviceCollectionMap);

        // device type points map
        List<PaoTypeMasks> paoTypeMasksList = createExistsPointsMap(deviceTypeSet, maskExistingPoints, false, deviceCollection);
        mav.addObject("paoTypeMasksList", paoTypeMasksList);
        
        // shared points map
        Map<PointTemplate, Boolean> sharedPointTemplateMaskMap = createSharedPointsTemplateMap(paoTypeMasksList);

        PaoTypeMasks sharedPaoTypeMasks = new PaoTypeMasks();
        sharedPaoTypeMasks.setPointTemplateMaskMap(sharedPointTemplateMaskMap);
        mav.addObject("sharedPaoTypeMasks", sharedPaoTypeMasks);
        
        return mav;
    }
    
    // EXECUTE ADD
    public ModelAndView execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, Exception {
        
        ModelAndView mav = new ModelAndView("redirect:updatePointsResults");
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        
        // device collection
        DeviceCollection deviceCollection = this.deviceCollectionFactory.createDeviceCollection(request);
        
        // options
        boolean sharedPoints = ServletRequestUtils.getRequiredBooleanParameter(request, "sharedPoints");
        boolean maskExistingPoints = ServletRequestUtils.getRequiredBooleanParameter(request, "maskExistingPoints");
        mav.addObject("sharedPoints", sharedPoints);
        mav.addObject("maskExistingPoints", maskExistingPoints);
        
        // mask existing points redirect
        String maskExistingPointsSubmitButton = ServletRequestUtils.getStringParameter(request, "maskExistingPointsSubmitButton");
        if (maskExistingPointsSubmitButton != null) {
            
            mav.setViewName("redirect:home");
            mav.addAllObjects(deviceCollection.getCollectionParameters());
            mav.addObject("maskExistingPoints", !maskExistingPoints); // toggle it!
            return mav;
        }
        
        String modifyField = ServletRequestUtils.getStringParameter(request, "fieldToModify");
        UpdatePointsFieldType updateField = UpdatePointsFieldType.valueOf(modifyField);
        String setValue = ServletRequestUtils.getStringParameter(request, "setValue");
        
        Map<PaoType, Set<PointTemplate>> pointTemplatesMap = extractPointTemplatesMapFromParameters(request, deviceCollection, sharedPoints);
        
        if(pointTemplatesMap.isEmpty()){
            String noPointsSuppliedMsg = "noPointsSuppliedMsg";
            ModelAndView home = redirectWithError(noPointsSuppliedMsg, deviceCollection);
            return home;
        } else {
            String errorMsg = validateInput(updateField, setValue, userContext);
            if(StringUtils.isNotBlank(errorMsg)){
                ModelAndView home = redirectWithError(errorMsg, deviceCollection);
                return home;
            }
        }
        
        // create processor
        Processor<YukonDevice> updatePointsProcessor = null;
        if (updateField == UpdatePointsFieldType.ADJUSTED_MULTIPLIER){
            Double value = Double.valueOf(setValue);
            updatePointsProcessor= updatePointsProcessorFactory.getAdjustMultiplierProcessor(pointTemplatesMap, value);
        } else if (updateField == UpdatePointsFieldType.EXPLICIT_MULTIPLIER) {
            Double value = Double.valueOf(setValue);
            updatePointsProcessor= updatePointsProcessorFactory.getExplicitMultiplierProcessor(pointTemplatesMap, value);
        } else if (updateField == UpdatePointsFieldType.DECIMAL_PLACES) {
            Integer value = Integer.valueOf(setValue);
            updatePointsProcessor= updatePointsProcessorFactory.getDecimalPlacesProcessor(pointTemplatesMap, value);
        }
        
        // start processor
        String id = startBulkProcessor(deviceCollection, updatePointsProcessor, BackgroundProcessTypeEnum.UPDATE_POINTS);
        
        // redirect to results page
        mav.addObject("resultsId", id);
        return mav;
    }
    
    @Override
    /**
     *  This method handles the special masking used for the shared update points set.
     */
    protected boolean isSharedPointTemplateMasked(Iterable<Boolean> pointTemplateMaskSet){
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
        case EXPLICIT_MULTIPLIER :
        case ADJUSTED_MULTIPLIER :
            try {
                Double.parseDouble(setValue);
            } catch (NumberFormatException e){
                errorMsg = "validDecimalNumberMsg";
            }
            break;
            
        case DECIMAL_PLACES :
            try {
                int intValue = Integer.parseInt(setValue);
                if(intValue < 0){
                    errorMsg = "numberGreaterThanZeroMsg";
                }
            } catch (NumberFormatException e) {
                errorMsg = "validIntegerNumberMsg";
            }
            break;
        default:
            throw new IllegalArgumentException("The field type "+updateField+" is not supported.");
        } 
        
        return errorMsg;
    }

    // VIEW RESULTS
    public ModelAndView updatePointsResults(HttpServletRequest request, HttpServletResponse response) throws ServletException {

        ModelAndView mav = new ModelAndView("updatePoints/updatePointsResults.jsp");
        
        // prepare mav with basic results data
        prepResultsView(request, mav);
        
        // options
        boolean sharedPoints = ServletRequestUtils.getBooleanParameter(request, "sharedPoints", true);
        boolean maskExistingPoints = ServletRequestUtils.getBooleanParameter(request, "maskExistingPoints", false);
        mav.addObject("sharedPoints", sharedPoints);
        mav.addObject("maskExistingPoints", maskExistingPoints);
        
        return mav;
    }
    
    @Autowired
    public void setUpdatePointsProcessorFactory(UpdatePointsProcessorFactory updatePointsProcessorFactory){
        this.updatePointsProcessorFactory = updatePointsProcessorFactory;
    }
}
