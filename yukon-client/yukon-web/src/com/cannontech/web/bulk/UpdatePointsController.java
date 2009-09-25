package com.cannontech.web.bulk;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.common.bulk.callbackResult.BackgroundProcessTypeEnum;
import com.cannontech.common.bulk.collection.DeviceCollection;
import com.cannontech.common.bulk.processor.Processor;
import com.cannontech.common.device.definition.model.PointTemplate;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.web.bulk.model.UpdatePointsFieldType;
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
        boolean maskExistingPoints = ServletRequestUtils.getBooleanParameter(request, "maskExistingPoints", false);
        mav.addObject("sharedPoints", sharedPoints);
        mav.addObject("maskExistingPoints", maskExistingPoints);
        
        List<UpdatePointsFieldType> pointFields = Lists.newArrayList(UpdatePointsFieldType.values());
        mav.addObject("pointFields", pointFields);
        
        // device types set
        Set<Integer> deviceTypeSet = getDeviceTypesSet(deviceCollection);
        
        // device type names map
        Map<Integer, String> deviceTypeNamesMap = getDeviceTypeNamesMap(deviceTypeSet);
        mav.addObject("deviceTypeNamesMap", deviceTypeNamesMap);
        
        Map<Integer, PaoType> deviceTypeEnumMap = getDeviceTypeEnumMap(deviceTypeSet);
        mav.addObject("deviceTypeEnumMap", deviceTypeEnumMap);
        
        Map<Integer, DeviceCollection> deviceTypeDeviceCollectionMap = getDeviceTypeDeviceCollectionMap(deviceTypeSet, deviceCollection);
        mav.addObject("deviceTypeDeviceCollectionMap", deviceTypeDeviceCollectionMap);

        // device type points map
        Map<Integer, Map<String, List<PointTemplateWrapper>>> pointsMap = createExistsPointsMap(deviceTypeSet, maskExistingPoints, false, deviceCollection);
        mav.addObject("pointsMap", pointsMap);
        
        // shared points map
        Map<String, List<PointTemplateWrapper>> sharedPointsTypeMap = createSharedPointsTypeMapWithPointsMap(pointsMap);
        mav.addObject("sharedPointsTypeMap", sharedPointsTypeMap);
        
        return mav;
    }
    
    // EXECUTE ADD
    public ModelAndView execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, Exception {
        
        ModelAndView mav = new ModelAndView("redirect:updatePointsResults");
        
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
        
        Map<Integer, Set<PointTemplate>> pointTemplatesMap = extractPointTemplatesMapFromParameters(request, deviceCollection, sharedPoints);
        
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
