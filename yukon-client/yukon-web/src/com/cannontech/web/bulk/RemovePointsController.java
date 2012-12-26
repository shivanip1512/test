package com.cannontech.web.bulk;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.callbackResult.BackgroundProcessTypeEnum;
import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.bulk.processor.ProcessingException;
import com.cannontech.common.bulk.processor.SingleProcessor;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.definition.model.PointTemplate;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.web.bulk.model.PaoTypeMasks;
import com.cannontech.web.common.flashScope.FlashScope;
import com.google.common.collect.HashMultimap;

public class RemovePointsController extends AddRemovePointsControllerBase {

	private Logger log = YukonLogManager.getLogger(AddPointsController.class);
	
	// HOME
    public ModelAndView home(HttpServletRequest request, HttpServletResponse response) throws Exception, ServletException {
        
        ModelAndView mav = new ModelAndView("removePoints/removePointsHome.jsp");
        
        // device collection
        DeviceCollection deviceCollection = this.deviceCollectionFactory.createDeviceCollection(request);
        mav.addObject("deviceCollection", deviceCollection);
        
        // options
        boolean sharedPoints = ServletRequestUtils.getBooleanParameter(request, "sharedPoints", true);
        boolean maskMissingPoints = ServletRequestUtils.getBooleanParameter(request, "maskMissingPoints", true);
        mav.addObject("sharedPoints", sharedPoints);
        mav.addObject("maskMissingPoints", maskMissingPoints);
        
        String errorMsg = ServletRequestUtils.getStringParameter(request, "errorMsg");
        if(StringUtils.isNotBlank(errorMsg)){
        	FlashScope flashScope = new FlashScope(request);
        	flashScope.setError(new YukonMessageSourceResolvable("yukon.common.device.bulk.removePointsHome."+ errorMsg));
        }
        
        // device types set
        Set<PaoType> deviceTypeSet = getDeviceTypesSet(deviceCollection);
        
        Map<PaoType, DeviceCollection> deviceTypeDeviceCollectionMap = getDeviceTypeDeviceCollectionMap(deviceTypeSet, deviceCollection);
        mav.addObject("deviceTypeDeviceCollectionMap", deviceTypeDeviceCollectionMap);
        
        // device type points map
        List<PaoTypeMasks> paoTypeMasksList = createExistsPointsMap(deviceTypeSet, maskMissingPoints, false, deviceCollection);
        mav.addObject("paoTypeMasksList", paoTypeMasksList);
        
        // shared points map
        Map<PointTemplate, Boolean> sharedPointTemplateMaskMap = createSharedPointsTemplateMap(paoTypeMasksList);

        PaoTypeMasks sharedPaoTypeMasks = new PaoTypeMasks();
        sharedPaoTypeMasks.setPointTemplateMaskMap(sharedPointTemplateMaskMap);
        mav.addObject("sharedPaoTypeMasks", sharedPaoTypeMasks);
        
        return mav;
    }
    
    @Override
    /**
     * @param maskIfExistOnAllDevices
     * @param paoTypeToSimpleDeviceMultiMap
     * @param paoType
     * @param pointTemplateMaskMap
     */
    protected void fillInPointTemplateMask(boolean maskIfExistOnAllDevices,
                                           HashMultimap<PaoType, SimpleDevice> paoTypeToSimpleDeviceMultiMap,
                                           PaoType paoType,
                                           Map<PointTemplate, Boolean> pointTemplateMaskMap) {
        
        // loop over each possible point for this device type
        for (PointTemplate pointTemplate : pointTemplateMaskMap.keySet()) {
            
            // check each device of this type and see if it has the point or not
            boolean noneHavePoint = true;
            for (SimpleDevice device : paoTypeToSimpleDeviceMultiMap.get(paoType)) {
                boolean pointExistsForDevice = 
                    pointService.pointExistsForPao(device, pointTemplate.getPointIdentifier());
                if (pointExistsForDevice) {
                    noneHavePoint = false;
                    break;
                }
            }
            
            // after looking at all the device of this type, did we find at least one that had it?
            if (noneHavePoint) {
                pointTemplateMaskMap.put(pointTemplate, true);
            }
        }
    }
    
    
    // EXECUTE REMOVE
    public ModelAndView execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, Exception {
    	
    	ModelAndView mav = new ModelAndView("redirect:removePointsResults");
        
    	// device collection
        DeviceCollection deviceCollection = this.deviceCollectionFactory.createDeviceCollection(request);
        
    	// options
        boolean sharedPoints = ServletRequestUtils.getRequiredBooleanParameter(request, "sharedPoints");
        boolean maskMissingPoints = ServletRequestUtils.getRequiredBooleanParameter(request, "maskMissingPoints");
        mav.addObject("sharedPoints", sharedPoints);
        mav.addObject("maskMissingPoints", maskMissingPoints);
        
        // maskExisting POINTS REDIRECT
    	String maskMissingPointsSubmitButton = ServletRequestUtils.getStringParameter(request, "maskMissingPointsSubmitButton");
    	if (maskMissingPointsSubmitButton != null) {
    		
    		mav.setViewName("redirect:home");
    		mav.addAllObjects(deviceCollection.getCollectionParameters());
            mav.addObject("maskMissingPoints", !maskMissingPoints); // toggle it!
            return mav;
    	}
    	
    	// create processor
    	Map<PaoType, Set<PointTemplate>> pointTemplatesMap = extractPointTemplatesMapFromParameters(request, deviceCollection, sharedPoints);
    	SingleProcessor<YukonDevice> addPointsProcessor = getRemovePointsProcessor(pointTemplatesMap);
    	
    	if(pointTemplatesMap.isEmpty()){
            String noPointsSuppliedMsg = "noPointsSuppliedMsg";
            ModelAndView home = redirectWithError(noPointsSuppliedMsg, deviceCollection);
            return home;
        }
    	
    	// start processor
    	String id = startBulkProcessor(deviceCollection, addPointsProcessor, BackgroundProcessTypeEnum.REMOVE_POINTS);
    	
    	// redirect to results page
        mav.addObject("resultsId", id);
        return mav;
    }
    
    // remove points processor
    private SingleProcessor<YukonDevice> getRemovePointsProcessor(final Map<PaoType, Set<PointTemplate>> pointTemplatesMap) {
    	
    	SingleProcessor<YukonDevice> removePointsProcessor = new SingleProcessor<YukonDevice>() {

            @Override
            public void process(YukonDevice device) throws ProcessingException {
            	
            	PaoType paoType = device.getPaoIdentifier().getPaoType();
            	if (pointTemplatesMap.containsKey(paoType)) {
	            	Set<PointTemplate> pointSet = pointTemplatesMap.get(paoType);
					for (PointTemplate pointTemplate : pointSet) {
						
						boolean pointExistsForDevice = pointService.pointExistsForPao(device, pointTemplate.getPointIdentifier());
						if (pointExistsForDevice) {
							
							LitePoint liteDeletePoint = pointService.getPointForPao(device, pointTemplate.getPointIdentifier());
				            PointBase deletePoint = (PointBase)LiteFactory.convertLiteToDBPers(liteDeletePoint);
				            
				            log.debug("Removing point from device: deletePointId=" + liteDeletePoint.getLiteID() + " point=" + pointTemplate + " deviceId=" + device.getPaoIdentifier().getPaoId());
				            dbPersistentDao.performDBChange(deletePoint, Transaction.DELETE);
							
						} else {
							log.debug("Point does not exist for device, not removing: point=" + pointTemplate + " deviceId=" + device.getPaoIdentifier().getPaoId());
						}
					}
            	} else {
            		log.debug("Not points selected for device type, none removed: deviceId=" + device.getPaoIdentifier().getPaoId());
            	}
            }
        };
        
        return removePointsProcessor;
    }
    
    
    // VIEW RESULTS
    public ModelAndView removePointsResults(HttpServletRequest request, HttpServletResponse response) throws ServletException {

        ModelAndView mav = new ModelAndView("removePoints/removePointsResults.jsp");
        
        // prepare mav with basic results data
        prepResultsView(request, mav);
        
        // options
        boolean sharedPoints = ServletRequestUtils.getBooleanParameter(request, "sharedPoints", true);
        boolean maskMissingPoints = ServletRequestUtils.getBooleanParameter(request, "maskMissingPoints", false);
        mav.addObject("sharedPoints", sharedPoints);
        mav.addObject("maskMissingPoints", maskMissingPoints);
        
        return mav;
    }
}
