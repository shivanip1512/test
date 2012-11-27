package com.cannontech.web.bulk;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.common.bulk.callbackResult.BackgroundProcessResultHolder;
import com.cannontech.common.bulk.callbackResult.ImportUpdateCallbackResult;
import com.cannontech.common.bulk.field.BulkFieldColumnHeader;
import com.cannontech.common.bulk.field.BulkFieldService;
import com.cannontech.common.bulk.service.BulkUpdateFileInfo;
import com.cannontech.common.bulk.service.BulkUpdateService;
import com.cannontech.common.bulk.service.ParsedBulkUpdateFileInfo;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.bulk.util.BulkFileUpload;
import com.cannontech.web.bulk.util.BulkFileUploadUtils;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@CheckRoleProperty(YukonRoleProperty.BULK_UPDATE_OPERATION)
public class UpdateController extends MultiActionController {

    private BulkFieldService bulkFieldService = null;
    private BulkUpdateService bulkUpdateService = null;
    private RecentResultsCache<BackgroundProcessResultHolder> recentResultsCache = null;
   
    private Map<String, BulkUpdateFileInfo> bulkUpdateFileInfoMap = new HashMap<String, BulkUpdateFileInfo>();
    
    // UPLOAD
    public ModelAndView upload(HttpServletRequest request, HttpServletResponse response) throws ServletException {

        // direct to upload page
        ModelAndView mav = new ModelAndView("update/updateUpload.jsp");
        
        // column info
        addColumnInfoToMav(mav);
        
        // errors
        String errors = ServletRequestUtils.getStringParameter(request, "fileErrorKeys", "");
        mav.addObject("fileErrorKeysList", StringUtils.split(errors, "||"));
        
        // options checked included? pass along
        Boolean ignoreInvalidCols = ServletRequestUtils.getBooleanParameter(request, "ignoreInvalidCols", true);
        Boolean ignoreInvalidIdentifiers = ServletRequestUtils.getBooleanParameter(request, "ignoreInvalidIdentifiers", false);
        mav.addObject("ignoreInvalidCols", ignoreInvalidCols);
        mav.addObject("ignoreInvalidIdentifiers", ignoreInvalidIdentifiers);
        
        return mav;
    }

    // PARSE
    public ModelAndView parseUpload(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // options 
        Boolean ignoreInvalidCols = ServletRequestUtils.getBooleanParameter(request, "ignoreInvalidCols", false);
        Boolean ignoreInvalidIdentifiers = ServletRequestUtils.getBooleanParameter(request, "ignoreInvalidIdentifiers", false);
        
        // error mav
        ModelAndView errorMav = new ModelAndView("redirect:/bulk/update/upload");
        errorMav.addObject("ignoreInvalidCols", ignoreInvalidCols);
        errorMav.addObject("ignoreInvalidIdentifiers", ignoreInvalidIdentifiers);
        
        // detect file
        BulkFileUpload bulkFileUpload = BulkFileUploadUtils.getBulkFileUpload(request);
        
        if (bulkFileUpload.hasErrors()) {
            errorMav.addObject("fileErrorKeys", StringUtils.join(bulkFileUpload.getErrors(), "||"));
            return errorMav;
        }
        
        // init file info
        FileSystemResource fileResource = new FileSystemResource(bulkFileUpload.getFile());
        BulkUpdateFileInfo bulkUpdateFileInfo = new BulkUpdateFileInfo(fileResource, ignoreInvalidCols, ignoreInvalidIdentifiers);
        
        // save file info
        String fileInfoId = bulkUpdateFileInfo.getId();
        bulkUpdateFileInfoMap.put(fileInfoId, bulkUpdateFileInfo);
        
        // confirm
        ModelAndView mav = new ModelAndView("redirect:/bulk/update/updateConfirm");
        mav.addObject("fileInfoId", fileInfoId);
        
        return mav;
    }
    
   
    
    // CONFIRM
    public ModelAndView updateConfirm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        ModelAndView mav = new ModelAndView("update/updateConfirm.jsp");
        
        // get file info
        String fileInfoId = ServletRequestUtils.getRequiredStringParameter(request, "fileInfoId");
        BulkUpdateFileInfo bulkUpdateFileInfo = bulkUpdateFileInfoMap.get(fileInfoId);
        mav.addObject("bulkUpdateFileInfo", bulkUpdateFileInfo);
        
        ParsedBulkUpdateFileInfo parsedResult = bulkUpdateService.createParsedBulkUpdateFileInfo(bulkUpdateFileInfo);
        mav.addObject("parsedResult", parsedResult);
        
        // has file errors
        if (parsedResult.hasErrors()) {
            
            // options
            ModelAndView errorMav = new ModelAndView("update/updateUpload.jsp");
            errorMav.addObject("ignoreInvalidCols", bulkUpdateFileInfo.isIgnoreInvalidCols());
            errorMav.addObject("ignoreInvalidIdentifiers", bulkUpdateFileInfo.isIgnoreInvalidIdentifiers());
            
            // column info
            addColumnInfoToMav(errorMav);
            
            // errors
            errorMav.addObject("headersErrorResolverList", parsedResult.getErrorResolvers());
            
            return errorMav;
        }
        
        return mav;
    }
    
    // DO UPDATE
    public ModelAndView doUpdate(HttpServletRequest request, HttpServletResponse response) throws ServletException, FileNotFoundException, IOException {
        
        ModelAndView mav = new ModelAndView("redirect:/bulk/update/updateResults");
        
        // open file as csv
        String fileInfoId = ServletRequestUtils.getRequiredStringParameter(request, "fileInfoId");
        BulkUpdateFileInfo bulkUpdateFileInfo = bulkUpdateFileInfoMap.get(fileInfoId);
        ParsedBulkUpdateFileInfo parsedResult = bulkUpdateService.createParsedBulkUpdateFileInfo(bulkUpdateFileInfo);
       
        String resultsId = bulkUpdateService.startBulkUpdate(parsedResult);
        
        mav.addObject("resultsId", resultsId);
        
        return mav;
    }
    
    // VIEW RESULTS
    public ModelAndView updateResults(HttpServletRequest request, HttpServletResponse response) throws ServletException {

        ModelAndView mav = new ModelAndView("update/updateResults.jsp");

        // result info
        String resultsId = ServletRequestUtils.getRequiredStringParameter(request, "resultsId");
        ImportUpdateCallbackResult callbackResult = (ImportUpdateCallbackResult)recentResultsCache.getResult(resultsId);
        
        BulkUpdateFileInfo bulkUpdateFileInfo = (BulkUpdateFileInfo)callbackResult.getBulkFileInfo();
        
        mav.addObject("ignoreInvalidCols", bulkUpdateFileInfo.isIgnoreInvalidCols());
        mav.addObject("ignoreInvalidIdentifiers", bulkUpdateFileInfo.isIgnoreInvalidIdentifiers());
        mav.addObject("callbackResult", callbackResult);

        return mav;
    }
    
    private void addColumnInfoToMav(ModelAndView mav) {
        
        // column info
        Set<BulkFieldColumnHeader> updateableFields = bulkFieldService.getUpdateableBulkFieldColumnHeaders();
        Set<BulkFieldColumnHeader> identifierFields = bulkFieldService.getUpdateIdentifierBulkFieldColumnHeaders();
        
        Set<BulkFieldColumnHeader> allFields = new HashSet<BulkFieldColumnHeader>();
        allFields.addAll(updateableFields);
        allFields.addAll(identifierFields);
        
        mav.addObject("allFields", allFields);
        Map<BulkFieldColumnHeader, Boolean> identifierFieldsMap = ServletUtil.convertSetToMap(identifierFields);
        mav.addObject("identifierFieldsMap", identifierFieldsMap);
    }

    @Required
    public void setBulkFieldService(BulkFieldService bulkFieldService) {
        this.bulkFieldService = bulkFieldService;
    }
    
    @Required
    public void setRecentResultsCache(RecentResultsCache<BackgroundProcessResultHolder> recentResultsCache) {
        this.recentResultsCache = recentResultsCache;
    }
    
    @Required
    public void setBulkUpdateService(BulkUpdateService bulkUpdateService) {
        this.bulkUpdateService = bulkUpdateService;
    }

}
