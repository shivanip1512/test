package com.cannontech.web.bulk;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.bulk.callbackResult.BackgroundProcessResultHolder;
import com.cannontech.common.bulk.callbackResult.ImportUpdateCallbackResult;
import com.cannontech.common.bulk.field.BulkFieldColumnHeader;
import com.cannontech.common.bulk.service.BulkImportFileInfo;
import com.cannontech.common.bulk.service.BulkImportMethod;
import com.cannontech.common.bulk.service.BulkImportService;
import com.cannontech.common.bulk.service.BulkImportType;
import com.cannontech.common.bulk.service.ParsedBulkImportFileInfo;
import com.cannontech.common.events.loggers.ToolsEventLogService;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.bulk.util.BulkFileUpload;
import com.cannontech.web.bulk.util.BulkFileUploadUtils;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@CheckRoleProperty(YukonRoleProperty.BULK_IMPORT_OPERATION)
@Controller
@RequestMapping("import/*")
public class ImportController {

    @Autowired private BulkImportService bulkImportService;
    @Autowired private List<BulkImportMethod> importMethods;
    @Autowired private ToolsEventLogService toolsEventLogService;
    @Resource(name = "recentResultsCache") private RecentResultsCache<BackgroundProcessResultHolder> recentResultsCache;

    private Map<String, BulkImportFileInfo> bulkImportFileInfoMap = new ConcurrentHashMap<>();
    
    @PostConstruct
    public void init() {
        // Order import methods by type (MCT|RFN) alphabetically, then by name (TEMPLATE|DEVICE) reverse alphabetically.
        Collections.sort(importMethods, new Comparator<BulkImportMethod>() {
            @Override
            public int compare(BulkImportMethod o1, BulkImportMethod o2) {
                int cmp = o1.getType().name().compareTo(o2.getType().name());
                if (cmp == 0) {
                    cmp = o2.getName().compareTo(o1.getName());
                }
                return cmp;
            }
        });
    }
    
    // UPLOAD
    @RequestMapping(value="upload", method=RequestMethod.GET)
    public String upload(ModelMap model, HttpServletRequest request) {
        
        // method columns
        addColumnInfoToModel(model);
        
        model.addAttribute("bulkImportTypes", BulkImportType.values());
        
        return "import/importUpload.jsp";
    }
    
    @RequestMapping(value="upload", method=RequestMethod.POST)
    public String parseUpload(ModelMap model, HttpServletRequest request) throws ServletRequestBindingException {
        
        // options 
        String importTypeSelector = ServletRequestUtils.getStringParameter(request, "importTypeSelector");
        Boolean ignoreInvalidCols = ServletRequestUtils.getBooleanParameter(request, "ignoreInvalidCols", false);
        
        // detect file
        BulkFileUpload bulkFileUpload = BulkFileUploadUtils.getBulkFileUpload(request);
        
        if (bulkFileUpload.hasErrors()) {
            
            model.addAttribute("ignoreInvalidCols", ignoreInvalidCols);
            model.addAttribute("importTypeSelector", importTypeSelector);
            model.addAttribute("fileErrors", bulkFileUpload.getErrors());
            addColumnInfoToModel(model);
            model.addAttribute("bulkImportTypes", BulkImportType.values());
            
            return "import/importUpload.jsp";
        }
        
        // init file info
        FileSystemResource fileResource = new FileSystemResource(bulkFileUpload.getFile());
        BulkImportFileInfo bulkImportFileInfo = new BulkImportFileInfo(fileResource, ignoreInvalidCols);
        bulkImportFileInfo.setOriginalFilename(bulkFileUpload.getName());
        bulkImportFileInfo.setImportType(importTypeSelector);
        
        // save file info
        String fileInfoId = bulkImportFileInfo.getId();
        bulkImportFileInfoMap.put(fileInfoId, bulkImportFileInfo);
            
        // confirm
        model.addAttribute("fileInfoId", fileInfoId);
        model.addAttribute("bulkImportType", importTypeSelector);
        
        return "redirect:/bulk/import/import-confirm";
    }
    
    // CONFIRM
    @RequestMapping("import-confirm")
    public String importConfirm(ModelMap model, HttpServletRequest request) throws ServletRequestBindingException {
        
        String type = ServletRequestUtils.getStringParameter(request, "bulkImportType");
        BulkImportType bulkImportType = BulkImportType.valueOf(type);
        
        // get file info
        String fileInfoId = ServletRequestUtils.getRequiredStringParameter(request, "fileInfoId");
        BulkImportFileInfo bulkImportFileInfo = bulkImportFileInfoMap.get(fileInfoId);
        
        ParsedBulkImportFileInfo parsedResult = 
                bulkImportService.createParsedBulkImportFileInfo(bulkImportFileInfo, bulkImportType);
        model.addAttribute("parsedResult", parsedResult);
        model.addAttribute("bulkImportType", bulkImportType);
        
        // header errors
        if (parsedResult.hasErrors()) {
            
            model.addAttribute("ignoreInvalidCols", bulkImportFileInfo.isIgnoreInvalidCols());
            model.addAttribute("importTypeSelector", bulkImportType);
            model.addAttribute("headersErrorResolverList", parsedResult.getErrorResolvers());
            model.addAttribute("bulkImportTypes", BulkImportType.values());
            
            addColumnInfoToModel(model);
            
            return "import/importUpload.jsp";
        }
        
        return "import/importConfirm.jsp";
    }
    
    // DO IMPORT
    @RequestMapping("parse")
    public String parse(ModelMap model, HttpServletRequest request, YukonUserContext userContext) throws ServletRequestBindingException, IOException {
        
        BulkImportType bulkImportType = BulkImportType.valueOf(ServletRequestUtils.getStringParameter(request, "bulkImportType"));
        
        // open file as csv
        String fileInfoId = ServletRequestUtils.getRequiredStringParameter(request, "fileInfoId");
        BulkImportFileInfo bulkImportFileInfo = bulkImportFileInfoMap.get(fileInfoId);
        ParsedBulkImportFileInfo parsedResult = 
                bulkImportService.createParsedBulkImportFileInfo(bulkImportFileInfo, bulkImportType);
        toolsEventLogService.importStarted(userContext.getYukonUser(), bulkImportType.name(),
            bulkImportFileInfo.getOriginalFilename());
        String resultsId = bulkImportService.startBulkImport(parsedResult);
        
        model.addAttribute("resultsId", resultsId);
        
        return "redirect:/bulk/import/importResults";
    }
    
    // VIEW RESULTS
    @RequestMapping("importResults")
    public String importResults(ModelMap model, HttpServletRequest request) throws ServletRequestBindingException {
        
        // result info
        String resultsId = ServletRequestUtils.getRequiredStringParameter(request, "resultsId");
        ImportUpdateCallbackResult callbackResult = (ImportUpdateCallbackResult)recentResultsCache.getResult(resultsId);
        
        BulkImportFileInfo bulkImportFileInfo = (BulkImportFileInfo)callbackResult.getBulkFileInfo();
        
        model.addAttribute("ignoreInvalidCols", bulkImportFileInfo.isIgnoreInvalidCols());
        model.addAttribute("callbackResult", callbackResult);
        if (bulkImportFileInfo.getFileResource() != null) {
            model.addAttribute("fileName", bulkImportFileInfo.getFileResource().getFilename());
        }
        return "import/importResults.jsp";
    }
    
    private void addColumnInfoToModel(ModelMap model) {
        
        Map<BulkImportMethod, Set<BulkFieldColumnHeader>> requiredFieldsByMethod = new HashMap<>();
        Map<BulkImportMethod, Set<BulkFieldColumnHeader>> updateableFieldsByMethod  = new HashMap<>();
        
        for (BulkImportMethod method : importMethods) {
            Set<BulkFieldColumnHeader> orderedRequiredColumns =
                new TreeSet<>(BulkFieldColumnHeader.FIELD_NAME_COMPARATOR);
            orderedRequiredColumns.addAll(method.getRequiredColumns());
            requiredFieldsByMethod.put(method, orderedRequiredColumns);

            Set<BulkFieldColumnHeader> orderedOptionalColumns =
                new TreeSet<>(BulkFieldColumnHeader.FIELD_NAME_COMPARATOR);
            orderedOptionalColumns.addAll(method.getOptionalColumns());
            updateableFieldsByMethod.put(method, orderedOptionalColumns);
        }
        
        model.addAttribute("importMethods", importMethods);
        model.addAttribute("requiredFieldsByMethod", requiredFieldsByMethod);
        model.addAttribute("updateableFieldsByMethod", updateableFieldsByMethod );
    }
}