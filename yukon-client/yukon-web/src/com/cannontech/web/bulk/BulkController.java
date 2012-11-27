package com.cannontech.web.bulk;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.common.bulk.callbackResult.BackgroundProcessResultHolder;
import com.cannontech.common.bulk.callbackResult.BackgroundProcessTypeEnum;
import com.cannontech.common.bulk.callbackResult.BulkFieldBackgroupProcessResultHolder;
import com.cannontech.common.bulk.callbackResult.ImportUpdateCallbackResult;
import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.bulk.collection.device.DeviceCollectionCreationException;
import com.cannontech.common.bulk.field.BulkFieldColumnHeader;
import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;
import com.cannontech.common.device.model.DeviceCollectionReportDevice;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.util.MappingList;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.common.util.ReverseList;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.service.PaoLoadingService;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.tools.csv.CSVReader;
import com.cannontech.tools.csv.CSVWriter;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;

/**
 * Spring controller class for bulk operations
 */
public class BulkController extends BulkControllerBase {

    private final static int MAX_SELECTED_DEVICES_DISPLAYED = 1000;
    
    private PaoLoadingService paoLoadingService = null;
    private RecentResultsCache<BackgroundProcessResultHolder> recentResultsCache = null;
    private RolePropertyDao rolePropertyDao;
    private DeviceGroupService deviceGroupService;
    private TemporaryDeviceGroupService temporaryDeviceGroupService;
    private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    
    // BULK HOME
    public ModelAndView bulkHome(HttpServletRequest request, HttpServletResponse response) throws ServletException {

        ModelAndView mav = new ModelAndView("bulkHome.jsp");
        
        // INIT RESULT LISTS
        //------------------------------------------------------------------------------------------
        
        // bulk update operations (add both completed and pending to same list)
        List<BackgroundProcessResultHolder> rawResultsList = new ArrayList<BackgroundProcessResultHolder>();
        
        // ADD RESULTS TO LISTS
        // -----------------------------------------------------------------------------------------
        
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        LiteYukonUser user = userContext.getYukonUser();
        boolean hasBulkImportRP = rolePropertyDao.checkProperty(YukonRoleProperty.BULK_IMPORT_OPERATION, user);
        boolean hasBulkUpdateRP = rolePropertyDao.checkProperty(YukonRoleProperty.BULK_UPDATE_OPERATION, user);
        boolean hasMassChangeRP = rolePropertyDao.checkProperty(YukonRoleProperty.MASS_CHANGE, user);
        boolean hasMassDeleteRP = rolePropertyDao.checkProperty(YukonRoleProperty.MASS_DELETE, user);
        boolean hasAddRemovePointsRP = rolePropertyDao.checkProperty(YukonRoleProperty.ADD_REMOVE_POINTS, user);
        
        // results
        rawResultsList.addAll(new ReverseList<BackgroundProcessResultHolder>(recentResultsCache.getPending()));
        rawResultsList.addAll(new ReverseList<BackgroundProcessResultHolder>(recentResultsCache.getCompleted()));
        
        BulkOpToViewableBulkOpMapper bulkOpToViewableBulkOpMapper = new BulkOpToViewableBulkOpMapper(hasBulkImportRP,
                                                                                                     hasBulkUpdateRP,
                                                                                                     hasMassChangeRP,
                                                                                                     hasMassDeleteRP,
                                                                                                     hasAddRemovePointsRP);
        MappingList<BackgroundProcessResultHolder, BulkOperationDisplayableResult> resultsList = new MappingList<BackgroundProcessResultHolder, BulkOperationDisplayableResult>(rawResultsList, bulkOpToViewableBulkOpMapper);
        
        mav.addObject("hasBulkImportRP", hasBulkImportRP);
        mav.addObject("hasBulkUpdateRP", hasBulkUpdateRP);
        mav.addObject("hasMassChangeRP", hasMassChangeRP);
        mav.addObject("hasMassDeleteRP", hasMassDeleteRP);
        mav.addObject("resultsList", resultsList);
        
        return mav;
    }
    

    // COLLECTION ACTIONS
    public ModelAndView collectionActions(HttpServletRequest request, HttpServletResponse response) throws ServletException {

        ModelAndView mav;
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        LiteYukonUser user = userContext.getYukonUser();
        
        try {
        
            if (request.getMethod().equals("POST")) {
                // if we got here from a post (like the file upload), let's redirect
                mav = new ModelAndView("redirect:/bulk/collectionActions");
                Map<String, String> collectionParameters = this.getDeviceCollection(request).getCollectionParameters();
                mav.addAllObjects(collectionParameters);
            } else {
                mav = new ModelAndView("collectionActions.jsp");
                this.addDeviceCollectionToModel(mav, request);
                boolean showGroupManagement = rolePropertyDao.checkProperty(YukonRoleProperty.DEVICE_GROUP_MODIFY, user);
                boolean showAddRemovePoints = rolePropertyDao.checkProperty(YukonRoleProperty.ADD_REMOVE_POINTS, user);
                boolean hasMassDelete = rolePropertyDao.checkProperty(YukonRoleProperty.MASS_DELETE, user);
                boolean hasMassChange = rolePropertyDao.checkProperty(YukonRoleProperty.MASS_CHANGE, user);
                boolean showEditing = hasMassChange || hasMassDelete;
                
                
                mav.addObject("showGroupManagement", showGroupManagement);
                mav.addObject("showEditing", showEditing);
                mav.addObject("showAddRemovePoints", showAddRemovePoints);
            }
        } catch (ObjectMappingException e) {
            
            mav = new ModelAndView("redirect:/bulk/deviceSelection");
            mav.addObject("errorMsg", e.getMessage());
        }
        
        return mav;
    }
    
    public ModelAndView deviceSelection(HttpServletRequest request, HttpServletResponse response) throws ServletException {

        ModelAndView mav = new ModelAndView("deviceSelection.jsp");
        
        String errorMsg = ServletRequestUtils.getStringParameter(request, "errorMsg", null);
        if (!StringUtils.isBlank(errorMsg)) {
            mav.addObject("errorMsg", errorMsg);
        }
        
        return mav;
    }
    
    public ModelAndView deviceSelectionGetDevices(HttpServletRequest request, HttpServletResponse response) throws ServletException {

    	try {
    		return collectionActions(request, response);
    	} catch (DeviceCollectionCreationException e) {
    		ModelAndView mav = new ModelAndView("redirect:/bulk/deviceSelection");
            mav.addObject("errorMsg", e.getMessage());
            return mav;
    	}
    }
    
    // DEVICE COLLECTION REPORT
    public ModelAndView deviceCollectionReport(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        
        ModelAndView mav = new ModelAndView("deviceCollectionReport.jsp");
        DeviceCollection deviceCollection = this.deviceCollectionFactory.createDeviceCollection(request);
        mav.addObject("deviceCollection", deviceCollection);
        
        StoredDeviceGroup tempDeviceGroup = temporaryDeviceGroupService.createTempGroup(null);
        deviceGroupMemberEditorDao.addDevices(tempDeviceGroup, deviceCollection.getDeviceList());
        
        mav.addObject("tempDeviceGroup", tempDeviceGroup);
        mav.addObject("collectionDescriptionResovlable", deviceCollection.getDescription());
        
        return mav;
    }
    
    // SELECTED DEVICES POPUP TBALE
    public ModelAndView selectedDevicesTableForDeviceCollection(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        
        DeviceCollection deviceCollection = this.deviceCollectionFactory.createDeviceCollection(request);
        int totalDeviceCount = (int)deviceCollection.getDeviceCount();
        List<SimpleDevice> devicesToLoad = deviceCollection.getDevices(0, MAX_SELECTED_DEVICES_DISPLAYED);

        return getSelectedDevicesTableMav(devicesToLoad, totalDeviceCount);
    }
    
    public ModelAndView selectedDevicesTableForGroupName(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        
        String groupName = ServletRequestUtils.getRequiredStringParameter(request, "groupName");
        DeviceGroup group = deviceGroupService.resolveGroupName(groupName);
        
        int totalDeviceCount = deviceGroupService.getDeviceCount(Collections.singletonList(group));
        Set<SimpleDevice> devicesToLoad = deviceGroupService.getDevices(Collections.singletonList(group), MAX_SELECTED_DEVICES_DISPLAYED);
        
        return getSelectedDevicesTableMav(devicesToLoad, totalDeviceCount);
        
    }
    
    private ModelAndView getSelectedDevicesTableMav(Collection<SimpleDevice> devicesToLoad, int totalDeviceCount) {
        
        ModelAndView mav = new ModelAndView("selectedDevicesPopup.jsp");
        
        List<DeviceCollectionReportDevice> deviceCollectionReportDevices = paoLoadingService.getDeviceCollectionReportDevices(devicesToLoad);
        Collections.sort(deviceCollectionReportDevices);
        
        List<Map<String, Object>> deviceInfoList = new ArrayList<Map<String, Object>>();
        for (DeviceCollectionReportDevice device : deviceCollectionReportDevices) {
            
            Map<String, Object> deviceInfo = new LinkedHashMap<String, Object>();
            
            deviceInfo.put("Device Name", device.getName());
            deviceInfo.put("Address", device.getAddress());
            deviceInfo.put("Route", device.getRoute());
            
            deviceInfoList.add(deviceInfo);
        }
        
        if (totalDeviceCount > MAX_SELECTED_DEVICES_DISPLAYED) {
            mav.addObject("resultsLimitedTo", MAX_SELECTED_DEVICES_DISPLAYED);
        }
        mav.addObject("deviceInfoList", deviceInfoList);
        
        return mav;
    }
    
    
    public ModelAndView processingExceptionErrorsRefresh(HttpServletRequest request, HttpServletResponse response) throws ServletException {

        ModelAndView mav = new ModelAndView("processingExceptionErrorsList.jsp");

        String resultsId = ServletRequestUtils.getRequiredStringParameter(request, "resultsId");
        BackgroundProcessResultHolder bulkUpdateOperationResults = recentResultsCache.getResult(resultsId);
        
        mav.addObject("exceptionRowNumberMap", bulkUpdateOperationResults.getProcessingExceptionRowNumberMap());
        return mav;
    }
    
    public ModelAndView processingExceptionFileDownload(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {


        String resultsId = ServletRequestUtils.getRequiredStringParameter(request, "resultsId");
        BackgroundProcessResultHolder backGroundCallback = recentResultsCache.getResult(resultsId);
        
        ImportUpdateCallbackResult callback = (ImportUpdateCallbackResult)backGroundCallback;
        
        // header row
        InputStreamReader inputStreamReader = new InputStreamReader(callback.getBulkFileInfo().getFileResource().getInputStream());
        BufferedReader reader = new BufferedReader(inputStreamReader);
        CSVReader csvReader = new CSVReader(reader);
        String[] headerRow = csvReader.readNext();
        csvReader.close();
         
        // failed lines
        List<String[]> fileLines = callback.getProcesingExceptionObjects();
        
        response.setContentType("text/csv");
        response.setHeader("Content-Type", "application/force-download");
        response.setHeader("Content-Disposition","attachment; filename=\"" + ServletUtil.makeWindowsSafeFileName("ExceptionsFile_" + resultsId) + ".csv\"");
        OutputStream outputStream = response.getOutputStream();
        
        Writer writer = new BufferedWriter(new OutputStreamWriter(outputStream));
        CSVWriter csvWriter = new CSVWriter(writer);
        
        csvWriter.writeNext(headerRow);
        for (String[] line : fileLines) {
            csvWriter.writeNext(line);
        }
        csvWriter.close();
        
        return null;
    }
    
    // HELPER MAPPER TO CREATE RESULTS THAT HAVE A "DETAIL VIEWABLE" PROPERTY BASED ON USER ROLE PROP
    // Used for display on main bulk ops page to display/hide the "view" link.
    private class BulkOpToViewableBulkOpMapper implements ObjectMapper<BackgroundProcessResultHolder, BulkOperationDisplayableResult> {

        private boolean hasBulkImportRP = false;
        private boolean hasBulkUpdateRP = false;
        private boolean hasMassChangeRP = false;
        private boolean hasMassDeleteRP = false;
        private boolean hasAddRemovePointsRP = false;

        public BulkOpToViewableBulkOpMapper(boolean hasBulkImportRP,
                boolean hasBulkUpdateRP, boolean hasMassChangeRP,
                boolean hasMassDeleteRP,
                boolean hasAddRemovePointsRP) {
            this.hasBulkImportRP = hasBulkImportRP;
            this.hasBulkUpdateRP = hasBulkUpdateRP;
            this.hasMassChangeRP = hasMassChangeRP;
            this.hasMassDeleteRP = hasMassDeleteRP;
            this.hasAddRemovePointsRP = hasAddRemovePointsRP;
        }

        public BulkOperationDisplayableResult map(BackgroundProcessResultHolder from) throws ObjectMappingException {
        	
        	List<BulkFieldColumnHeader> bulkFieldColumnHeaders = null;
        	if (from instanceof BulkFieldBackgroupProcessResultHolder) {
        		bulkFieldColumnHeaders = ((BulkFieldBackgroupProcessResultHolder)from).getBulkFieldColumnHeaders();
        	}
        	
            return new BulkOperationDisplayableResult(from,
                                                      this.hasBulkImportRP,
                                                      this.hasBulkUpdateRP,
                                                      this.hasMassChangeRP,
                                                      this.hasMassDeleteRP,
                                                      this.hasAddRemovePointsRP,
                                                      bulkFieldColumnHeaders);
        }
    }
    
    public class BulkOperationDisplayableResult {

        private BackgroundProcessResultHolder result;
        private boolean hasBulkImportRP = false;
        private boolean hasBulkUpdateRP = false;
        private boolean hasMassChangeRP = false;
        private boolean hasMassDeleteRP = false;
        private boolean hasAddRemovePointsRP = false;
        private List<BulkFieldColumnHeader> bulkFieldColumnHeaders = null;

        public BulkOperationDisplayableResult(
        		BackgroundProcessResultHolder result,
                boolean hasBulkImportRP, boolean hasBulkUpdateRP,
                boolean hasMassChangeRP, boolean hasMassDeleteRP,
                boolean hasAddRemovePointsRP,
                List<BulkFieldColumnHeader> bulkFieldColumnHeaders) {
            this.result = result;
            this.hasBulkImportRP = hasBulkImportRP;
            this.hasBulkUpdateRP = hasBulkUpdateRP;
            this.hasMassChangeRP = hasMassChangeRP;
            this.hasMassDeleteRP = hasMassDeleteRP;
            this.hasAddRemovePointsRP = hasAddRemovePointsRP;
            this.bulkFieldColumnHeaders = bulkFieldColumnHeaders;
        }

        public BackgroundProcessResultHolder getResult() {
            return result;
        }

		public boolean isHasBulkImportRP() {
			return hasBulkImportRP;
		}

		public boolean isHasBulkUpdateRP() {
			return hasBulkUpdateRP;
		}

		public boolean isHasMassChangeRP() {
			return hasMassChangeRP;
		}

		public boolean isHasMassDeleteRP() {
			return hasMassDeleteRP;
		}

		public boolean isHasAddRemovePointsRP() {
			return hasAddRemovePointsRP;
		}
		
		public List<BulkFieldColumnHeader> getBulkFieldColumnHeaders() {
			return bulkFieldColumnHeaders;
		}
        
        public boolean isDetailViewable() {

            BackgroundProcessTypeEnum opType = this.result.getBackgroundProcessType();
            boolean detailViewable = (this.hasBulkImportRP && opType.equals(BackgroundProcessTypeEnum.IMPORT)) 
            						|| (this.hasBulkUpdateRP && opType.equals(BackgroundProcessTypeEnum.UPDATE)) 
            						|| (this.hasMassChangeRP && (opType.equals(BackgroundProcessTypeEnum.MASS_CHANGE) || opType.equals(BackgroundProcessTypeEnum.CHANGE_DEVICE_TYPE))) 
            			            || (this.hasMassDeleteRP && opType.equals(BackgroundProcessTypeEnum.MASS_DELETE)
            			            || (this.hasAddRemovePointsRP && (opType.equals(BackgroundProcessTypeEnum.ADD_POINTS) || opType.equals(BackgroundProcessTypeEnum.REMOVE_POINTS))));
            return detailViewable;
        }
    }
    
    @Required
    public void setRecentResultsCache(RecentResultsCache<BackgroundProcessResultHolder> recentResultsCache) {
        this.recentResultsCache = recentResultsCache;
    }

    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
		this.rolePropertyDao = rolePropertyDao;
	}
    
    @Autowired
    public void setPaoLoadingService(PaoLoadingService paoLoadingService) {
        this.paoLoadingService = paoLoadingService;
    }
    
    @Autowired
    public void setDeviceGroupService(DeviceGroupService deviceGroupService) {
        this.deviceGroupService = deviceGroupService;
    }
    
    @Autowired
    public void setTemporaryDeviceGroupService(TemporaryDeviceGroupService temporaryDeviceGroupService) {
		this.temporaryDeviceGroupService = temporaryDeviceGroupService;
	}
    
    @Autowired
    public void setDeviceGroupMemberEditorDao(DeviceGroupMemberEditorDao deviceGroupMemberEditorDao) {
		this.deviceGroupMemberEditorDao = deviceGroupMemberEditorDao;
	}
}
