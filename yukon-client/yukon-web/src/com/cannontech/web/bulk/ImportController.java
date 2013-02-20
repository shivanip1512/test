package com.cannontech.web.bulk;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;

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
@Controller
@RequestMapping("import/*")
public class ImportController {

    @Autowired private BulkImportService bulkImportService;
    @Autowired private List<BulkImportMethod> importMethods;
    @Resource(name="recentResultsCache") private RecentResultsCache<BackgroundProcessResultHolder> recentResultsCache;
    
    private Map<String, BulkImportFileInfo> bulkImportFileInfoMap = new ConcurrentHashMap<>();
    
    // UPLOAD
    @RequestMapping
    public String upload(ModelMap model, HttpServletRequest request) throws ServletRequestBindingException {
        
        String importTypeSelector = ServletRequestUtils.getStringParameter(request, "importTypeSelector");
        
        // template method columns
        model.addAttribute("importMethods", importMethods);
        model.addAttribute("methodUpdateableFieldsMap", getMethodUpdateabledFieldsMap());
        
        // errors
        String errors = ServletRequestUtils.getStringParameter(request, "fileErrorKeys", "");
        model.addAttribute("fileErrorKeysList", StringUtils.split(errors, "||"));
        
        // options
        Boolean ignoreInvalidCols = ServletRequestUtils.getBooleanParameter(request, "ignoreInvalidCols", true);
        model.addAttribute("ignoreInvalidCols", ignoreInvalidCols);
        
        model.addAttribute("bulkImportTypes", BulkImportType.values());
        
        model.addAttribute("importTypeSelector", importTypeSelector);
        
        return "import/importUpload.jsp";
    }
    
    private Map<BulkImportMethod, Set<BulkFieldColumnHeader>> getMethodUpdateabledFieldsMap() {
        
        Map<BulkImportMethod, Set<BulkFieldColumnHeader>> methodUpdateableFieldsMap = new HashMap<BulkImportMethod, Set<BulkFieldColumnHeader>>();
        for (BulkImportMethod method : importMethods) {
          
            methodUpdateableFieldsMap.put(method, method.getOptionalColumns());
        }
        
        return methodUpdateableFieldsMap;
    }
    
    
    // PARSE
    @RequestMapping
    public String parseUpload(ModelMap model, HttpServletRequest request) throws ServletRequestBindingException {
        
        String importTypeSelector = ServletRequestUtils.getStringParameter(request, "importTypeSelector");
        
        // options 
        Boolean ignoreInvalidCols = ServletRequestUtils.getBooleanParameter(request, "ignoreInvalidCols", false);
        
        // detect file
        BulkFileUpload bulkFileUpload = BulkFileUploadUtils.getBulkFileUpload(request);
        
        if (bulkFileUpload.hasErrors()) {
            // error mav
            model.addAttribute("ignoreInvalidCols", ignoreInvalidCols);
            model.addAttribute("importTypeSelector", importTypeSelector);
            model.addAttribute("fileErrorKeys", StringUtils.join(bulkFileUpload.getErrors(), "||"));
            return "redirect:/bulk/import/upload";
        }
        
        // init file info
        FileSystemResource fileResource = new FileSystemResource(bulkFileUpload.getFile());
        BulkImportFileInfo bulkImportFileInfo = new BulkImportFileInfo(fileResource, ignoreInvalidCols);
        
        // save file info
        String fileInfoId = bulkImportFileInfo.getId();
        bulkImportFileInfoMap.put(fileInfoId, bulkImportFileInfo);
            
        // confirm
        model.addAttribute("fileInfoId", fileInfoId);
        model.addAttribute("bulkImportType", importTypeSelector);
        
        return "redirect:/bulk/import/importConfirm";
    }
    
    // CONFIRM
    @RequestMapping
    public String importConfirm(ModelMap model, HttpServletRequest request) throws ServletRequestBindingException {
        
        BulkImportType bulkImportType = BulkImportType.valueOf(ServletRequestUtils.getStringParameter(request, "bulkImportType"));
        
        // get file info
        String fileInfoId = ServletRequestUtils.getRequiredStringParameter(request, "fileInfoId");
        BulkImportFileInfo bulkImportFileInfo = bulkImportFileInfoMap.get(fileInfoId);
        
        ParsedBulkImportFileInfo parsedResult = bulkImportService.createParsedBulkImportFileInfo(bulkImportFileInfo, bulkImportType);
        model.addAttribute("parsedResult", parsedResult);
        model.addAttribute("bulkImportType", bulkImportType);

        // header errors
        if (parsedResult.hasErrors()) {
            
            model.addAttribute("ignoreInvalidCols", bulkImportFileInfo.isIgnoreInvalidCols());
            model.addAttribute("importTypeSelector", bulkImportType);
            model.addAttribute("importMethods", importMethods);
            model.addAttribute("methodUpdateableFieldsMap", getMethodUpdateabledFieldsMap());
            model.addAttribute("headersErrorResolverList", parsedResult.getErrorResolvers());
            model.addAttribute("bulkImportTypes", BulkImportType.values());
            
            return "import/importUpload.jsp";
        }
        
        return "import/importConfirm.jsp";
    }
    
    // DO IMPORT
    @RequestMapping
    public String doImport(ModelMap model, HttpServletRequest request) throws ServletRequestBindingException, IOException {
        
        BulkImportType bulkImportType = BulkImportType.valueOf(ServletRequestUtils.getStringParameter(request, "bulkImportType"));
     
        // open file as csv
        String fileInfoId = ServletRequestUtils.getRequiredStringParameter(request, "fileInfoId");
        BulkImportFileInfo bulkImportFileInfo = bulkImportFileInfoMap.get(fileInfoId);
        ParsedBulkImportFileInfo parsedResult = bulkImportService.createParsedBulkImportFileInfo(bulkImportFileInfo, bulkImportType);
        String resultsId = bulkImportService.startBulkImport(parsedResult);
        
        model.addAttribute("resultsId", resultsId);
        
        return "redirect:/bulk/import/importResults";
    }
    
    // VIEW RESULTS
    @RequestMapping
    public String importResults(ModelMap model, HttpServletRequest request) throws ServletRequestBindingException {
        
        // result info
        String resultsId = ServletRequestUtils.getRequiredStringParameter(request, "resultsId");
        ImportUpdateCallbackResult callbackResult = (ImportUpdateCallbackResult)recentResultsCache.getResult(resultsId);
        
        BulkImportFileInfo bulkImportFileInfo = (BulkImportFileInfo)callbackResult.getBulkFileInfo();
        
        model.addAttribute("ignoreInvalidCols", bulkImportFileInfo.isIgnoreInvalidCols());
        model.addAttribute("callbackResult", callbackResult);
        
        return "import/importResults.jsp";
    }
    
}