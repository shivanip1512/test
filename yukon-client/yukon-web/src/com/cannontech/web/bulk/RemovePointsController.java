package com.cannontech.web.bulk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.callbackResult.BackgroundProcessTypeEnum;
import com.cannontech.common.bulk.collection.DeviceCollection;
import com.cannontech.common.bulk.processor.ProcessingException;
import com.cannontech.common.bulk.processor.SingleProcessor;
import com.cannontech.common.device.DeviceType;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.definition.model.DeviceDefinition;
import com.cannontech.common.device.definition.model.PointTemplate;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.PointBase;

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
        boolean maskMissingPoints = ServletRequestUtils.getBooleanParameter(request, "maskMissingPoints", false);
        mav.addObject("sharedPoints", sharedPoints);
        mav.addObject("maskMissingPoints", maskMissingPoints);
        
        // device types set
        Set<Integer> deviceTypeSet = getDeviceTypesSet(deviceCollection);
        
        // device type names map
        Map<Integer, String> deviceTypeNamesMap = getDeviceTypeNamesMap(deviceTypeSet);
        mav.addObject("deviceTypeNamesMap", deviceTypeNamesMap);
        
        Map<Integer, DeviceType> deviceTypeEnumMap = getDeviceTypeEnumMap(deviceTypeSet);
        mav.addObject("deviceTypeEnumMap", deviceTypeEnumMap);
        
        Map<Integer, DeviceCollection> deviceTypeDeviceCollectionMap = getDeviceTypeDeviceCollectionMap(deviceTypeSet, deviceCollection);
        mav.addObject("deviceTypeDeviceCollectionMap", deviceTypeDeviceCollectionMap);
        
        // device type points map
        Map<Integer, Map<String, List<PointTemplateWrapper>>> pointsMap = createRemovePointsMap(deviceTypeSet, maskMissingPoints, deviceCollection);
        mav.addObject("pointsMap", pointsMap);
        
        // shared points map
        Map<String, List<PointTemplateWrapper>> sharedPointsTypeMap = createSharedPointsTypeMapWithPointsMap(pointsMap);
        mav.addObject("sharedPointsTypeMap", sharedPointsTypeMap);
        
        return mav;
    }
    
    // points map helper
    private Map<Integer, Map<String, List<PointTemplateWrapper>>> createRemovePointsMap(Set<Integer> deviceTypeSet, boolean maskMissingPoints, DeviceCollection deviceCollection) {
    	
    	/// make a copy of device list if we'll be doing maskExistingPoints
    	// being able to remove devices from the list as we process each device type will speed up the next iteration building of the devicesOfTypeList
    	List<YukonDevice> mutableDeviceList = null;
    	if (maskMissingPoints) {
    		mutableDeviceList = new ArrayList<YukonDevice>(deviceCollection.getDeviceList());
    	}
    	
    	Map<Integer, Map<String, List<PointTemplateWrapper>>> pointsMap = new LinkedHashMap<Integer, Map<String, List<PointTemplateWrapper>>>();
        for (int deviceType : deviceTypeSet) {
        	
        	// all defined point templates for device type, convert to wrappers that are all initially unmasked
        	DeviceDefinition deviceDefiniton = deviceDefinitionDao.getDeviceDefinition(DeviceType.getForId(deviceType));
        	Set<PointTemplateWrapper> allPointTemplates = convertToPointTemplateWrapperSet(deviceDefinitionDao.getAllPointTemplates(deviceDefiniton), false);
        	
        	// mask those device type points none of the devices of this type have the point
        	if (maskMissingPoints) {
        		
        		Set<PointTemplateWrapper> maskedPointTemplates = new HashSet<PointTemplateWrapper>();
        		
        		// first pull out all the device from the collection that match this device type
        		List<YukonDevice> devicesOfTypeList = new ArrayList<YukonDevice>();
        		for (YukonDevice device : mutableDeviceList) {
        			if (device.getType() == deviceType) {
        				devicesOfTypeList.add(device);
        			}
        		}
        		mutableDeviceList.removeAll(devicesOfTypeList);
        		
        		// loop over each possible point for this device type
        		for (PointTemplateWrapper pointTemplateWrapper : allPointTemplates) {
        			
        			// check each device of this type and see if it has the point or not
        			boolean noneHavePoint = true;
        			for (YukonDevice device : devicesOfTypeList) {
        				boolean pointExistsForDevice = pointService.pointExistsForDevice(device, pointTemplateWrapper.getPointTemplate().getPointIdentifier());
        				if (pointExistsForDevice) {
        					noneHavePoint = false;
        					break;
        				}
        			}
        			
        			// after looking at all the device of this type, did we find at least one that had it?
        			if (noneHavePoint) {
        				pointTemplateWrapper.setMasked(true);
        			}
        			
        			maskedPointTemplates.add(pointTemplateWrapper);
        		}
        		
        		allPointTemplates = maskedPointTemplates;
        	}
        	
        	// sort points list
        	List<PointTemplateWrapper> pointList = new ArrayList<PointTemplateWrapper>(allPointTemplates);
        	Collections.sort(pointList, pointTemplateOffsetCompartor);
        	
        	// make point type map of points list
        	Map<String, List<PointTemplateWrapper>> pointTypeMap = createPointTypeMap(pointList);
        	
        	// add to master device type map
        	pointsMap.put(deviceType, pointTypeMap);
        }
    	
    	return pointsMap;
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
    	Map<Integer, Set<PointTemplate>> pointTemplatesMap = extractPointTemplatesMapFromParameters(request, deviceCollection, sharedPoints);
    	SingleProcessor<YukonDevice> addPointsProcessor = getRemovePointsProcessor(pointTemplatesMap);
    	
    	// start processor
    	String id = startBulkProcessor(deviceCollection, addPointsProcessor, BackgroundProcessTypeEnum.REMOVE_POINTS);
    	
    	// redirect to results page
        mav.addObject("resultsId", id);
        return mav;
    }
    
    // remove points processor
    private SingleProcessor<YukonDevice> getRemovePointsProcessor(final Map<Integer, Set<PointTemplate>> pointTemplatesMap) {
    	
    	SingleProcessor<YukonDevice> removePointsProcessor = new SingleProcessor<YukonDevice>() {

            @Override
            public void process(YukonDevice device) throws ProcessingException {
            	
            	int deviceType = device.getType();
            	if (pointTemplatesMap.containsKey(deviceType)) {
	            	Set<PointTemplate> pointSet = pointTemplatesMap.get(deviceType);
					for (PointTemplate pointTemplate : pointSet) {
						
						boolean pointExistsForDevice = pointService.pointExistsForDevice(device, pointTemplate.getPointIdentifier());
						if (pointExistsForDevice) {
							
							LitePoint liteDeletePoint = pointService.getPointForDevice(device, pointTemplate.getPointIdentifier());
				            PointBase deletePoint = (PointBase)LiteFactory.convertLiteToDBPers(liteDeletePoint);
				            
				            log.debug("Removing point from device: deletePointId=" + liteDeletePoint.getLiteID() + " point=" + pointTemplate + " deviceId=" + device.getDeviceId());
				            dbPersistentDao.performDBChange(deletePoint, Transaction.DELETE);
							
						} else {
							log.debug("Point does not exist for device, not removing: point=" + pointTemplate + " deviceId=" + device.getDeviceId());
						}
					}
            	} else {
            		log.debug("Not points selected for device type, none removed: deviceId=" + device.getDeviceId());
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
