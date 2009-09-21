package com.cannontech.web.bulk;

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
import com.cannontech.common.device.definition.model.PointTemplate;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonDevice;
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
        
        Map<Integer, PaoType> deviceTypeEnumMap = getDeviceTypeEnumMap(deviceTypeSet);
        mav.addObject("deviceTypeEnumMap", deviceTypeEnumMap);
        
        Map<Integer, DeviceCollection> deviceTypeDeviceCollectionMap = getDeviceTypeDeviceCollectionMap(deviceTypeSet, deviceCollection);
        mav.addObject("deviceTypeDeviceCollectionMap", deviceTypeDeviceCollectionMap);

        // device type points map
        Map<Integer, Map<String, List<PointTemplateWrapper>>> pointsMap = createPointsMap(deviceTypeSet, maskExistingPoints, true, deviceCollection);
        mav.addObject("pointsMap", pointsMap);
        
        // shared points map
        Map<String, List<PointTemplateWrapper>> sharedPointsTypeMap = createSharedPointsTypeMapWithPointsMap(pointsMap);
        mav.addObject("sharedPointsTypeMap", sharedPointsTypeMap);
        
        return mav;
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
            	
            	int deviceType = device.getPaoIdentifier().getPaoType().getDeviceTypeId();
            	if (pointTemplatesMap.containsKey(deviceType)) {
	            	Set<PointTemplate> pointSet = pointTemplatesMap.get(deviceType);
					for (PointTemplate pointTemplate : pointSet) {
						
						boolean pointExistsForDevice = pointService.pointExistsForDevice(device, pointTemplate.getPointIdentifier());
						
						// add new point
						if (!pointExistsForDevice) {
							
							// add new
							PointBase newPoint = pointService.createPoint(device.getPaoIdentifier().getPaoId(), pointTemplate);
							dbPersistentDao.performDBChange(newPoint, Transaction.INSERT);
							
							log.debug("Added point to device: deviceId=" + device.getPaoIdentifier().getPaoId() + " pointTemplate=" + pointTemplate);
							
						} else {
							
							// update point
							if (updatePoints) {
								
								log.debug("Point already exists for device, updatePoints=true, will attempt update: deviceId=" + device.getPaoIdentifier().getPaoId() + " pointTemplate=" + pointTemplate);
								
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
					            	
					            	log.debug("Point type not supported, not updating: deviceId=" + device.getPaoIdentifier().getPaoId() + " pointId=" + litePoint.getLiteID() + " pointType=" + litePoint.getLiteType());
					            }
					            
					            log.debug("Updated point for device: deviceId=" + device.getPaoIdentifier().getPaoId() + " point=" + pointTemplate);
					            
							} else {
								
								log.debug("Point already exists for device and updatePoints=false, not updating: deviceId=" + device.getPaoIdentifier().getPaoId() + " point=" + pointTemplate);
							}
						}
					}
					
            	} else {
            		
            		log.debug("No points selected for device type, none added or updated: deviceId=" + device.getPaoIdentifier().getPaoId());
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
