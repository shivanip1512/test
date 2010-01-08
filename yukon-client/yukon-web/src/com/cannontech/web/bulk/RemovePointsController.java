package com.cannontech.web.bulk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
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
import com.cannontech.common.bulk.collection.DeviceCollection;
import com.cannontech.common.bulk.processor.ProcessingException;
import com.cannontech.common.bulk.processor.SingleProcessor;
import com.cannontech.common.device.definition.model.DeviceDefinition;
import com.cannontech.common.device.definition.model.PointTemplate;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

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
        
        String errorMsg = ServletRequestUtils.getStringParameter(request, "errorMsg");
        if(StringUtils.isNotBlank(errorMsg)){
            mav.addObject("errorMsg", errorMsg);
        }
        
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
        Map<Integer, Map<String, List<PointTemplateWrapper>>> pointsMap = createRemovePointsMap(deviceTypeSet, maskMissingPoints, deviceCollection);
        mav.addObject("pointsMap", pointsMap);
        
        // shared points map
        Map<String, List<PointTemplateWrapper>> sharedPointsTypeMap = createSharedPointsTypeMapWithPointsMap(pointsMap);
        mav.addObject("sharedPointsTypeMap", sharedPointsTypeMap);
        
        return mav;
    }
    
    @Override
    protected Map<String, List<PointTemplateWrapper>> createSharedPointsTypeMapWithPointsMap(Map<Integer, Map<String, List<PointTemplateWrapper>>> pointsMap) {
        
        // complete set of all point templates
        Set<PointTemplateWrapper> allPointTemplates = new HashSet<PointTemplateWrapper>();
        
        // list of sets of point templates, one for each device type
        List<Set<PointTemplateWrapper>> devicePointTemplateSetsList = new ArrayList<Set<PointTemplateWrapper>>();
        
        for (int deviceType : pointsMap.keySet()) {
            
            Set<PointTemplateWrapper> deviceTypePointSet = new HashSet<PointTemplateWrapper>();
            
            for (String pointTypeName : pointsMap.get(deviceType).keySet()) {
                
                List<PointTemplateWrapper> pointTypePointList = pointsMap.get(deviceType).get(pointTypeName);
                deviceTypePointSet.addAll(pointTypePointList);

                // Creates a set that handles the shared points and 
                // also handles the masking for that set.
                for (PointTemplateWrapper pointTemplateWrapper : pointTypePointList) {
                    if (allPointTemplates.contains(pointTemplateWrapper)) {
                        if (pointTemplateWrapper.isMasked() == false) {
                            allPointTemplates.remove(pointTemplateWrapper);
                            allPointTemplates.add(pointTemplateWrapper);
                        }
                    } else {
                        allPointTemplates.add(pointTemplateWrapper);
                    }
                }
            }
            
            devicePointTemplateSetsList.add(deviceTypePointSet);
        }
        
        
        // reduce the "all" set by retaining each device type point template set
        for (Set<PointTemplateWrapper> deviceTypePointSet : devicePointTemplateSetsList) {
            allPointTemplates.retainAll(deviceTypePointSet);
        }
        
        List<PointTemplateWrapper> pointList = new ArrayList<PointTemplateWrapper>(allPointTemplates);
        Collections.sort(pointList, pointTemplateOffsetCompartor);
        
        return createPointTypeMap(pointList);
    }
    
    // points map helper
    private Map<Integer, Map<String, List<PointTemplateWrapper>>> createRemovePointsMap(Set<Integer> deviceTypeSet, boolean maskMissingPoints, DeviceCollection deviceCollection) {
    	
    	/// make a copy of device list if we'll be doing maskExistingPoints
    	// being able to remove devices from the list as we process each device type will speed up the next iteration building of the devicesOfTypeList
    	List<SimpleDevice> mutableDeviceList = null;
    	if (maskMissingPoints) {
    		mutableDeviceList = new ArrayList<SimpleDevice>(deviceCollection.getDeviceList());
    	}
    	
    	Map<Integer, Map<String, List<PointTemplateWrapper>>> pointsMap = Maps.newLinkedHashMap();
        for (int deviceType : deviceTypeSet) {
        	
        	// all defined point templates for device type, convert to wrappers that are all initially unmasked
        	DeviceDefinition deviceDefiniton = deviceDefinitionDao.getDeviceDefinition(PaoType.getForId(deviceType));
        	Set<PointTemplateWrapper> allPointTemplates = convertToPointTemplateWrapperSet(deviceDefinitionDao.getAllPointTemplates(deviceDefiniton), false);
        	
        	// mask those device type points none of the devices of this type have the point
        	if (maskMissingPoints) {
        		
        		Set<PointTemplateWrapper> maskedPointTemplates = Sets.newHashSet();
        		
        		// first pull out all the device from the collection that match this device type
        		List<SimpleDevice> devicesOfTypeList = new ArrayList<SimpleDevice>();
        		for (SimpleDevice device : mutableDeviceList) {
        			if (device.getType() == deviceType) {
        				devicesOfTypeList.add(device);
        			}
        		}
        		mutableDeviceList.removeAll(devicesOfTypeList);
        		
        		// loop over each possible point for this device type
        		for (PointTemplateWrapper pointTemplateWrapper : allPointTemplates) {
        			
        			// check each device of this type and see if it has the point or not
        			boolean noneHavePoint = true;
        			for (SimpleDevice device : devicesOfTypeList) {
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
    	YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        
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
    	
    	if(pointTemplatesMap.isEmpty()){
            MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
            String noPointsSuppliedMsg = messageSourceAccessor.getMessage("yukon.common.device.bulk.updatePointsHome.noPointsSuppliedMsg");
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
    private SingleProcessor<YukonDevice> getRemovePointsProcessor(final Map<Integer, Set<PointTemplate>> pointTemplatesMap) {
    	
    	SingleProcessor<YukonDevice> removePointsProcessor = new SingleProcessor<YukonDevice>() {

            @Override
            public void process(YukonDevice device) throws ProcessingException {
            	
            	int deviceType = device.getPaoIdentifier().getPaoType().getDeviceTypeId();
            	if (pointTemplatesMap.containsKey(deviceType)) {
	            	Set<PointTemplate> pointSet = pointTemplatesMap.get(deviceType);
					for (PointTemplate pointTemplate : pointSet) {
						
						boolean pointExistsForDevice = pointService.pointExistsForDevice(device, pointTemplate.getPointIdentifier());
						if (pointExistsForDevice) {
							
							LitePoint liteDeletePoint = pointService.getPointForDevice(device, pointTemplate.getPointIdentifier());
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
