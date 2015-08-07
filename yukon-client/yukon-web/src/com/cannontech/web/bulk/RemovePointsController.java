package com.cannontech.web.bulk;

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

@Controller
@RequestMapping("removePoints/*")
public class RemovePointsController extends AddRemovePointsControllerBase {

	private Logger log = YukonLogManager.getLogger(AddPointsController.class);
	@Autowired DeviceCollectionFactory deviceCollectionFactory;
	
	// HOME
	@Override
	@RequestMapping("home")
    public String home(ModelMap model, HttpServletRequest request) throws Exception, ServletException {
        
        // device collection
        DeviceCollection deviceCollection = deviceCollectionFactory.createDeviceCollection(request);
        model.addAttribute("deviceCollection", deviceCollection);
        
        // options
        boolean sharedPoints = ServletRequestUtils.getBooleanParameter(request, "sharedPoints", true);
        boolean maskMissingPoints = ServletRequestUtils.getBooleanParameter(request, "maskMissingPoints", true);
        model.addAttribute("sharedPoints", sharedPoints);
        model.addAttribute("maskMissingPoints", maskMissingPoints);
        
        String errorMsg = ServletRequestUtils.getStringParameter(request, "errorMsg");
        if(StringUtils.isNotBlank(errorMsg)){
        	FlashScope flashScope = new FlashScope(request);
        	flashScope.setError(new YukonMessageSourceResolvable("yukon.common.device.bulk.removePointsHome."+ errorMsg));
        }
        
        // device types set
        Set<PaoType> deviceTypeSet = getDeviceTypesSet(deviceCollection);
        
        Map<PaoType, DeviceCollection> deviceTypeDeviceCollectionMap = getDeviceTypeDeviceCollectionMap(deviceTypeSet, deviceCollection);
        model.addAttribute("deviceTypeDeviceCollectionMap", deviceTypeDeviceCollectionMap);
        
        // device type points map
        List<PaoTypeMasks> paoTypeMasksList = createExistsPointsMap(deviceTypeSet, maskMissingPoints, false, deviceCollection);
        model.addAttribute("paoTypeMasksList", paoTypeMasksList);
        
        // shared points map
        Map<PointTemplate, Boolean> sharedPointTemplateMaskMap = createSharedPointsTemplateMap(paoTypeMasksList);

        PaoTypeMasks sharedPaoTypeMasks = new PaoTypeMasks();
        sharedPaoTypeMasks.setPointTemplateMaskMap(sharedPointTemplateMaskMap);
        model.addAttribute("sharedPaoTypeMasks", sharedPaoTypeMasks);
        
        return"removePoints/removePointsHome.jsp";
    }
    
    @Override
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
    @Override
    @RequestMapping("execute")
    public String execute(ModelMap model, HttpServletRequest request) throws ServletException, Exception {
    	
    	// device collection
        DeviceCollection deviceCollection = this.deviceCollectionFactory.createDeviceCollection(request);
        
    	// options
        boolean sharedPoints = ServletRequestUtils.getRequiredBooleanParameter(request, "sharedPoints");
        boolean maskMissingPoints = ServletRequestUtils.getRequiredBooleanParameter(request, "maskMissingPoints");
        model.addAttribute("sharedPoints", sharedPoints);
        model.addAttribute("maskMissingPoints", maskMissingPoints);
        
        // maskExisting POINTS REDIRECT
    	String maskMissingPointsSubmitButton = ServletRequestUtils.getStringParameter(request, "maskMissingPointsSubmitButton");
    	if (maskMissingPointsSubmitButton != null) {
    		
    		model.addAllAttributes(deviceCollection.getCollectionParameters());
            model.addAttribute("maskMissingPoints", !maskMissingPoints); // toggle it!
            return "redirect:home";
    	}
    	
    	// create processor
    	Map<PaoType, Set<PointTemplate>> pointTemplatesMap = extractPointTemplatesMapFromParameters(request, deviceCollection, sharedPoints);
    	SingleProcessor<YukonDevice> addPointsProcessor = getRemovePointsProcessor(pointTemplatesMap);
    	
    	if(pointTemplatesMap.isEmpty()){
            String noPointsSuppliedMsg = "noPointsSuppliedMsg";
            return redirectWithError(model, noPointsSuppliedMsg, deviceCollection);
        }
    	
    	// start processor
    	String id = startBulkProcessor(deviceCollection, addPointsProcessor, BackgroundProcessTypeEnum.REMOVE_POINTS);
    	
    	// redirect to results page
        model.addAttribute("resultsId", id);
        return "redirect:removePointsResults";
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
    @RequestMapping("removePointsResults")
    public String removePointsResults(ModelMap model, HttpServletRequest request) throws ServletException {
        
        // prepare mav with basic results data
        prepResultsView(model, request);
        
        // options
        boolean sharedPoints = ServletRequestUtils.getBooleanParameter(request, "sharedPoints", true);
        boolean maskMissingPoints = ServletRequestUtils.getBooleanParameter(request, "maskMissingPoints", false);
        model.addAttribute("sharedPoints", sharedPoints);
        model.addAttribute("maskMissingPoints", maskMissingPoints);
        
        return "removePoints/removePointsResults.jsp";
    }
}