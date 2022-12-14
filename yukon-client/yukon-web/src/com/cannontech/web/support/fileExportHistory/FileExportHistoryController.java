package com.cannontech.web.support.fileExportHistory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.cannontech.common.fileExportHistory.ExportHistoryEntry;
import com.cannontech.common.fileExportHistory.FileExportType;
import com.cannontech.common.fileExportHistory.dao.FileExportHistoryDao;
import com.cannontech.common.fileExportHistory.service.FileExportHistoryService;
import com.cannontech.i18n.WebMessageSourceResolvable;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.common.flashScope.FlashScope;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/fileExportHistory/*")
public class FileExportHistoryController {
    
    @Autowired private FileExportHistoryService fileExportHistoryService;
    @Autowired private FileExportHistoryDao fileExportHistoryDao;
    
    private final static String baseKey = "yukon.web.modules.support.fileExportHistory";
    
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public String list(ModelMap model, FlashScope flashScope, String name, String jobName, Integer entryId,
            @RequestParam(defaultValue="ARCHIVED_DATA_EXPORT") FileExportType exportType, Integer jobGroupId) {

        List<ExportHistoryEntry> exports = null;
        if (entryId != null) {
            ExportHistoryEntry entry = fileExportHistoryDao.getEntry(entryId);
            exports = Lists.newArrayList(entry);
            
			WebMessageSourceResolvable resolvable = new WebMessageSourceResolvable(
					baseKey + ".singleEntry", entry.getType(),
					Integer.toString(entry.getJobGroupId()));
            resolvable.setHtmlEscape(false);
            flashScope.setWarning(resolvable);
        } else {
            exports = fileExportHistoryDao.getFilteredEntries(name, jobName, exportType, jobGroupId);
        }
        Collections.sort(exports);
        
        model.addAttribute("exports", exports);
        model.addAttribute("searchName", name);
        model.addAttribute("searchJobName", jobName);
        model.addAttribute("dataExportTypeList", FileExportType.values());
        if(null != exportType) {
        	model.addAttribute("searchExportType", exportType);
        }        
        
        return "fileExportHistory/list.jsp";
    }
    
    @RequestMapping("downloadArchivedCopy")
    public String downloadArchivedCopy(HttpServletResponse response, FlashScope flashScope, int entryId) {
        
        ExportHistoryEntry historyEntry = fileExportHistoryDao.getEntry(entryId);
        
        try (OutputStream output = response.getOutputStream();
             InputStream input = new FileInputStream(fileExportHistoryService.getArchivedFile(entryId));) {
            //set up the response
            response.setContentType(historyEntry.getFileMimeType());
            String fileName = ServletUtil.makeWindowsSafeFileName(historyEntry.getOriginalFileName());
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName +"\"");
            //pull data from the file and push it to the browser
            IOUtils.copy(input, output);
        } catch(FileNotFoundException e) {
            flashScope.setError(new YukonMessageSourceResolvable("yukon.web.modules.support.fileExportHistory.fileNotFound"));
        } catch(IOException e) {
            flashScope.setError(new YukonMessageSourceResolvable("yukon.web.modules.support.fileExportHistory.ioError"));
        }
        return null;
    }
}