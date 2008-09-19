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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.bulk.collection.DeviceCollection;
import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.bulk.service.BulkOperationCallbackResults;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.common.util.ReverseList;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.simplereport.ColumnInfo;
import com.cannontech.tools.csv.CSVReader;
import com.cannontech.tools.csv.CSVWriter;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.reports.XmlReportDataUtils;

/**
 * Spring controller class for bulk operations
 */
public class BulkController extends BulkControllerBase {

    private final static int MAX_SELECTED_DEVICES_DISPLAYED = 1000;
    
    private PaoDao paoDao = null;
    private RecentResultsCache<BulkOperationCallbackResults<?>> recentResultsCache = null;
    private MeterDao meterDao = null;
    private YukonUserContextMessageSourceResolver messageSourceResolver = null;
    
    // BULK HOME
    public ModelAndView bulkHome(HttpServletRequest request, HttpServletResponse response) throws ServletException {

        ModelAndView mav = new ModelAndView("bulkHome.jsp");
        
        // INIT RESULT LISTS
        //------------------------------------------------------------------------------------------
        
        // bulk update operations (add both completed and pending to same list)
        List<BulkOperationCallbackResults<?>> bulkUpdateOperationResultsList = new ArrayList<BulkOperationCallbackResults<?>>();
        
        // ADD RESULTS TO LISTS
        // -----------------------------------------------------------------------------------------
        
        // results
        bulkUpdateOperationResultsList.addAll(new ReverseList<BulkOperationCallbackResults<?>>(recentResultsCache.getPending()));
        bulkUpdateOperationResultsList.addAll(new ReverseList<BulkOperationCallbackResults<?>>(recentResultsCache.getCompleted()));
        
        // add lists to mav
        mav.addObject("bulkUpdateOperationResultsList", bulkUpdateOperationResultsList);

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
    
    public ModelAndView deviceCollectionReportXmlData(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
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
        
        XmlReportDataUtils.outputReportData(data, columnInfo, response.getOutputStream());
        
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
        BulkOperationCallbackResults<?> bulkUpdateOperationResults = recentResultsCache.getResult(resultsId);
        
        mav.addObject("exceptionRowNumberMap", bulkUpdateOperationResults.getProcessingExceptionRowNumberMap());
        return mav;
    }
    
    @SuppressWarnings("unchecked")
    public ModelAndView processingExceptionFileDownload(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {


        String resultsId = ServletRequestUtils.getRequiredStringParameter(request, "resultsId");
        BulkOperationCallbackResults<?> bulkUpdateOperationResults = recentResultsCache.getResult(resultsId);
        
        // header row
        InputStreamReader inputStreamReader = new InputStreamReader(bulkUpdateOperationResults.getBulkFileInfo().getFileResource().getInputStream());
        BufferedReader reader = new BufferedReader(inputStreamReader);
        CSVReader csvReader = new CSVReader(reader);
        String[] headerRow = csvReader.readNext();
        csvReader.close();
         
        // failed lines
        List<String[]> fileLines = (List<String[]>)bulkUpdateOperationResults.getProcesingExceptionObjects();
        
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
    
    @Required
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
    
    @Required
    public void setRecentBulkOperationResultsCache(RecentResultsCache<BulkOperationCallbackResults<?>> recentResultsCache) {
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
}
