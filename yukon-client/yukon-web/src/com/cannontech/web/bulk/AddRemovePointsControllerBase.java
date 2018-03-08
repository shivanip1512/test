package com.cannontech.web.bulk;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.BulkProcessor;
import com.cannontech.common.bulk.callbackResult.BackgroundProcessTypeEnum;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.collection.device.model.CollectionAction;
import com.cannontech.common.bulk.collection.device.model.CollectionActionBulkProcessorCallback;
import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.collection.device.service.CollectionActionService;
import com.cannontech.common.bulk.mapper.PassThroughMapper;
import com.cannontech.common.bulk.processor.Processor;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoDefinition;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.pao.definition.model.PointTemplate;
import com.cannontech.common.pao.service.PointCreationService;
import com.cannontech.common.pao.service.PointService;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.point.PointType;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.bulk.model.PaoTypeMasks;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.base.Function;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;

@CheckRoleProperty(YukonRoleProperty.ADD_REMOVE_POINTS)
public abstract class AddRemovePointsControllerBase {

    @Autowired protected PaoDefinitionDao paoDefinitionDao;
    @Autowired protected PointService pointService;
    @Autowired protected PointCreationService pointCreationService;
    @Autowired protected DBPersistentDao dbPersistentDao;
    @Autowired protected TemporaryDeviceGroupService temporaryDeviceGroupService;
    @Autowired protected DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    @Autowired protected DeviceGroupCollectionHelper deviceGroupCollectionHelper;
    @Autowired protected PointDao pointDao;
    @Autowired protected PaoDao paoDao;
    @Autowired protected CollectionActionService collectionActionService;
    
    @Resource(name="transactionPerItemProcessor") protected BulkProcessor bulkProcessor;

    
    private Logger log = YukonLogManager.getLogger(AddRemovePointsControllerBase.class);
    
    // ABSTRACT
    public abstract String home(ModelMap model, HttpServletRequest request) throws Exception, ServletException;
    public abstract String execute(ModelMap model, HttpServletRequest request) throws ServletException, Exception;
    
    protected String redirectWithError(ModelMap model, String errorMsg, DeviceCollection deviceCollection) {
        model.addAllAttributes(deviceCollection.getCollectionParameters());
        model.addAttribute("errorMsg", errorMsg);
        return "redirect:home";
    }
    
    // START BULK PROCESSOR
    public int startBulkProcessor(CollectionAction action, DeviceCollection deviceCollection, Processor<? super YukonDevice> processor, BackgroundProcessTypeEnum backgroundProcessType, YukonUserContext context) {
             
        
        CollectionActionResult result = collectionActionService.createResult(action, null,
            deviceCollection, context);
        
        // PROCESS
        ObjectMapper<SimpleDevice, SimpleDevice> mapper = new PassThroughMapper<>();
        
        bulkProcessor.backgroundBulkProcess(deviceCollection.iterator(), mapper, processor,
            new CollectionActionBulkProcessorCallback(result, collectionActionService));

        return result.getCacheKey();
    }

    // PREP RESULTS PAGE
    protected void prepResultsView(ModelMap model, HttpServletRequest request) throws ServletRequestBindingException {
    	
    	// result info
        String resultsId = ServletRequestUtils.getRequiredStringParameter(request, "resultsId");
   /*     AddRemovePointsCallbackResult callbackResult = (AddRemovePointsCallbackResult)recentResultsCache.getResult(resultsId);
        model.addAttribute("callbackResult", callbackResult);
        model.addAttribute("fileName", callbackResult.getDeviceCollection().getUploadFileName());
        // device collection
        model.addAttribute("deviceCollection", callbackResult.getDeviceCollection());*/
    }
    
    // MISC LIST/MAP ORGANIZING HELPERS
    protected Set<PaoType> getDeviceTypesSet(DeviceCollection deviceCollection){
    	
    	Set<PaoType> deviceTypeSet = Sets.newLinkedHashSet();
        for (SimpleDevice device : deviceCollection.getDeviceList()) {
        	deviceTypeSet.add(device.getDeviceType());
        }
        return deviceTypeSet;
    }
    
    protected Map<PaoType, DeviceCollection> getDeviceTypeDeviceCollectionMap(Set<PaoType> paoTypeSet, DeviceCollection deviceCollection) {
    	
    	List<SimpleDevice> devices = Lists.newArrayList(deviceCollection.getDeviceList());
    	
    	Map<PaoType, DeviceCollection> deviceTypeDeviceCollectionMap = Maps.newLinkedHashMap();
        for (PaoType paoType : paoTypeSet) {
        	
        	List<SimpleDevice> devicesOfType = new ArrayList<>();
        	StoredDeviceGroup typeGroup = temporaryDeviceGroupService.createTempGroup();
        	for (SimpleDevice device : devices) {
        		
        		if (device.getDeviceType().equals(paoType)) {
        			devicesOfType.add(device);
        		}
        	}
        	deviceGroupMemberEditorDao.addDevices(typeGroup, devicesOfType);
        	devices.removeAll(devicesOfType);
        	
        	deviceTypeDeviceCollectionMap.put(paoType, deviceGroupCollectionHelper.buildDeviceCollection(typeGroup));
        }
        return deviceTypeDeviceCollectionMap;
    }
    
    protected Map<PointTemplate, Boolean> createSharedPointsTemplateMap(List<PaoTypeMasks> paoTypeMaskList) {
    	ArrayListMultimap<PointTemplate, Boolean> sharedPointTemplateMasks = ArrayListMultimap.create();
    	
    	for (PaoTypeMasks paoTypeMasks : paoTypeMaskList) {
    	    SetMultimap<PointTemplate, Boolean> pointTemplateMask = Multimaps.forMap(paoTypeMasks.getPointTemplateMaskMap());
            sharedPointTemplateMasks.putAll(pointTemplateMask);
        }
    	
    	// remove un-common types
    	Set<PointTemplate> removeList = Sets.newHashSet();
    	for (PointTemplate sharedPointTemplate : sharedPointTemplateMasks.keySet()) {
    	    int pointTemplateCount = sharedPointTemplateMasks.get(sharedPointTemplate).size();
    	    if (pointTemplateCount < paoTypeMaskList.size()) {
    	        removeList.add(sharedPointTemplate);
    	    }
    	}

    	for (PointTemplate removedPointTemplate : removeList) {
    	    sharedPointTemplateMasks.removeAll(removedPointTemplate);
        }
    	
    	// Handle the masking for the result set.
    	Function<Collection<Boolean>, Boolean> func = new Function<Collection<Boolean>, Boolean>() {
    	    @Override
    	    public Boolean apply(Collection<Boolean> pointTemplateMaskSet) {
    	        return isSharedPointTemplateMasked(pointTemplateMaskSet);
    	    }
    	};
    	
        Map<PointTemplate, Boolean> resultUnsorted = Maps.transformValues(sharedPointTemplateMasks.asMap(), func);
        return new TreeMap<>(resultUnsorted);
    }
    
    /**
     * This method handles the masking used for the shared functionality of the add/remove point sets.
     * 
     * @param pointTemplateMaskSet
     * @return
     */
    protected boolean isSharedPointTemplateMasked(Iterable<Boolean> pointTemplateMaskSet){
        for (Boolean pointTemplateMask : pointTemplateMaskSet) {
            if (pointTemplateMask == false) {
                return false;
            }
        }
        return true;
    }
    
    protected void addPointListToAllPointTemplates(Map<PointTemplate, Boolean> allPointTemplates, Map<PointTemplate, Boolean> pointTypePointList){
        allPointTemplates.putAll(pointTypePointList);
    }
    
    protected Map<PaoType, Set<PointTemplate>> extractPointTemplatesMapFromParameters (HttpServletRequest request, DeviceCollection deviceCollection, boolean commonPoints) {
    	
    	// POINT TAMPLATES MAP
        final Map<PaoType, Set<PointTemplate>> pointTemplatesMap = Maps.newHashMap();
        
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
        	
        	String parameterName = parameterNames.nextElement();
        	
        	if (parameterName.startsWith("PT:")) {
        		
        		String[] parts = StringUtils.split(parameterName, ":");
        		if (parts.length == 4) {
        			
        		    
        		    int paoTypeId = Integer.parseInt(parts[1]);
        		    PaoType paoType = null;
        		    if (paoTypeId != 0) { 
        		        paoType = PaoType.getForId(paoTypeId);
        		    }
        			int pointType = Integer.parseInt(parts[2]);
        			int offset = Integer.parseInt(parts[3]);
        			
        			List<PaoType> checkedPaoTypes = Lists.newArrayList();
        			
        			// common point, process as if check box was selected for all device types
        			if (commonPoints && paoType == null) {
        				
        				Set<PaoType> allDeviceTypesSet = Sets.newHashSet();
        				for (SimpleDevice device : deviceCollection.getDeviceList()) {
        					allDeviceTypesSet.add(device.getDeviceType());
        				}
        				checkedPaoTypes.addAll(allDeviceTypesSet);
        			}
        			
        			// single device type check box
        			if (!commonPoints && paoType != null) {
        				checkedPaoTypes.add(paoType);
        			}
        			
        			for (PaoType checkedDeviceType : checkedPaoTypes) {
        			
        				log.debug("Selected point checkbox: deviceType=" + checkedDeviceType + " pointType=" + pointType + " offset=" + offset);
        			
	        			PointTemplate pointTemplate = paoDefinitionDao.getPointTemplateByTypeAndOffset(checkedDeviceType, new PointIdentifier(PointType.getForId(pointType), offset));
	        			
	        			if (!pointTemplatesMap.containsKey(checkedDeviceType)) {
	        				pointTemplatesMap.put(checkedDeviceType, new HashSet<PointTemplate>());
	        			}
	        			pointTemplatesMap.get(checkedDeviceType).add(pointTemplate);
        			}
        		}
        	}
        }
    	return pointTemplatesMap;
    }
    
    // points map helper
    protected List<PaoTypeMasks> createExistsPointsMap(Set<PaoType> paoTypeSet, boolean maskExistingPoints, 
                                                       boolean maskIfExistOnAllDevices, DeviceCollection deviceCollection) {

        // first pull out all the device from the collection that match this device type
        HashMultimap<PaoType, SimpleDevice> paoTypeToSimpleDeviceMultiMap = null;
        if (maskExistingPoints) {
            paoTypeToSimpleDeviceMultiMap = HashMultimap.create();
            
            for (SimpleDevice device : deviceCollection.getDeviceList()) {
                paoTypeToSimpleDeviceMultiMap.put(device.getDeviceType(), device);
            }
        }
        
        List<PaoTypeMasks> paoTypeMasksList = Lists.newArrayList();
        for (PaoType paoType : paoTypeSet) {
            
            // all defined point templates for device type, convert to wrappers that are all initially unmasked
            PaoDefinition paoDefiniton = paoDefinitionDao.getPaoDefinition(paoType);
            Set<PointTemplate> allPointTemplates = paoDefinitionDao.getAllPointTemplates(paoDefiniton);
            Map<PointTemplate, Boolean> pointTemplateMaskMapUnsorted = createDefaultPointTemplateMaskMap(allPointTemplates);
            
            // mask those device type points where all the the device of this type have the point
            if (maskExistingPoints) {
                
                fillInPointTemplateMask(maskIfExistOnAllDevices,
                                        paoTypeToSimpleDeviceMultiMap, paoType,
                                        pointTemplateMaskMapUnsorted);
            }
            
            PaoTypeMasks paoTypeMasks = new PaoTypeMasks();
            paoTypeMasks.setPaoType(paoType);
            TreeMap<PointTemplate, Boolean> pointTemplateMaskMapSorted = 
                new TreeMap<>(pointTemplateMaskMapUnsorted);
            paoTypeMasks.setPointTemplateMaskMap(pointTemplateMaskMapSorted);
            paoTypeMasksList.add(paoTypeMasks);
            
        }
        return paoTypeMasksList;
    }
    
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
            boolean allDevicesHavePoint = true;
            for (SimpleDevice device : paoTypeToSimpleDeviceMultiMap.get(paoType)) {
                boolean pointExistsForDevice = 
                    pointService.pointExistsForPao(device, pointTemplate.getPointIdentifier());
                if (!pointExistsForDevice) {
                    allDevicesHavePoint = false;
                    break;
                }
            }
            
            if(maskIfExistOnAllDevices){
                if (allDevicesHavePoint) {
                    pointTemplateMaskMap.put(pointTemplate, true);
                }
            }else {
                if (!allDevicesHavePoint) {
                    pointTemplateMaskMap.put(pointTemplate, true);
                }
            }
        }
    }
   
    public Map<PointTemplate, Boolean> createDefaultPointTemplateMaskMap(Set<PointTemplate> allPointTemplates){
        Map<PointTemplate, Boolean> results = Maps.newTreeMap(new PointTemplateMaskMapComparator());
        for (PointTemplate pointTemplate : allPointTemplates) {
            results.put(pointTemplate, false);
        }
        return results;
    }
    
    public class PointTemplateMaskMapComparator implements Comparator<PointTemplate> {

        @Override
        public int compare(PointTemplate o1, PointTemplate o2) {
            // compares types
            int typeCompare = 
                Integer.valueOf(o1.getPointType().getPointTypeId()).compareTo(o2.getPointType().getPointTypeId());
            
            if(typeCompare != 0) {
                return typeCompare;
            }
            
            return Integer.valueOf(o1.getPointIdentifier().getOffset()).compareTo(o2.getPointIdentifier().getOffset());
            
        }
        
    }
    
}