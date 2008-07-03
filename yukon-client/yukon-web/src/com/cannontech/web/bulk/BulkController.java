package com.cannontech.web.bulk;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.common.bulk.collection.DeviceCollection;
import com.cannontech.common.bulk.service.BulkOperationCallbackResults;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroupHierarchy;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.groups.service.NonHiddenDeviceGroupPredicate;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.common.util.ReverseList;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.tools.csv.CSVWriter;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.group.DeviceGroupTreeUtils;
import com.cannontech.web.util.ExtTreeNode;

/**
 * Spring controller class for bulk operations
 */
public class BulkController extends BulkControllerBase {

    private PaoDao paoDao = null;
    private DeviceGroupService deviceGroupService = null;
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
    
    // SELECT DEVICES BY GROUP TREE JSON
    public ModelAndView selectDevicesByGroupTreeJson(HttpServletRequest request, HttpServletResponse response) throws ServletException, Exception {
        
        // make a device group hierarchy starting at root, only modifiable groups
        DeviceGroup rootGroup = deviceGroupService.getRootGroup();
        DeviceGroupHierarchy groupHierarchy = deviceGroupService.getDeviceGroupHierarchy(rootGroup, new NonHiddenDeviceGroupPredicate());
        
        // make tree json
        ExtTreeNode root = makeDeviceGroupExtTree(groupHierarchy, "Groups");
        
        // make a list containing maps which represents each group node
        List<Map<String, Object>> groupList = new ArrayList<Map<String, Object>>();
        for (ExtTreeNode n : root.getChildren()) {
            groupList.add(n.toMap());
        }
        
        // convert list to JSON array
        JSONArray jsonArray = new JSONArray(groupList);
        
        // write JSON to response
        PrintWriter writer = response.getWriter();
        String responseJsonStr = jsonArray.toString();
        writer.write(responseJsonStr);
        
        return null;
    }
    
    private static ExtTreeNode makeDeviceGroupExtTree(DeviceGroupHierarchy dgh, String rootName) throws Exception{
        
        DeviceGroup deviceGroup = dgh.getGroup();
        
        // setup node basics
        ExtTreeNode node = new ExtTreeNode();
        String nodeId = deviceGroup.getFullName().replaceAll("[^a-zA-Z0-9]","");;
        DeviceGroupTreeUtils.setupNodeAttributes(node, deviceGroup, nodeId, rootName);
        
        // link info
        node.setAttribute("href", "javascript:void(0);");
        
        Map<String, String> info = new HashMap<String, String>();
        info.put("groupName", deviceGroup.getFullName());
        node.setAttribute("info", info);
        
        // recursively add child groups
        for (DeviceGroupHierarchy d : dgh.getChildGroupList()) {
            node.addChild(makeDeviceGroupExtTree(d, null));
        }
        
        // leaf?
        DeviceGroupTreeUtils.setLeaf(node);
        
        return node;
    }
    
    @Required
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
    
    @Required
    public void setDeviceGroupService(DeviceGroupService deviceGroupService) {
        this.deviceGroupService = deviceGroupService;
    }
    
    @Required
    public void setRecentBulkOperationResultsCache(RecentResultsCache<BulkOperationCallbackResults<?>> recentResultsCache) {
        this.recentResultsCache = recentResultsCache;
    }

    
}
