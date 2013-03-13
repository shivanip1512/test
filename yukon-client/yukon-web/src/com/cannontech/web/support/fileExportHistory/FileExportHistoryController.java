package com.cannontech.web.support.fileExportHistory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cannontech.common.fileExportHistory.ExportHistoryEntry;
import com.cannontech.common.fileExportHistory.service.FileExportHistoryService;
import com.cannontech.common.search.SearchResult;
import com.cannontech.i18n.WebMessageSourceResolvable;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.common.flashScope.FlashScope;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/fileExportHistory/*")
public class FileExportHistoryController {
	@Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
	@Autowired private FileExportHistoryService fileExportHistoryService;
	
	@RequestMapping
	public String list(ModelMap model, HttpServletRequest request, YukonUserContext userContext,
			FlashScope flashScope, String name, String initiator, Integer entryId,
			@RequestParam(defaultValue="25") int itemsPerPage, @RequestParam(defaultValue="1") int page) {

		int startIndex = (page -1) * itemsPerPage;
		
		List<ExportHistoryEntry> entries;
		if(entryId != null) {
			ExportHistoryEntry entry = fileExportHistoryService.getEntry(entryId);
			entries = Lists.newArrayList(entry);
			
			WebMessageSourceResolvable resolvable = new WebMessageSourceResolvable("yukon.web.modules.support.fileExportHistory.singleEntry", entry.getInitiator());
			resolvable.setHtmlEscape(false);
			flashScope.setWarning(resolvable);
		} else {
			entries = fileExportHistoryService.getFilteredEntries(name, initiator);
		}
		Collections.sort(entries);
		int endIndex = startIndex + itemsPerPage > entries.size() ? entries.size() : startIndex + itemsPerPage;
		
		SearchResult<ExportHistoryEntry> searchResult = new SearchResult<ExportHistoryEntry>();
		searchResult.setBounds(startIndex, itemsPerPage, entries.size());
		searchResult.setResultList(entries.subList(startIndex, endIndex));
		model.addAttribute("searchResult", searchResult);
		model.addAttribute("searchName", name);
		model.addAttribute("searchInitiator", initiator);
		
		return "fileExportHistory/list.jsp";
	}
	
	@RequestMapping
	public String downloadArchivedCopy(ModelMap model, HttpServletResponse response, FlashScope flashScope, int entryId) {
		ExportHistoryEntry historyEntry = fileExportHistoryService.getEntry(entryId);
		
		try (
				OutputStream output = response.getOutputStream();
				InputStream input = new FileInputStream(fileExportHistoryService.getArchivedFile(entryId));
		){
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
