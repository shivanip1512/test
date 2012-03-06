package com.cannontech.web.bulk;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
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
import com.cannontech.common.bulk.service.BulkImportFileInfo;
import com.cannontech.common.bulk.service.BulkImportMethod;
import com.cannontech.common.bulk.service.BulkImportService;
import com.cannontech.common.bulk.service.BulkImportType;
import com.cannontech.common.bulk.service.ParsedBulkImportFileInfo;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.web.bulk.util.BulkFileUpload;
import com.cannontech.web.bulk.util.BulkFileUploadUtils;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@CheckRoleProperty(YukonRoleProperty.BULK_IMPORT_OPERATION)
public class ImportController extends MultiActionController {

    private BulkImportService bulkImportService = null;
    private RecentResultsCache<BackgroundProcessResultHolder> recentResultsCache = null;
    
    private List<BulkImportMethod> importMethods = null;
    private Map<String, BulkImportFileInfo> bulkImportFileInfoMap = new HashMap<String, BulkImportFileInfo>();
    
    // UPLOAD
    public ModelAndView upload(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        
        String importTypeSelector = ServletRequestUtils.getStringParameter(request, "importTypeSelector");

        ModelAndView mav = new ModelAndView("import/importUpload.jsp");
        
        // template method columns
        mav.addObject("importMethods", importMethods);
        mav.addObject("methodUpdateableFieldsMap", getMethodUpdateabledFieldsMap());
        
        // errors
        String errors = ServletRequestUtils.getStringParameter(request, "fileErrorKeys", "");
        mav.addObject("fileErrorKeysList", StringUtils.split(errors, "||"));
        
        // options
        Boolean ignoreInvalidCols = ServletRequestUtils.getBooleanParameter(request, "ignoreInvalidCols", true);
        mav.addObject("ignoreInvalidCols", ignoreInvalidCols);
        
        mav.addObject("bulkImportTypes", BulkImportType.values());
        
        mav.addObject("importTypeSelector", importTypeSelector);
        
        return mav;
    }
    
    private Map<BulkImportMethod, Set<BulkFieldColumnHeader>> getMethodUpdateabledFieldsMap() {
        
        Map<BulkImportMethod, Set<BulkFieldColumnHeader>> methodUpdateableFieldsMap = new HashMap<BulkImportMethod, Set<BulkFieldColumnHeader>>();
        for (BulkImportMethod method : importMethods) {
          
            methodUpdateableFieldsMap.put(method, method.getOptionalColumns());
        }
        
        return methodUpdateableFieldsMap;
    }
    
    
    // PARSE
    public ModelAndView parseUpload(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        String importTypeSelector = ServletRequestUtils.getStringParameter(request, "importTypeSelector");
        
        // options 
        Boolean ignoreInvalidCols = ServletRequestUtils.getBooleanParameter(request, "ignoreInvalidCols", false);
        
        // error mav
        ModelAndView errorMav = new ModelAndView("redirect:/spring/bulk/import/upload");
        errorMav.addObject("ignoreInvalidCols", ignoreInvalidCols);
        
        // detect file
        BulkFileUpload bulkFileUpload = BulkFileUploadUtils.getBulkFileUpload(request);
        
        if (bulkFileUpload.hasErrors()) {
            
            errorMav.addObject("importTypeSelector", importTypeSelector);
            errorMav.addObject("fileErrorKeys", StringUtils.join(bulkFileUpload.getErrors(), "||"));
            return errorMav;
        }
        
        // init file info
        FileSystemResource fileResource = new FileSystemResource(bulkFileUpload.getFile());
        BulkImportFileInfo bulkImportFileInfo = new BulkImportFileInfo(fileResource, ignoreInvalidCols);
        
        // save file info
        String fileInfoId = bulkImportFileInfo.getId();
        bulkImportFileInfoMap.put(fileInfoId, bulkImportFileInfo);
            
        // confirm
        ModelAndView mav = new ModelAndView("redirect:/spring/bulk/import/importConfirm");
        mav.addObject("fileInfoId", fileInfoId);
        mav.addObject("bulkImportType", importTypeSelector);
        
        return mav;
    }
    
    // CONFIRM
    public ModelAndView importConfirm(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        
        ModelAndView mav = new ModelAndView("import/importConfirm.jsp");
        
 
        BulkImportType bulkImportType = BulkImportType.valueOf(ServletRequestUtils.getStringParameter(request, "bulkImportType"));
        
        // get file info
        String fileInfoId = ServletRequestUtils.getRequiredStringParameter(request, "fileInfoId");
        BulkImportFileInfo bulkImportFileInfo = bulkImportFileInfoMap.get(fileInfoId);
        
        ParsedBulkImportFileInfo parsedResult = bulkImportService.createParsedBulkImportFileInfo(bulkImportFileInfo, bulkImportType);
        mav.addObject("parsedResult", parsedResult);
        mav.addObject("bulkImportType", bulkImportType);

        // header errors
        if (parsedResult.hasErrors()) {
            
            ModelAndView errorMav = new ModelAndView("import/importUpload.jsp");
            errorMav.addObject("ignoreInvalidCols", bulkImportFileInfo.isIgnoreInvalidCols());
            errorMav.addObject("importTypeSelector", bulkImportType);
            errorMav.addObject("importMethods", importMethods);
            errorMav.addObject("methodUpdateableFieldsMap", getMethodUpdateabledFieldsMap());
            errorMav.addObject("headersErrorResolverList", parsedResult.getErrorResolvers());
            errorMav.addObject("bulkImportTypes", BulkImportType.values());
            
            return errorMav;
        }
        
        return mav;
    }
    
    // DO IMPORT
    public ModelAndView doImport(HttpServletRequest request, HttpServletResponse response) throws ServletException, FileNotFoundException, IOException {
        
        ModelAndView mav = new ModelAndView("redirect:/spring/bulk/import/importResults");
        
        BulkImportType bulkImportType = BulkImportType.valueOf(ServletRequestUtils.getStringParameter(request, "bulkImportType"));
     
        // open file as csv
        String fileInfoId = ServletRequestUtils.getRequiredStringParameter(request, "fileInfoId");
        BulkImportFileInfo bulkImportFileInfo = bulkImportFileInfoMap.get(fileInfoId);
        ParsedBulkImportFileInfo parsedResult = bulkImportService.createParsedBulkImportFileInfo(bulkImportFileInfo, bulkImportType);
        String resultsId = bulkImportService.startBulkImport(parsedResult);
        
        mav.addObject("resultsId", resultsId);
        
        return mav;
    }
    
    // VIEW RESULTS
    public ModelAndView importResults(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        
        ModelAndView mav = new ModelAndView("import/importResults.jsp");
        
        // result info
        String resultsId = ServletRequestUtils.getRequiredStringParameter(request, "resultsId");
        ImportUpdateCallbackResult callbackResult = (ImportUpdateCallbackResult)recentResultsCache.getResult(resultsId);
        
        BulkImportFileInfo bulkImportFileInfo = (BulkImportFileInfo)callbackResult.getBulkFileInfo();
        
        mav.addObject("ignoreInvalidCols", bulkImportFileInfo.isIgnoreInvalidCols());
        mav.addObject("callbackResult", callbackResult);
        
        return mav;
    }

    @Required
    public void setRecentResultsCache(RecentResultsCache<BackgroundProcessResultHolder> recentResultsCache) {
        this.recentResultsCache = recentResultsCache;
    }

    public void setBulkImportService(BulkImportService bulkImportService) {
        this.bulkImportService = bulkImportService;
    }

    @Required
    public void setImportMethods(List<BulkImportMethod> importMethods) {
        this.importMethods = importMethods;
    }
}