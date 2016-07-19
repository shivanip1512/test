package com.cannontech.web.capcontrol.regulator.setup;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileCopyUtils;

import com.cannontech.capcontrol.export.RegulatorPointMappingExportService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.user.YukonUserContext;
import com.cannontech.yukon.IDatabaseCache;

public class FileExporter {
    
    @Autowired private RegulatorPointMappingExportService exportService;
    @Autowired private IDatabaseCache dbCache;
    
    Map<String, File> filecache = new ConcurrentHashMap<>();
    private final Logger log = YukonLogManager.getLogger(FileExporter.class);
    
    public String build(HttpServletResponse resp, Collection<Integer> ids, YukonUserContext userContext) throws IOException {
        
        String prefix = "RegulatorAttributeMapping";
        
        try {
            File csvFile = exportService.generateCsv(prefix, ids, userContext);
            String key = StringUtils.replace(UUID.randomUUID().toString(), "-", "");
            filecache.put(key, csvFile);
            
            return key;
        } catch (IOException e) {
            log.error("Error creating regulator attribute mapping file for regulator.", e);
            throw e;
        }
    }
    
    public void export(String key, HttpServletResponse resp) {
        
        try {
            File csvFile = filecache.remove(key);
            
            //Set response properties for CSV file
            resp.setContentType("text/csv");
            resp.setHeader("Content-Disposition", "attachment; filename=\"" + csvFile.getName()+"\"");
            resp.setHeader("Content-Length", Long.toString(csvFile.length()));
            
            FileCopyUtils.copy(new FileInputStream(csvFile), resp.getOutputStream());
        } catch (IOException e) {
            log.error("Error creating regulator attribute mapping file for regulator.", e);
        }
    }
    
}