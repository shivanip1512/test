package com.cannontech.web.bulk;

import java.io.BufferedWriter;
import java.io.IOException;
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

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.common.bulk.collection.DeviceCollection;
import com.cannontech.common.bulk.service.BulkOperationCallbackResults;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.common.util.ReverseList;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.tools.csv.CSVWriter;
import com.cannontech.util.ServletUtil;

/**
 * Spring controller class for bulk operations
 */
public class BulkController extends BulkControllerBase {

    private PaoDao paoDao = null;
    private RecentResultsCache<BulkOperationCallbackResults<?>> recentResultsCache = null;
    
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

        ModelAndView mav = new ModelAndView("collectionActions.jsp");
        
        this.addDeviceCollectionToModel(mav, request);
        
        return mav;
    }
    
    
    // SELECTED DEVICES POPUP TBALE
    public ModelAndView selectedDevicesTable(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        
        ModelAndView mav = new ModelAndView("selectedDevicesPopup.jsp");
        
        DeviceCollection deviceCollection = this.deviceCollectionFactory.createDeviceCollection(request);
        
        List<Map<String, Object>> deviceInfoList = new ArrayList<Map<String, Object>>();
        for (YukonDevice device : deviceCollection.getDeviceList()) {
            
            Map<String, Object> deviceInfo = new LinkedHashMap<String, Object>();
            
            LiteYukonPAObject devicePaoObj = paoDao.getLiteYukonPAO(device.getDeviceId());
            deviceInfo.put("Device Name", devicePaoObj.getPaoName());
            deviceInfo.put("Address", devicePaoObj.getAddress());
            deviceInfo.put("Route", paoDao.getRouteNameForRouteId(devicePaoObj.getRouteID()));
            
            
            deviceInfoList.add(deviceInfo);
            
        }
        
        mav.addObject("deviceInfoList", deviceInfoList);
        
        return mav;
        
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
        
        List<String[]> fileLines = (List<String[]>)bulkUpdateOperationResults.getProcesingExceptionObjects();
        
        response.setContentType("text/csv");
        response.setHeader("Content-Type", "application/force-download");
        response.setHeader("Content-Disposition","attachment; filename=\"" + ServletUtil.makeWindowsSafeFileName("ExceptionsFile_" + resultsId) + ".csv\"");
        OutputStream outputStream = response.getOutputStream();
        
        Writer writer = new BufferedWriter(new OutputStreamWriter(outputStream));
        CSVWriter csvWriter = new CSVWriter(writer);
        
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

    
}
