package com.cannontech.web.bulk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.input.BOMInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.cannontech.common.bulk.callbackResult.BackgroundProcessResultHolder;
import com.cannontech.common.bulk.callbackResult.BackgroundProcessTypeEnum;
import com.cannontech.common.bulk.callbackResult.BulkFieldBackgroupProcessResultHolder;
import com.cannontech.common.bulk.callbackResult.ImportUpdateCallbackResult;
import com.cannontech.common.bulk.collection.device.DeviceCollectionFactory;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.field.BulkFieldColumnHeader;
import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;
import com.cannontech.common.util.MappingList;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.common.util.ReverseList;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.pao.PaoPopupHelper;
import com.cannontech.web.util.WebFileUtils;
import com.google.common.collect.Lists;
import com.opencsv.CSVReader;

/**
 * Handles request directly off the /bulk/* url.
 * Other controllers handle specific sections like the {@link AddPointsController} (/bulk/addPoints/*)
 */
@Controller
@RequestMapping("/*")
public class BulkController {

    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private TemporaryDeviceGroupService temporaryDeviceGroupService;
    @Autowired private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    @Autowired private DeviceCollectionFactory deviceCollectionFactory;
    @Autowired private DeviceGroupCollectionHelper deviceGroupCollectionHelper; 
    @Autowired private PaoPopupHelper paoPopupHelper;
    @Resource(name="recentResultsCache") private RecentResultsCache<BackgroundProcessResultHolder> recentResultsCache;
    
    // BULK RESULTS
    @RequestMapping("recentResults")
    public String recentResults(ModelMap model, LiteYukonUser user) {

        // INIT RESULT LISTS
        //------------------------------------------------------------------------------------------
        
        // bulk update operations (add both completed and pending to same list)
        List<BackgroundProcessResultHolder> rawResultsList = new ArrayList<>();
        
        // ADD RESULTS TO LISTS
        // -----------------------------------------------------------------------------------------
        
        boolean hasBulkImportRP = rolePropertyDao.checkProperty(YukonRoleProperty.BULK_IMPORT_OPERATION, user);
        boolean hasBulkUpdateRP = rolePropertyDao.checkProperty(YukonRoleProperty.BULK_UPDATE_OPERATION, user);
        boolean hasMassChangeRP = rolePropertyDao.checkProperty(YukonRoleProperty.MASS_CHANGE, user);
        boolean hasMassDeleteRP = rolePropertyDao.checkProperty(YukonRoleProperty.MASS_DELETE, user);
        boolean hasAddRemovePointsRP = rolePropertyDao.checkProperty(YukonRoleProperty.ADD_REMOVE_POINTS, user);
        
        // results
        rawResultsList.addAll(new ReverseList<>(recentResultsCache.getPending()));
        rawResultsList.addAll(new ReverseList<>(recentResultsCache.getCompleted()));
        
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
        
        return "bulkResults.jsp";
    }
    
    // COLLECTION ACTIONS
    @RequestMapping("collectionActions")
    public String collectionActions(ModelMap model, HttpServletRequest request,
            @RequestParam(defaultValue = "false") boolean isFileUpload, FlashScope flashScope)
            throws ServletRequestBindingException {
        
        DeviceCollection collection = deviceCollectionFactory.createDeviceCollection(request);
        model.addAttribute("deviceCollection", collection);
        return "../collectionActions/collectionActionsHome.jsp";

    }
    
    // DEVICE COLLECTION REPORT
    @RequestMapping("deviceCollectionReport")
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
    @RequestMapping("selectedDevicesTableForDeviceCollection")
    public String selectedDevicesTableForDeviceCollection(HttpServletRequest request, ModelMap model, YukonUserContext context) 
            throws ServletRequestBindingException {

        DeviceCollection deviceCollection = this.deviceCollectionFactory.createDeviceCollection(request);
        paoPopupHelper.buildPopupModel(deviceCollection, model, context);
        return "selectedDevicesPopup.jsp";
    }

    @RequestMapping("selectedDevicesTableForGroupName")
    public String selectedDevicesTableForGroupName(HttpServletRequest request, ModelMap model, YukonUserContext context) 
            throws ServletRequestBindingException {

        String groupName = ServletRequestUtils.getRequiredStringParameter(request, "groupName");
        DeviceGroup group = deviceGroupService.resolveGroupName(groupName);
        DeviceCollection collection = deviceGroupCollectionHelper.buildDeviceCollection(group);

        paoPopupHelper.buildPopupModel(collection, model, context);
        return "selectedDevicesPopup.jsp";
    }

    @RequestMapping("processing-errors")
    public String processingErrors(ModelMap model, String resultsId, boolean isFileUpload) {
        
        BackgroundProcessResultHolder bulkUpdateOperationResults = recentResultsCache.getResult(resultsId);
        model.addAttribute("exceptionRowNumberMap", bulkUpdateOperationResults.getProcessingExceptionRowNumberMap());
        model.addAttribute("isFileUpload", isFileUpload);
        return "processingExceptionErrorsList.jsp";
    }
    
    @RequestMapping("processingExceptionFileDownload")
    public void processingExceptionFileDownload(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        String resultsId = ServletRequestUtils.getRequiredStringParameter(request, "resultsId");
        BackgroundProcessResultHolder backGroundCallback = recentResultsCache.getResult(resultsId);
        
        ImportUpdateCallbackResult callback = (ImportUpdateCallbackResult)backGroundCallback;
        
        // header row
        InputStream inputStream = callback.getBulkFileInfo().getFileResource().getInputStream();
        BOMInputStream bomInputStream = new BOMInputStream(inputStream, ByteOrderMark.UTF_8, ByteOrderMark.UTF_16LE,
                ByteOrderMark.UTF_16BE, ByteOrderMark.UTF_32LE, ByteOrderMark.UTF_32BE);
        InputStreamReader inputStreamReader = new InputStreamReader(bomInputStream);
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

        @Override
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
            			            || (this.hasAddRemovePointsRP && (opType.equals(BackgroundProcessTypeEnum.ADD_POINTS) || opType.equals(BackgroundProcessTypeEnum.REMOVE_POINTS)))
                                    || opType == BackgroundProcessTypeEnum.DATA_STREAMING);
            return detailViewable;
        }
    }
    

    @RequestMapping(value="downloadResult", method=RequestMethod.POST)
    public String downloadResult(@RequestParam(value = "deviceErrors", required = false) Set<String> errors,
            @RequestParam(value = "uploadFileName", required = false) String errorFileName,
            @RequestParam(value = "header", required = false) String header,
            HttpServletResponse response)
            throws ServletRequestBindingException, IOException {
        errorFileName = FilenameUtils.removeExtension(errorFileName) + "_errors.csv";
        buildCsv(errors, header, errorFileName, response);

        return null;
    }

    private void buildCsv(Set<String> errors, String header, String errorFileName, HttpServletResponse response)
            throws IOException {
 
        String[] headerRow = null;
        if(header != null){
            headerRow = new String[] { header };
        }
        List<String[]> dataRows = Lists.newArrayList();

        for (String error : errors) {
            dataRows.add(new String[] { error });
        }
        // write out the file
        WebFileUtils.writeToCSV(response, headerRow, dataRows, errorFileName);
    }
}