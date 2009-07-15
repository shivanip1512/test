package com.cannontech.web.bulk;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.bulk.callbackResult.BackgroundProcessResultHolder;
import com.cannontech.common.bulk.callbackResult.BackgroundProcessTypeEnum;
import com.cannontech.common.bulk.callbackResult.ImportUpdateCallbackResult;
import com.cannontech.common.bulk.collection.DeviceCollection;
import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.MappingList;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.common.util.ReverseList;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.simplereport.ColumnInfo;
import com.cannontech.tools.csv.CSVReader;
import com.cannontech.tools.csv.CSVWriter;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.reports.JsonReportDataUtils;

/**
 * Spring controller class for bulk operations
 */
public class BulkController extends BulkControllerBase {

    private final static int MAX_SELECTED_DEVICES_DISPLAYED = 1000;
    
    private PaoDao paoDao = null;
    private RecentResultsCache<BackgroundProcessResultHolder> recentResultsCache = null;
    private MeterDao meterDao = null;
    private YukonUserContextMessageSourceResolver messageSourceResolver = null;
    private RolePropertyDao rolePropertyDao;
    
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
        
        try {
        
            if (request.getMethod().equals("POST")) {
                // if we got here from a post (like the file upload), let's redirect
                mav = new ModelAndView("redirect:/spring/bulk/collectionActions");
                Map<String, String> collectionParameters = this.getDeviceCollection(request).getCollectionParameters();
                mav.addAllObjects(collectionParameters);
            } else {
                mav = new ModelAndView("collectionActions.jsp");
                this.addDeviceCollectionToModel(mav, request);
            }
        } catch (ObjectMappingException e) {
            
            mav = new ModelAndView("redirect:/spring/bulk/deviceSelection");
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
    
    // SELECTED DEVICES POPUP TBALE
    public ModelAndView selectedDevicesTable(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        
        ModelAndView mav = new ModelAndView("selectedDevicesPopup.jsp");
        
        DeviceCollection deviceCollection = this.deviceCollectionFactory.createDeviceCollection(request);
        
        List<Map<String, Object>> deviceInfoList = new ArrayList<Map<String, Object>>();
        for (YukonDevice device : deviceCollection.getDevices(0, MAX_SELECTED_DEVICES_DISPLAYED)) {
            
            Map<String, Object> deviceInfo = new LinkedHashMap<String, Object>();
            
            LiteYukonPAObject devicePaoObj = paoDao.getLiteYukonPAO(device.getDeviceId());
            deviceInfo.put("Device Name", devicePaoObj.getPaoName());
            deviceInfo.put("Address", devicePaoObj.getAddress());
            deviceInfo.put("Route", paoDao.getRouteNameForRouteId(devicePaoObj.getRouteID()));
            
            deviceInfoList.add(deviceInfo);
        }
        
        if (deviceCollection.getDeviceCount() > MAX_SELECTED_DEVICES_DISPLAYED) {
            mav.addObject("resultsLimitedTo", MAX_SELECTED_DEVICES_DISPLAYED);
        }
        mav.addObject("deviceInfoList", deviceInfoList);
        
        return mav;
        
    }
    
    // DEVICE COLLECTION REPORT
    public ModelAndView deviceCollectionReport(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        
        ModelAndView mav = new ModelAndView("deviceCollectionReport.jsp");
        DeviceCollection deviceCollection = this.deviceCollectionFactory.createDeviceCollection(request);
        mav.addObject("deviceCollection", deviceCollection);
        
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        List<ColumnInfo> columnInfo = getDeviceCollectionReportColumnInfo(userContext);
        mav.addObject("columnInfo", columnInfo);
        
        List<Meter> meterList = new ArrayList<Meter>();
        for (YukonDevice device : deviceCollection.getDeviceList()) {
            Meter meter = meterDao.getForId(device.getDeviceId());
            meterList.add(meter);
        }
        mav.addObject("meterList", meterList);
        
        return mav;
    }
    
    public ModelAndView deviceCollectionReportJsonData(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        DeviceCollection deviceCollection = this.deviceCollectionFactory.createDeviceCollection(request);
        
        List<ColumnInfo> columnInfo = getDeviceCollectionReportColumnInfo(userContext);
        
        List<List<String>> data = new ArrayList<List<String>>();
        for (YukonDevice device : deviceCollection.getDeviceList()) {

            Meter meter = meterDao.getForId(device.getDeviceId());
            
            List<String> cols = new ArrayList<String>();
            cols.add(meter.getName());
            cols.add(meter.getMeterNumber());
            cols.add(meter.getTypeStr());
            cols.add(meter.getAddress());
            cols.add(meter.getRoute());
            data.add(cols);
        }
        
        JsonReportDataUtils.outputReportData(data, columnInfo, response.getOutputStream());
        
        return null;
        
    }
    
    private List<ColumnInfo> getDeviceCollectionReportColumnInfo(YukonUserContext userContext) {
        
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        
        String keyBase = "yukon.common.device.bulk.collectionActions.deviceCollectionReport.headers.";
        List<ColumnInfo> columnInfo = new ArrayList<ColumnInfo>();
        columnInfo.add(new ColumnInfo(messageSourceAccessor.getMessage(keyBase + "deviceName"), 200, null));
        columnInfo.add(new ColumnInfo(messageSourceAccessor.getMessage(keyBase + "meterNumber"), 150, null));
        columnInfo.add(new ColumnInfo(messageSourceAccessor.getMessage(keyBase + "deviceType"), 150, null));
        columnInfo.add(new ColumnInfo(messageSourceAccessor.getMessage(keyBase + "address"), 150, null));
        columnInfo.add(new ColumnInfo(messageSourceAccessor.getMessage(keyBase + "route"), 150, null));
        
        return columnInfo;
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
        	
            return new BulkOperationDisplayableResult(from,
                                                      this.hasBulkImportRP,
                                                      this.hasBulkUpdateRP,
                                                      this.hasMassChangeRP,
                                                      this.hasMassDeleteRP,
                                                      this.hasAddRemovePointsRP);
        }
    }
    
    public class BulkOperationDisplayableResult {

        private BackgroundProcessResultHolder result;
        private boolean hasBulkImportRP = false;
        private boolean hasBulkUpdateRP = false;
        private boolean hasMassChangeRP = false;
        private boolean hasMassDeleteRP = false;
        private boolean hasAddRemovePointsRP = false;

        public BulkOperationDisplayableResult(
        		BackgroundProcessResultHolder result,
                boolean hasBulkImportRP, boolean hasBulkUpdateRP,
                boolean hasMassChangeRP, boolean hasMassDeleteRP,
                boolean hasAddRemovePointsRP) {
            this.result = result;
            this.hasBulkImportRP = hasBulkImportRP;
            this.hasBulkUpdateRP = hasBulkUpdateRP;
            this.hasMassChangeRP = hasMassChangeRP;
            this.hasMassDeleteRP = hasMassDeleteRP;
            this.hasAddRemovePointsRP = hasAddRemovePointsRP;
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
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
    
    @Required
    public void setRecentResultsCache(RecentResultsCache<BackgroundProcessResultHolder> recentResultsCache) {
        this.recentResultsCache = recentResultsCache;
    }

    @Autowired
    public void setMeterDao(MeterDao meterDao) {
        this.meterDao = meterDao;
    }
    
    @Autowired
    public void setMessageSourceResolver(
            YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }
    
    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
		this.rolePropertyDao = rolePropertyDao;
	}
}
