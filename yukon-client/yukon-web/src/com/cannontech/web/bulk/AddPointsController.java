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
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.AccumulatorPoint;
import com.cannontech.database.data.point.AnalogPoint;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.StatusPoint;

public class AddPointsController extends AddRemovePointsControllerBase {
	
	private Logger log = YukonLogManager.getLogger(AddPointsController.class);

	// HOME
    public ModelAndView home(HttpServletRequest request, HttpServletResponse response) throws Exception, ServletException {
        
        ModelAndView mav = new ModelAndView("addPoints/addPointsHome.jsp");
        
        // device collection
        DeviceCollection deviceCollection = this.deviceCollectionFactory.createDeviceCollection(request);
        mav.addObject("deviceCollection", deviceCollection);
        
        // options
        boolean sharedPoints = ServletRequestUtils.getBooleanParameter(request, "sharedPoints", true);
        boolean updatePoints = ServletRequestUtils.getBooleanParameter(request, "updatePoints", false);
        boolean maskExistingPoints = ServletRequestUtils.getBooleanParameter(request, "maskExistingPoints", false);
        mav.addObject("sharedPoints", sharedPoints);
        mav.addObject("updatePoints", updatePoints);
        mav.addObject("maskExistingPoints", maskExistingPoints);
        
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
        Map<Integer, Map<String, List<PointTemplateWrapper>>> pointsMap = createAddPointsMap(deviceTypeSet, maskExistingPoints, deviceCollection);
        mav.addObject("pointsMap", pointsMap);
        
        // shared points map
        Map<String, List<PointTemplateWrapper>> sharedPointsTypeMap = createSharedPointsTypeMapWithPointsMap(pointsMap);
        mav.addObject("sharedPointsTypeMap", sharedPointsTypeMap);
        
        return mav;
    }
    
    // points map helper
    private Map<Integer, Map<String, List<PointTemplateWrapper>>> createAddPointsMap(Set<Integer> deviceTypeSet, boolean maskExistingPoints, DeviceCollection deviceCollection) {
    	
    	/// make a copy of device list if we'll be doing maskExistingPoints
    	// being able to remove devices from the list as we process each device type will speed up the next iteration building of the devicesOfTypeList
    	List<YukonDevice> mutableDeviceList = null;
    	if (maskExistingPoints) {
    		mutableDeviceList = new ArrayList<YukonDevice>(deviceCollection.getDeviceList());
    	}
    	
    	Map<Integer, Map<String, List<PointTemplateWrapper>>> pointsMap = new LinkedHashMap<Integer, Map<String, List<PointTemplateWrapper>>>();
        for (int deviceType : deviceTypeSet) {
        	
        	// all defined point templates for device type, convert to wrappers that are all initially unmasked
        	DeviceDefinition deviceDefiniton = deviceDefinitionDao.getDeviceDefinition(DeviceType.getForId(deviceType));
        	Set<PointTemplateWrapper> allPointTemplates = convertToPointTemplateWrapperSet(deviceDefinitionDao.getAllPointTemplates(deviceDefiniton), false);
        	
        	
        	// mask those device type points where all the the device of this type have the point
        	if (maskExistingPoints) {
        		
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
        			boolean allDevicesHavePoint = true;
        			for (YukonDevice device : devicesOfTypeList) {
        				boolean pointExistsForDevice = pointService.pointExistsForDevice(device, pointTemplateWrapper.getPointTemplate().getPointIdentifier());
        				if (!pointExistsForDevice) {
        					allDevicesHavePoint = false;
        					break;
        				}
        			}
        			
        			// after looking at all the device of this type, did we find that that none of them have it?
        			if (allDevicesHavePoint) {
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
    
    // EXECUTE ADD
    public ModelAndView execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, Exception {
    	
    	ModelAndView mav = new ModelAndView("redirect:addPointsResults");
        
    	// device collection
        DeviceCollection deviceCollection = this.deviceCollectionFactory.createDeviceCollection(request);
        
    	// options
        boolean sharedPoints = ServletRequestUtils.getRequiredBooleanParameter(request, "sharedPoints");
        final boolean updatePoints = ServletRequestUtils.getBooleanParameter(request, "updatePoints", false);
        boolean maskExistingPoints = ServletRequestUtils.getRequiredBooleanParameter(request, "maskExistingPoints");
        mav.addObject("sharedPoints", sharedPoints);
        mav.addObject("updatePoints", updatePoints);
        mav.addObject("maskExistingPoints", maskExistingPoints);
        
        // mask existing points redirect
    	String maskExistingPointsSubmitButton = ServletRequestUtils.getStringParameter(request, "maskExistingPointsSubmitButton");
    	if (maskExistingPointsSubmitButton != null) {
    		
    		mav.setViewName("redirect:home");
    		mav.addAllObjects(deviceCollection.getCollectionParameters());
    		mav.addObject("maskExistingPoints", !maskExistingPoints); // toggle it!
            return mav;
    	}
    	
    	// create processor
    	Map<Integer, Set<PointTemplate>> pointTemplatesMap = extractPointTemplatesMapFromParameters(request, deviceCollection, sharedPoints);
    	SingleProcessor<YukonDevice> addPointsProcessor = getAddPointsProcessor(pointTemplatesMap, updatePoints);
    	
    	// start processor
    	String id = startBulkProcessor(deviceCollection, addPointsProcessor, BackgroundProcessTypeEnum.ADD_POINTS);
    	
    	// redirect to results page
    	mav.addObject("resultsId", id);
        return mav;
    }
    
    // add points processor
    private SingleProcessor<YukonDevice> getAddPointsProcessor(final Map<Integer, Set<PointTemplate>> pointTemplatesMap, final boolean updatePoints) {
    	
    	SingleProcessor<YukonDevice> addPointsProcessor = new SingleProcessor<YukonDevice>() {

            @Override
            public void process(YukonDevice device) throws ProcessingException {
            	
            	int deviceType = device.getType();
            	if (pointTemplatesMap.containsKey(deviceType)) {
	            	Set<PointTemplate> pointSet = pointTemplatesMap.get(deviceType);
					for (PointTemplate pointTemplate : pointSet) {
						
						boolean pointExistsForDevice = pointService.pointExistsForDevice(device, pointTemplate.getPointIdentifier());
						
						// add new point
						if (!pointExistsForDevice) {
							
							// add new
							PointBase newPoint = pointService.createPoint(device.getPaoId(), pointTemplate);
							dbPersistentDao.performDBChange(newPoint, Transaction.INSERT);
							
							log.debug("Added point to device: deviceId=" + device.getDeviceId() + " pointTemplate=" + pointTemplate);
							
						} else {
							
							// update point
							if (updatePoints) {
								
								log.debug("Point already exists for device, updatePoints=true, will attempt update: deviceId=" + device.getDeviceId() + " pointTemplate=" + pointTemplate);
								
								LitePoint litePoint = pointService.getPointForDevice(device, pointTemplate.getPointIdentifier());
					            //PointBase pointBase = (PointBase)LiteFactory.convertLiteToDBPers(litePoint);
					            
					            PointBase pointBase = (PointBase)dbPersistentDao.retrieveDBPersistent(litePoint);
					            
					            if (pointBase instanceof AnalogPoint) {
					            	
					            	AnalogPoint analogPoint = (AnalogPoint)pointBase;
					            	analogPoint.getPointAnalog().setMultiplier(pointTemplate.getMultiplier());
					            	analogPoint.getPointUnit().setUomID(pointTemplate.getUnitOfMeasure());
					            	analogPoint.getPoint().setStateGroupID(pointTemplate.getStateGroupId());
					            	dbPersistentDao.performDBChange(analogPoint, Transaction.UPDATE);
					            	
					            } else if (pointBase instanceof StatusPoint) {
					            	
					            	StatusPoint statusPoint = (StatusPoint)pointBase;
					            	statusPoint.getPoint().setStateGroupID(pointTemplate.getStateGroupId());
					            	dbPersistentDao.performDBChange(statusPoint, Transaction.UPDATE);
					            	
					            } else if (pointBase instanceof AccumulatorPoint) {
					            	
					            	AccumulatorPoint accumulatorPoint = (AccumulatorPoint)pointBase;
					            	accumulatorPoint.getPointAccumulator().setMultiplier(pointTemplate.getMultiplier());
					            	accumulatorPoint.getPointUnit().setUomID(pointTemplate.getUnitOfMeasure());
					            	accumulatorPoint.getPoint().setStateGroupID(pointTemplate.getStateGroupId());
					            	dbPersistentDao.performDBChange(accumulatorPoint, Transaction.UPDATE);
					            	
					            } else {
					            	
					            	log.debug("Point type not supported, not updating: deviceId=" + device.getDeviceId() + " pointId=" + litePoint.getLiteID() + " pointType=" + litePoint.getLiteType());
					            }
					            
					            log.debug("Updated point for device: deviceId=" + device.getDeviceId() + " point=" + pointTemplate);
					            
							} else {
								
								log.debug("Point already exists for device and updatePoints=false, not updating: deviceId=" + device.getDeviceId() + " point=" + pointTemplate + " deviceId=" + device.getDeviceId());
							}
						}
					}
					
            	} else {
            		
            		log.debug("No points selected for device type, none added or updated: deviceId=" + device.getDeviceId());
            	}
            }
        };
        
        return addPointsProcessor;
    }
    
    
    // VIEW RESULTS
    public ModelAndView addPointsResults(HttpServletRequest request, HttpServletResponse response) throws ServletException {

        ModelAndView mav = new ModelAndView("addPoints/addPointsResults.jsp");
        
        // prepare mav with basic results data
        prepResultsView(request, mav);
        
        // options
        boolean sharedPoints = ServletRequestUtils.getBooleanParameter(request, "sharedPoints", true);
        boolean updatePoints = ServletRequestUtils.getBooleanParameter(request, "updatePoints", false);
        boolean maskExistingPoints = ServletRequestUtils.getBooleanParameter(request, "maskExistingPoints", false);
        mav.addObject("sharedPoints", sharedPoints);
        mav.addObject("updatePoints", updatePoints);
        mav.addObject("maskExistingPoints", maskExistingPoints);
        
        return mav;
    }
}
