package com.cannontech.web.bulk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.BulkProcessor;
import com.cannontech.common.bulk.callbackResult.AddRemovePointsCallbackResult;
import com.cannontech.common.bulk.callbackResult.BackgroundProcessResultHolder;
import com.cannontech.common.bulk.callbackResult.BackgroundProcessTypeEnum;
import com.cannontech.common.bulk.collection.DeviceCollection;
import com.cannontech.common.bulk.collection.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.bulk.mapper.PassThroughMapper;
import com.cannontech.common.bulk.processor.SingleProcessor;
import com.cannontech.common.device.DeviceType;
import com.cannontech.common.device.definition.dao.DeviceDefinitionDao;
import com.cannontech.common.device.definition.model.PointIdentifier;
import com.cannontech.common.device.definition.model.PointTemplate;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.device.service.PointService;
import com.cannontech.common.util.MappingSet;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.pao.PaoGroupsWrapper;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@CheckRoleProperty(YukonRoleProperty.ADD_REMOVE_POINTS)
public abstract class AddRemovePointsControllerBase extends BulkControllerBase {

	protected BulkProcessor bulkProcessor = null;
    protected PaoGroupsWrapper paoGroupsWrapper;
    protected DeviceDefinitionDao deviceDefinitionDao;
    protected PointService pointService;
    protected DBPersistentDao dbPersistentDao;
    protected TemporaryDeviceGroupService temporaryDeviceGroupService;
    protected DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    protected DeviceGroupCollectionHelper deviceGroupCollectionHelper;
    protected RecentResultsCache<BackgroundProcessResultHolder> recentResultsCache;
    
    private Logger log = YukonLogManager.getLogger(AddRemovePointsControllerBase.class);
    protected static Comparator<PointTemplateWrapper> pointTemplateOffsetCompartor;
    
    static {
    	 pointTemplateOffsetCompartor = new java.util.Comparator<PointTemplateWrapper>() {
			public int compare(PointTemplateWrapper o1, PointTemplateWrapper o2){
				return o1.getPointTemplate().getOffset() - o2.getPointTemplate().getOffset();
			}
		};
    }
    
    // ABSTRACT
    public abstract ModelAndView home(HttpServletRequest request, HttpServletResponse response) throws Exception, ServletException;
    public abstract ModelAndView execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, Exception;
    
    
    // START BULK PROCESSOR
    public String startBulkProcessor(DeviceCollection deviceCollection, SingleProcessor<SimpleDevice> processor, BackgroundProcessTypeEnum backgroundProcessType) throws ServletException, Exception {
        
        // CALLBACK
    	String resultsId = StringUtils.replace(UUID.randomUUID().toString(), "-", "");
    	StoredDeviceGroup successGroup = temporaryDeviceGroupService.createTempGroup(null);
        StoredDeviceGroup processingExceptionGroup = temporaryDeviceGroupService.createTempGroup(null);
        
        AddRemovePointsCallbackResult callbackResult = new AddRemovePointsCallbackResult(backgroundProcessType,
																					resultsId,
																					deviceCollection, 
																					successGroup, 
																					processingExceptionGroup, 
																					deviceGroupMemberEditorDao,
																					deviceGroupCollectionHelper);
        
        // CACHE
        recentResultsCache.addResult(resultsId, callbackResult);
        
        // PROCESS
        ObjectMapper<SimpleDevice, SimpleDevice> mapper = new PassThroughMapper<SimpleDevice>();
        bulkProcessor.backgroundBulkProcess(deviceCollection.iterator(), mapper, processor, callbackResult);
        
        return resultsId;
    }
    
    // PREP RESULTS PAGE
    protected void prepResultsView(HttpServletRequest request, ModelAndView mav) throws ServletRequestBindingException {
    	
    	// result info
        String resultsId = ServletRequestUtils.getRequiredStringParameter(request, "resultsId");
        AddRemovePointsCallbackResult callbackResult = (AddRemovePointsCallbackResult)recentResultsCache.getResult(resultsId);
        mav.addObject("callbackResult", callbackResult);
        
        // device collection
        mav.addObject("deviceCollection", callbackResult.getDeviceCollection());
    }
    
    // MISC LIST/MAP ORGANIZING HELPERS
    protected Set<Integer> getDeviceTypesSet(DeviceCollection deviceCollection){
    	
    	Set<Integer> deviceTypeSet = new LinkedHashSet<Integer>();
        for (SimpleDevice device : deviceCollection.getDeviceList()) {
        	deviceTypeSet.add(device.getType());
        }
        return deviceTypeSet;
    }
    
    protected Map<Integer, String> getDeviceTypeNamesMap(Set<Integer> deviceTypeSet) {
    	
    	Map<Integer, String> deviceTypeNamesMap = new LinkedHashMap<Integer, String>();
        for (int deviceType : deviceTypeSet) {
        	String deviceTypeName = paoGroupsWrapper.getPAOTypeString(deviceType);
        	deviceTypeNamesMap.put(deviceType, deviceTypeName);
        }
        return deviceTypeNamesMap;
    }
    
    protected Map<Integer, DeviceType> getDeviceTypeEnumMap(Set<Integer> deviceTypeSet) {
    	
    	Map<Integer, DeviceType> deviceTypeEnumMap = new LinkedHashMap<Integer, DeviceType>();
        for (int deviceType : deviceTypeSet) {
        	DeviceType type = DeviceType.getForId(deviceType);
        	deviceTypeEnumMap.put(deviceType, type);
        }
        return deviceTypeEnumMap;
    }
    
    protected Map<Integer, DeviceCollection> getDeviceTypeDeviceCollectionMap(Set<Integer> deviceTypeSet, DeviceCollection deviceCollection) {
    	
    	List<SimpleDevice> devices = deviceCollection.getDeviceList();
    	
    	Map<Integer, DeviceCollection> deviceTypeDeviceCollectionMap = new LinkedHashMap<Integer, DeviceCollection>();
        for (int deviceType : deviceTypeSet) {
        	
        	List<SimpleDevice> devicesOfType = new ArrayList<SimpleDevice>();
        	StoredDeviceGroup typeGroup = temporaryDeviceGroupService.createTempGroup(null);
        	for (SimpleDevice device : devices) {
        		
        		if (device.getType() == deviceType) {
        			devicesOfType.add(device);
        		}
        	}
        	deviceGroupMemberEditorDao.addDevices(typeGroup, devicesOfType);
        	devices.removeAll(devicesOfType);
        	
        	deviceTypeDeviceCollectionMap.put(deviceType, deviceGroupCollectionHelper.buildDeviceCollection(typeGroup));
        }
        return deviceTypeDeviceCollectionMap;
    }
    
    protected Set<PointTemplateWrapper> convertToPointTemplateWrapperSet(Set<PointTemplate> set, final boolean defaultMasking) {
    	
    	// map PointTemplate set to a PoinTempalteWrapper set, all initialized with masking false 
    	ObjectMapper<PointTemplate, PointTemplateWrapper> mapper = new ObjectMapper<PointTemplate, PointTemplateWrapper>() {
    		public PointTemplateWrapper map(PointTemplate from) throws ObjectMappingException {
    			PointTemplateWrapper pointTemplateWrapper = new PointTemplateWrapper(from, defaultMasking);
    			return pointTemplateWrapper;
    		}
    	};
    	MappingSet<PointTemplate, PointTemplateWrapper> wrapperSet = new MappingSet<PointTemplate, PointTemplateWrapper>(set, mapper);
    	
    	return wrapperSet;
    }
    
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
    			allPointTemplates.addAll(pointTypePointList);
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
    
    protected Map<String, List<PointTemplateWrapper>> createPointTypeMap(List<PointTemplateWrapper> pointTemplates) {
    	
    	Map<String, List<PointTemplateWrapper>> pointTypeMap = new LinkedHashMap<String, List<PointTemplateWrapper>>();
    	for (PointTemplateWrapper pointTemplateWrapper : pointTemplates) {
    		
    		int pointType = pointTemplateWrapper.getPointTemplate().getType();
    		String pointTypeName = PointTypes.getType(pointType);
    		
    		if (!pointTypeMap.containsKey(pointTypeName)) {
    			pointTypeMap.put(pointTypeName, new ArrayList<PointTemplateWrapper>());
    		}
    		pointTypeMap.get(pointTypeName).add(pointTemplateWrapper);
    	}
    	
    	return pointTypeMap;
    }
    
    @SuppressWarnings("unchecked")
    protected Map<Integer, Set<PointTemplate>> extractPointTemplatesMapFromParameters (HttpServletRequest request, DeviceCollection deviceCollection, boolean commonPoints) {
    	
    	// POINT TAMPLATES MAP
        final Map<Integer, Set<PointTemplate>> pointTemplatesMap = new HashMap<Integer, Set<PointTemplate>>();
        
        Enumeration parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
        	
        	String parameterName = (String)parameterNames.nextElement();
        	
        	if (parameterName.startsWith("PT:")) {
        		
        		String[] parts = StringUtils.split(parameterName, ":");
        		if (parts.length == 4) {
        			
        			int deviceType = Integer.parseInt(parts[1]);
        			int pointType = Integer.parseInt(parts[2]);
        			int offset = Integer.parseInt(parts[3]);
        			
        			List<Integer> checkedDeviceTypes = new ArrayList<Integer>();
        			
        			// common point, process as if check box was selected for all device types
        			if (commonPoints && deviceType == 0) {
        				
        				Set<Integer> allDeviceTypesSet = new HashSet<Integer>();
        				for (SimpleDevice device : deviceCollection.getDeviceList()) {
        					allDeviceTypesSet.add(device.getType());
        				}
        				checkedDeviceTypes.addAll(allDeviceTypesSet);
        			}
        			
        			// single device type check box
        			if (!commonPoints && deviceType > 0) {
        				checkedDeviceTypes.add(deviceType);
        			}
        			
        			for (int checkedDeviceType : checkedDeviceTypes) {
        			
        				log.debug("Selected point checkbox: deviceType=" + checkedDeviceType + " pointType=" + pointType + " offset=" + offset);
        			
	        			PointTemplate pointTemplate = deviceDefinitionDao.getPointTemplateByTypeAndOffset(DeviceType.getForId(checkedDeviceType), new PointIdentifier(pointType, offset));
	        			
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
    
   
    protected class PointTemplateWrapper {
    	
    	private PointTemplate pointTemplate;
    	private boolean masked;
    	
    	public PointTemplateWrapper(PointTemplate pointTemplate, boolean masked) {
    		this.pointTemplate = pointTemplate;
    		this.masked = masked;
    	}

		public PointTemplate getPointTemplate() {
			return pointTemplate;
		}
		public boolean isMasked() {
			return masked;
		}
		public void setMasked(boolean masked) {
			this.masked = masked;
		}
		
		// this wrapper is just to tack on additional info for the UI, they should compare using the PointTemplate they wrap only
		@Override
	    public int hashCode() {
	        return this.getPointTemplate().hashCode();
	    }
		
		@Override
	    public boolean equals(Object obj) {
			return this.getPointTemplate().equals(((PointTemplateWrapper)obj).getPointTemplate());
	    }
    }
    
    @Resource(name="transactionPerItemProcessor")
    public void setBulkProcessor(BulkProcessor bulkProcessor) {
		this.bulkProcessor = bulkProcessor;
	}
    @Autowired
    public void setPaoGroupsWrapper(PaoGroupsWrapper paoGroupsWrapper) {
		this.paoGroupsWrapper = paoGroupsWrapper;
	}
    @Autowired
    public void setDeviceDefinitionDao(DeviceDefinitionDao deviceDefinitionDao) {
		this.deviceDefinitionDao = deviceDefinitionDao;
	}
    @Autowired
    public void setDbPersistentDao(DBPersistentDao dbPersistentDao) {
		this.dbPersistentDao = dbPersistentDao;
	}
    @Autowired
    public void setPointService(PointService pointService) {
		this.pointService = pointService;
	}
    @Autowired
    public void setTemporaryDeviceGroupService(TemporaryDeviceGroupService temporaryDeviceGroupService) {
		this.temporaryDeviceGroupService = temporaryDeviceGroupService;
	}
    @Autowired
    public void setDeviceGroupCollectionHelper(DeviceGroupCollectionHelper deviceGroupCollectionHelper) {
		this.deviceGroupCollectionHelper = deviceGroupCollectionHelper;
	}
    @Autowired
    public void setDeviceGroupMemberEditorDao(DeviceGroupMemberEditorDao deviceGroupMemberEditorDao) {
		this.deviceGroupMemberEditorDao = deviceGroupMemberEditorDao;
	}
    @Resource(name="recentResultsCache")
    public void setRecentResultsCache(RecentResultsCache<BackgroundProcessResultHolder> recentResultsCache) {
        this.recentResultsCache = recentResultsCache;
    }
}
