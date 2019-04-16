package com.cannontech.web.bulk;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
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
import com.cannontech.common.bulk.field.BulkFieldService;
import com.cannontech.common.bulk.service.BulkUpdateFileInfo;
import com.cannontech.common.bulk.service.BulkUpdateService;
import com.cannontech.common.bulk.service.ParsedBulkUpdateFileInfo;
import com.cannontech.common.events.loggers.ToolsEventLogService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.bulk.util.BulkFileUpload;
import com.cannontech.web.bulk.util.BulkFileUploadUtils;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@CheckRoleProperty(YukonRoleProperty.BULK_UPDATE_OPERATION)
@Controller
@RequestMapping("update/*")
public class UpdateController {
    
    @Autowired private BulkFieldService bulkFieldService;
    @Autowired private BulkUpdateService bulkUpdateService;
    @Autowired private ToolsEventLogService toolsEventLogService;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Resource(name="recentResultsCache") private RecentResultsCache<BackgroundProcessResultHolder> recentResultsCache;
   
    private Map<String, BulkUpdateFileInfo> bulkUpdateFileInfoMap = new ConcurrentHashMap<>();
    
    // UPLOAD
    @RequestMapping("upload")
    public String upload(ModelMap model, HttpServletRequest request) throws ServletException {

        // column info
        addColumnInfoToMav(model);
        
        // errors
        String errors = ServletRequestUtils.getStringParameter(request, "fileErrorKeys", "");
        model.addAttribute("fileErrorKeysList", StringUtils.split(errors, "||"));
        
        // options checked included? pass along
        Boolean ignoreInvalidCols = ServletRequestUtils.getBooleanParameter(request, "ignoreInvalidCols", true);
        Boolean ignoreInvalidIdentifiers = ServletRequestUtils.getBooleanParameter(request, "ignoreInvalidIdentifiers", false);
        model.addAttribute("ignoreInvalidCols", ignoreInvalidCols);
        model.addAttribute("ignoreInvalidIdentifiers", ignoreInvalidIdentifiers);
        
        return "update/updateUpload.jsp";
    }

    // PARSE
    @RequestMapping("parseUpload")
    public String parseUpload(ModelMap model, HttpServletRequest request, YukonUserContext userContext) {
        
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        
        // options 
        Boolean ignoreInvalidCols = ServletRequestUtils.getBooleanParameter(request, "ignoreInvalidCols", false);
        Boolean ignoreInvalidIdentifiers = ServletRequestUtils.getBooleanParameter(request, "ignoreInvalidIdentifiers", false);
        
        // detect file
        BulkFileUpload bulkFileUpload = BulkFileUploadUtils.getBulkFileUpload(request);
        
        if (bulkFileUpload.hasErrors()) {
            // error mav
            model.addAttribute("ignoreInvalidCols", ignoreInvalidCols);
            model.addAttribute("ignoreInvalidIdentifiers", ignoreInvalidIdentifiers);
            
            model.addAttribute("fileErrorKeys", StringUtils.join(bulkFileUpload.getErrors(), "||"));
            return "redirect:/bulk/update/upload";
        }
        
        // init file info
        FileSystemResource fileResource = new FileSystemResource(bulkFileUpload.getFile());
        BulkUpdateFileInfo bulkUpdateFileInfo = new BulkUpdateFileInfo(fileResource, ignoreInvalidCols, ignoreInvalidIdentifiers);
        
        // save file info
        bulkUpdateFileInfo.setOriginalFilename(bulkFileUpload.getName());
        bulkUpdateFileInfo.setImportType(accessor.getMessage("yukon.web.menu.update"));
        String fileInfoId = bulkUpdateFileInfo.getId();
        bulkUpdateFileInfoMap.put(fileInfoId, bulkUpdateFileInfo);
        
        // confirm
        model.addAttribute("fileInfoId", fileInfoId);
        
        return "redirect:/bulk/update/updateConfirm";
    }
    
    // CONFIRM
    @RequestMapping("updateConfirm")
    public String updateConfirm(ModelMap model, HttpServletRequest request) throws ServletRequestBindingException {
        
        // get file info
        String fileInfoId = ServletRequestUtils.getRequiredStringParameter(request, "fileInfoId");
        BulkUpdateFileInfo bulkUpdateFileInfo = bulkUpdateFileInfoMap.get(fileInfoId);
        model.addAttribute("bulkUpdateFileInfo", bulkUpdateFileInfo);
        
        ParsedBulkUpdateFileInfo parsedResult = bulkUpdateService.createParsedBulkUpdateFileInfo(bulkUpdateFileInfo);
        model.addAttribute("parsedResult", parsedResult);
        
        // has file errors
        if (parsedResult.hasErrors()) {
            
            // options
            model.addAttribute("ignoreInvalidCols", bulkUpdateFileInfo.isIgnoreInvalidCols());
            model.addAttribute("ignoreInvalidIdentifiers", bulkUpdateFileInfo.isIgnoreInvalidIdentifiers());
            
            // column info
            addColumnInfoToMav(model);
            
            // errors
            model.addAttribute("headersErrorResolverList", parsedResult.getErrorResolvers());
            
            return "update/updateUpload.jsp";
        }
        
        return "update/updateConfirm.jsp";
    }
    
    // DO UPDATE
    @RequestMapping("doUpdate")
    public String doUpdate(ModelMap model, HttpServletRequest request, YukonUserContext userContext) throws ServletRequestBindingException, IOException {
        
        // open file as csv
        String fileInfoId = ServletRequestUtils.getRequiredStringParameter(request, "fileInfoId");
        BulkUpdateFileInfo bulkUpdateFileInfo = bulkUpdateFileInfoMap.get(fileInfoId);
        ParsedBulkUpdateFileInfo parsedResult = bulkUpdateService.createParsedBulkUpdateFileInfo(bulkUpdateFileInfo);
        
        toolsEventLogService.importStarted(userContext.getYukonUser(), bulkUpdateFileInfo.getImportType(),
            bulkUpdateFileInfo.getOriginalFilename());
       
        String resultsId = bulkUpdateService.startBulkUpdate(parsedResult);
        
        model.addAttribute("resultsId", resultsId);
        
        return "redirect:/bulk/update/updateResults";
    }
    
    // VIEW RESULTS
    @RequestMapping("updateResults")
    public String updateResults(ModelMap model, HttpServletRequest request) throws ServletRequestBindingException {

        // result info
        String resultsId = ServletRequestUtils.getRequiredStringParameter(request, "resultsId");
        ImportUpdateCallbackResult callbackResult = (ImportUpdateCallbackResult)recentResultsCache.getResult(resultsId);
        
        BulkUpdateFileInfo bulkUpdateFileInfo = (BulkUpdateFileInfo)callbackResult.getBulkFileInfo();
        
        model.addAttribute("ignoreInvalidCols", bulkUpdateFileInfo.isIgnoreInvalidCols());
        model.addAttribute("ignoreInvalidIdentifiers", bulkUpdateFileInfo.isIgnoreInvalidIdentifiers());
        model.addAttribute("callbackResult", callbackResult);
        if (bulkUpdateFileInfo.getFileResource() != null) {
            model.addAttribute("fileName", bulkUpdateFileInfo.getFileResource().getFilename());
        }
        return "update/updateResults.jsp";
    }
    
    private void addColumnInfoToMav(ModelMap model) {
        
        // column info
        Set<BulkFieldColumnHeader> updateableFields = bulkFieldService.getUpdateableBulkFieldColumnHeaders();
        Set<BulkFieldColumnHeader> identifierFields = bulkFieldService.getUpdateIdentifierBulkFieldColumnHeaders();
        
        Set<BulkFieldColumnHeader> allFields = new TreeSet<>(BulkFieldColumnHeader.FIELD_NAME_COMPARATOR);
        allFields.addAll(updateableFields);
        allFields.addAll(identifierFields);
        
        model.addAttribute("allFields", allFields);
        Map<BulkFieldColumnHeader, Boolean> identifierFieldsMap = ServletUtil.convertSetToMap(identifierFields);
        model.addAttribute("identifierFieldsMap", identifierFieldsMap);
    }

}