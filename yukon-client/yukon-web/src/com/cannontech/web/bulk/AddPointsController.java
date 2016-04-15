package com.cannontech.web.bulk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.callbackResult.BackgroundProcessTypeEnum;
import com.cannontech.common.bulk.collection.device.DeviceCollectionFactory;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.processor.ProcessingException;
import com.cannontech.common.bulk.processor.SingleProcessor;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.definition.model.PointTemplate;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PersistenceException;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.AccumulatorPoint;
import com.cannontech.database.data.point.AnalogPoint;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.StatusPoint;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.web.bulk.model.PaoTypeMasks;
import com.cannontech.web.common.flashScope.FlashScope;

@Controller
@RequestMapping("addPoints/*")
public class AddPointsController extends AddRemovePointsControllerBase {
	public static String VARIABLE_PRESELECTED_POINT_ARRAY = "preselectedPointIdentifiers";
	
	private static final Logger log = YukonLogManager.getLogger(AddPointsController.class);
	@Autowired private DeviceCollectionFactory deviceCollectionFactory;

	// HOME
	@RequestMapping("home")
    public String home(ModelMap model, HttpServletRequest request) throws Exception, ServletException {
        
        // device collection
        DeviceCollection deviceCollection = deviceCollectionFactory.createDeviceCollection(request);
        model.addAttribute("deviceCollection", deviceCollection);
        
        // PointIdentifiers
        final String identifyingPointData   = ServletRequestUtils.getStringParameter(request, "pointTypeOffsets");
        final String pointTypeOffsets[]     = StringUtils.isBlank(identifyingPointData) ? null : identifyingPointData.split(",");
        final List<String> ptos             = pointTypeOffsets == null ? new ArrayList<String>() : Arrays.asList(pointTypeOffsets);
        model.addAttribute(VARIABLE_PRESELECTED_POINT_ARRAY, ptos);

        // options
        boolean sharedPoints = ServletRequestUtils.getBooleanParameter(request, "sharedPoints", true);
        boolean updatePoints = ServletRequestUtils.getBooleanParameter(request, "updatePoints", false);
        boolean maskExistingPoints = ServletRequestUtils.getBooleanParameter(request, "maskExistingPoints", true);
        model.addAttribute("sharedPoints", sharedPoints);
        model.addAttribute("updatePoints", updatePoints);
        model.addAttribute("maskExistingPoints", maskExistingPoints);
        
        String errorMsg = ServletRequestUtils.getStringParameter(request, "errorMsg");
        if(StringUtils.isNotBlank(errorMsg)){
        	FlashScope flashScope = new FlashScope(request);
        	flashScope.setError(new YukonMessageSourceResolvable("yukon.common.device.bulk.addPointsHome."+errorMsg));
        }
        
        // device types set
        Set<PaoType> deviceTypeSet = getDeviceTypesSet(deviceCollection);
        model.addAttribute("deviceTypeEnumSet", deviceTypeSet);
        
        Map<PaoType, DeviceCollection> deviceTypeDeviceCollectionMap = getDeviceTypeDeviceCollectionMap(deviceTypeSet, deviceCollection);
        model.addAttribute("deviceTypeDeviceCollectionMap", deviceTypeDeviceCollectionMap);

        // device type points map
        List<PaoTypeMasks> paoTypeMasksList = createExistsPointsMap(deviceTypeSet, maskExistingPoints, true, deviceCollection);
        model.addAttribute("paoTypeMasksList", paoTypeMasksList);
        
        // shared points map
        Map<PointTemplate, Boolean> sharedPointTemplateMaskMap = createSharedPointsTemplateMap(paoTypeMasksList);
        
        PaoTypeMasks sharedPaoTypeMasks = new PaoTypeMasks();
        sharedPaoTypeMasks.setPointTemplateMaskMap(sharedPointTemplateMaskMap);
        model.addAttribute("sharedPaoTypeMasks", sharedPaoTypeMasks);
        
        return "addPoints/addPointsHome.jsp";
    }
    
    // EXECUTE ADD
    @RequestMapping("execute")
    public String execute(ModelMap model, HttpServletRequest request) throws ServletException, Exception {
    	
    	// device collection
        DeviceCollection deviceCollection = deviceCollectionFactory.createDeviceCollection(request);
        
    	// options
        boolean sharedPoints = ServletRequestUtils.getRequiredBooleanParameter(request, "sharedPoints");
        final boolean updatePoints = ServletRequestUtils.getBooleanParameter(request, "updatePoints", false);
        boolean maskExistingPoints = ServletRequestUtils.getRequiredBooleanParameter(request, "maskExistingPoints");
        model.addAttribute("sharedPoints", sharedPoints);
        model.addAttribute("updatePoints", updatePoints);
        model.addAttribute("maskExistingPoints", maskExistingPoints);
        
        // mask existing points redirect
    	String maskExistingPointsSubmitButton = ServletRequestUtils.getStringParameter(request, "maskExistingPointsSubmitButton");
    	if (maskExistingPointsSubmitButton != null) {
    		
    		model.addAllAttributes(deviceCollection.getCollectionParameters());
    		model.addAttribute("maskExistingPoints", !maskExistingPoints); // toggle it!
            return "redirect:home";
    	}
    	
    	// Check to see if points were supplied and create processor
    	Map<PaoType, Set<PointTemplate>> pointTemplatesMap = extractPointTemplatesMapFromParameters(request, deviceCollection, sharedPoints);
    	SingleProcessor<YukonDevice> addPointsProcessor = getAddPointsProcessor(pointTemplatesMap, updatePoints);

    	if (pointTemplatesMap.isEmpty()) {
            return redirectWithError(model, "noPointsSuppliedMsg", deviceCollection);
    	}
    	
    	// start processor
    	String id = startBulkProcessor(deviceCollection, addPointsProcessor, BackgroundProcessTypeEnum.ADD_POINTS);
    	
    	// redirect to results page
    	model.addAttribute("resultsId", id);
    	return "redirect:addPointsResults";
    }
    
    // add points processor
    private SingleProcessor<YukonDevice> getAddPointsProcessor(final Map<PaoType, Set<PointTemplate>> pointTemplatesMap, final boolean updatePoints) {
    	
    	SingleProcessor<YukonDevice> addPointsProcessor = new SingleProcessor<YukonDevice>() {

            @Override
            public void process(YukonDevice device) throws ProcessingException {
            	
            	PaoType paoType = device.getPaoIdentifier().getPaoType();
            	try {
                    if (pointTemplatesMap.containsKey(paoType)) {
                    	Set<PointTemplate> pointSet = pointTemplatesMap.get(paoType);
                    	for (PointTemplate pointTemplate : pointSet) {
                    		
                    		boolean pointExistsForDevice = pointService.pointExistsForPao(device, pointTemplate.getPointIdentifier());
                    		
                    		/* Check to see if the point name is in use by a point with a different offset */
                    		if (!pointExistsForDevice && pointDao.findPointByName(device, pointTemplate.getName()) != null) {
                    		    String errorMessage = "Point with name (" + pointTemplate.getName() + ") already exists for device (" + paoDao.getYukonPAOName(device.getPaoIdentifier().getPaoId()) + ")";
                    		    log.debug(errorMessage);
                    		    throw new ProcessingException(errorMessage, "pointAlreadyExists", pointTemplate.getName(), paoDao.getYukonPAOName(device.getPaoIdentifier().getPaoId()));
                    		}
                    		
                    		// add new point
                    		if (!pointExistsForDevice) {
                    			
                    			// add new
                    			PointBase newPoint = pointCreationService.createPoint(device.getPaoIdentifier(), pointTemplate);
                    			dbPersistentDao.performDBChange(newPoint, TransactionType.INSERT);
                    			
                    			log.debug("Added point to device: deviceId=" + device.getPaoIdentifier().getPaoId() + " pointTemplate=" + pointTemplate);
                    			
                    		} else {
                    			
                    			// update point
                    			if (updatePoints) {
                    				
                    				log.debug("Point already exists for device, updatePoints=true, will attempt update: deviceId=" + device.getPaoIdentifier().getPaoId() + " pointTemplate=" + pointTemplate);
                    				
                    				LitePoint litePoint = pointService.getPointForPao(device, pointTemplate.getPointIdentifier());
                    	            
                    	            PointBase pointBase = (PointBase)dbPersistentDao.retrieveDBPersistent(litePoint);
                    	            
                    	            if (pointBase instanceof AnalogPoint) {
                    	            	
                    	            	AnalogPoint analogPoint = (AnalogPoint)pointBase;
                    	            	analogPoint.getPointAnalog().setMultiplier(pointTemplate.getMultiplier());
                    	            	analogPoint.getPointUnit().setUomID(pointTemplate.getUnitOfMeasure());
                    	            	analogPoint.getPoint().setStateGroupID(pointTemplate.getStateGroupId());
                    	            	dbPersistentDao.performDBChange(analogPoint, TransactionType.UPDATE);
                    	            	
                    	            } else if (pointBase instanceof StatusPoint) {
                    	            	
                    	            	StatusPoint statusPoint = (StatusPoint)pointBase;
                    	            	statusPoint.getPoint().setStateGroupID(pointTemplate.getStateGroupId());
                    	            	dbPersistentDao.performDBChange(statusPoint, TransactionType.UPDATE);
                    	            	
                    	            } else if (pointBase instanceof AccumulatorPoint) {
                    	            	
                    	            	AccumulatorPoint accumulatorPoint = (AccumulatorPoint)pointBase;
                    	            	accumulatorPoint.getPointAccumulator().setMultiplier(pointTemplate.getMultiplier());
                    	            	accumulatorPoint.getPointUnit().setUomID(pointTemplate.getUnitOfMeasure());
                    	            	accumulatorPoint.getPoint().setStateGroupID(pointTemplate.getStateGroupId());
                    	            	dbPersistentDao.performDBChange(accumulatorPoint, TransactionType.UPDATE);
                    	            	
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
                } catch (PersistenceException e) {
                    throw new ProcessingException("Add point failed", "addPointFailed", e );
                } catch (NotFoundException e) {
                    throw new ProcessingException("Add point failed", "addPointFailed", e);
                }
            }
        };
        
        return addPointsProcessor;
    }
    
    // VIEW RESULTS
    @RequestMapping("addPointsResults")
    public String addPointsResults(ModelMap model, HttpServletRequest request) throws ServletException {

        // prepare mav with basic results data
        prepResultsView(model, request);
        
        // options
        boolean sharedPoints = ServletRequestUtils.getBooleanParameter(request, "sharedPoints", true);
        boolean updatePoints = ServletRequestUtils.getBooleanParameter(request, "updatePoints", false);
        boolean maskExistingPoints = ServletRequestUtils.getBooleanParameter(request, "maskExistingPoints", false);
        model.addAttribute("sharedPoints", sharedPoints);
        model.addAttribute("updatePoints", updatePoints);
        model.addAttribute("maskExistingPoints", maskExistingPoints);
        
        return "addPoints/addPointsResults.jsp";
    }
    
}