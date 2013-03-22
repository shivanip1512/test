package com.cannontech.web.bulk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cannontech.common.bulk.callbackResult.BackgroundProcessResultHolder;
import com.cannontech.common.bulk.callbackResult.BackgroundProcessTypeEnum;
import com.cannontech.common.bulk.callbackResult.BulkFieldBackgroupProcessResultHolder;
import com.cannontech.common.bulk.callbackResult.ImportUpdateCallbackResult;
import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.bulk.collection.device.DeviceCollectionCreationException;
import com.cannontech.common.bulk.collection.device.DeviceCollectionFactory;
import com.cannontech.common.bulk.field.BulkFieldColumnHeader;
import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;
import com.cannontech.common.device.model.DeviceCollectionReportDevice;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.MappingList;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.common.util.ReverseList;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.service.PaoLoadingService;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.tools.csv.CSVReader;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.util.WebFileUtils;
import com.google.common.collect.Lists;

/**
 * Handles request directly off the /bulk/* url.
 * Other controllers handle specific sections like the {@link AddPointsController} (/bulk/addPoints/*)
 */
@Controller
@RequestMapping("/*")
public class BulkController {

    private final static int MAX_SELECTED_DEVICES_DISPLAYED = 1000;
    
    @Autowired private PaoLoadingService paoLoadingService;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private TemporaryDeviceGroupService temporaryDeviceGroupService;
    @Autowired private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    @Autowired private YukonUserContextMessageSourceResolver resolver;
    @Autowired private DeviceCollectionFactory deviceCollectionFactory;
    @Resource(name="recentResultsCache") private RecentResultsCache<BackgroundProcessResultHolder> recentResultsCache;
    
    // BULK HOME
    @RequestMapping
    public String bulkHome(ModelMap model, LiteYukonUser user) {

        // INIT RESULT LISTS
        //------------------------------------------------------------------------------------------
        
        // bulk update operations (add both completed and pending to same list)
        List<BackgroundProcessResultHolder> rawResultsList = new ArrayList<BackgroundProcessResultHolder>();
        
        // ADD RESULTS TO LISTS
        // -----------------------------------------------------------------------------------------
        
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
        MappingList<BackgroundProcessResultHolder, BulkOperationDisplayableResult> resultsList = new MappingList<>(rawResultsList, bulkOpToViewableBulkOpMapper);
        
        model.addAttribute("hasBulkImportRP", hasBulkImportRP);
        model.addAttribute("hasBulkUpdateRP", hasBulkUpdateRP);
        model.addAttribute("hasMassChangeRP", hasMassChangeRP);
        model.addAttribute("hasMassDeleteRP", hasMassDeleteRP);
        model.addAttribute("resultsList", resultsList);
        
        return "bulkHome.jsp";
    }
    
    // COLLECTION ACTIONS
    @RequestMapping
    public String collectionActions(ModelMap model, HttpServletRequest request, @RequestParam(defaultValue="false") boolean isFileUpload) throws ServletRequestBindingException {

        String view = "";
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        LiteYukonUser user = userContext.getYukonUser();
        
        DeviceCollection colleciton = deviceCollectionFactory.createDeviceCollection(request);
        try {
        
            if (isFileUpload) {
                Map<String, String> collectionParameters = colleciton.getCollectionParameters();
                model.addAllAttributes(collectionParameters);
            }
            view = "collectionActions.jsp";
            model.addAttribute("deviceCollection", colleciton);
            boolean showGroupManagement = rolePropertyDao.checkProperty(YukonRoleProperty.DEVICE_GROUP_MODIFY, user);
            boolean showAddRemovePoints = rolePropertyDao.checkProperty(YukonRoleProperty.ADD_REMOVE_POINTS, user);
            boolean hasMassDelete = rolePropertyDao.checkProperty(YukonRoleProperty.MASS_DELETE, user);
            boolean hasMassChange = rolePropertyDao.checkProperty(YukonRoleProperty.MASS_CHANGE, user);
            boolean showEditing = hasMassChange || hasMassDelete;
            
            model.addAttribute("showGroupManagement", showGroupManagement);
            model.addAttribute("showEditing", showEditing);
            model.addAttribute("showAddRemovePoints", showAddRemovePoints);
        } catch (ObjectMappingException e) {
            view = "redirect:/bulk/deviceSelection";
            model.addAttribute("errorMsg", e.getMessage());
        }
        
        return view;
    }
    
    @RequestMapping
    public String deviceSelection(ModelMap model, HttpServletRequest request) {
        
        String errorMsg = ServletRequestUtils.getStringParameter(request, "errorMsg", null);
        if (!StringUtils.isBlank(errorMsg)) {
            model.addAttribute("errorMsg", errorMsg);
        }
        
        return "deviceSelection.jsp";
    }
    
    @RequestMapping
    public String deviceSelectionGetDevices(ModelMap model, HttpServletRequest request, @RequestParam(defaultValue="false") boolean isFileUpload) throws ServletRequestBindingException {

    	try {
    		return collectionActions(model, request, isFileUpload);
    	} catch (DeviceCollectionCreationException e) {
            model.addAttribute("errorMsg", e.getMessage());
            return "redirect:/bulk/deviceSelection";
    	}
    }
    
    // DEVICE COLLECTION REPORT
    @RequestMapping
    public String deviceCollectionReport(ModelMap model, HttpServletRequest request) throws ServletRequestBindingException {
        
        DeviceCollection deviceCollection = deviceCollectionFactory.createDeviceCollection(request);
        model.addAttribute("deviceCollection", deviceCollection);
        
        StoredDeviceGroup tempDeviceGroup = temporaryDeviceGroupService.createTempGroup();
        deviceGroupMemberEditorDao.addDevices(tempDeviceGroup, deviceCollection.getDeviceList());
        
        model.addAttribute("tempDeviceGroup", tempDeviceGroup);
        model.addAttribute("collectionDescriptionResovlable", deviceCollection.getDescription());
        
        return "deviceCollectionReport.jsp";
    }
    
    // SELECTED DEVICES POPUP TABLE
    @RequestMapping
    public String selectedDevicesTableForDeviceCollection(ModelMap model, HttpServletRequest request) throws ServletRequestBindingException {
        
        DeviceCollection deviceCollection = this.deviceCollectionFactory.createDeviceCollection(request);
        int totalDeviceCount = (int)deviceCollection.getDeviceCount();
        List<SimpleDevice> devicesToLoad = deviceCollection.getDevices(0, MAX_SELECTED_DEVICES_DISPLAYED);

        return getSelectedDevicesTablemodel(model, devicesToLoad, totalDeviceCount, YukonUserContextUtils.getYukonUserContext(request));
    }
    
    @RequestMapping
    public String selectedDevicesTableForGroupName(ModelMap model, HttpServletRequest request) throws ServletRequestBindingException {
        
        String groupName = ServletRequestUtils.getRequiredStringParameter(request, "groupName");
        DeviceGroup group = deviceGroupService.resolveGroupName(groupName);
        
        int totalDeviceCount = deviceGroupService.getDeviceCount(Collections.singletonList(group));
        Set<SimpleDevice> devicesToLoad = deviceGroupService.getDevices(Collections.singletonList(group), MAX_SELECTED_DEVICES_DISPLAYED);
        
        return getSelectedDevicesTablemodel(model, devicesToLoad, totalDeviceCount, YukonUserContextUtils.getYukonUserContext(request));
        
    }
    
    private String getSelectedDevicesTablemodel(ModelMap model, 
                                                Collection<SimpleDevice> devicesToLoad, 
                                                int totalDeviceCount, 
                                                YukonUserContext context) {
        
        MessageSourceAccessor accessor = resolver.getMessageSourceAccessor(context);
        
        List<DeviceCollectionReportDevice> deviceCollectionReportDevices = paoLoadingService.getDeviceCollectionReportDevices(devicesToLoad);
        Collections.sort(deviceCollectionReportDevices);
        
        List<List<String>> rows = Lists.newArrayList();
        
        List<String> header = Lists.newArrayList();
        header.add(accessor.getMessage("yukon.common.deviceName"));
        header.add(accessor.getMessage("yukon.common.address"));
        header.add(accessor.getMessage("yukon.common.route"));
        rows.add(header);
        
        for (DeviceCollectionReportDevice device : deviceCollectionReportDevices) {
            List<String> row = Lists.newArrayList();
            row.add(device.getName());
            row.add(device.getAddress());
            row.add(device.getRoute());
            rows.add(row);
        }
        
        if (totalDeviceCount > MAX_SELECTED_DEVICES_DISPLAYED) {
            model.addAttribute("resultsLimitedTo", MAX_SELECTED_DEVICES_DISPLAYED);
        }
        model.addAttribute("deviceInfoList", rows);
        
        return "selectedDevicesPopup.jsp";
    }
    
    @RequestMapping
    public String processingExceptionErrorsRefresh(ModelMap model, HttpServletRequest request) throws ServletRequestBindingException {

        String resultsId = ServletRequestUtils.getRequiredStringParameter(request, "resultsId");
        BackgroundProcessResultHolder bulkUpdateOperationResults = recentResultsCache.getResult(resultsId);
        
        model.addAttribute("exceptionRowNumberMap", bulkUpdateOperationResults.getProcessingExceptionRowNumberMap());
        return "processingExceptionErrorsList.jsp";
    }
    
    @RequestMapping
    public void processingExceptionFileDownload(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

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
        
        WebFileUtils.writeToCSV(response, headerRow, fileLines, "ExceptionsFile_" + resultsId + ".csv");
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

}