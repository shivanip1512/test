package com.cannontech.web.dev;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.core.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.device.service.CollectionActionService;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.mbean.ServerDatabaseCache;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.service.CachedPointDataCorrelationService;
import com.cannontech.web.common.service.impl.CachedPointDataCorrelationServiceImpl.CorrelationSummary;
import com.cannontech.web.util.WebFileUtils;

@Controller
public class CacheManagementController {
    @Autowired private CollectionActionService collectionActionService;
    @Autowired private CachedPointDataCorrelationService cachedPointDataCorrelationService;
    @Autowired private ServerDatabaseCache dbCache;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private DateFormattingService dateFormattingService;
  
    private static final Logger log = YukonLogManager.getLogger(CacheManagementController.class);
    
    @RequestMapping("cacheManagement")
    public String cacheManagement(ModelMap model) {
        return "cacheManagement.jsp";
    }
    
    @RequestMapping("clearCollectionActionCache")
    public String clearCache(FlashScope flash) {
        collectionActionService.clearCache();
        flash.setConfirm(YukonMessageSourceResolvable.createDefaultWithoutCode("Collection Actions cache has been cleared."));
        return "redirect:cacheManagement";
    }
    
    @RequestMapping("correlatePointData")
    public String correlatePointData(HttpServletResponse response, String[] deviceSubGroups, FlashScope flash,
            YukonUserContext userContext) {
        Set<? extends DeviceGroup> deviceGroups = null;
        try {
            deviceGroups = deviceGroupService.resolveGroupNames(List.of(deviceSubGroups));
        } catch (Exception e) {
            log.error(e);
        }
        List<Integer> deviceIds = deviceGroupService.getDevices(deviceGroups).stream()
                .map(device -> device.getDeviceId()).collect(Collectors.toList());
        List<CorrelationSummary> summary = cachedPointDataCorrelationService.correlateAndLog(deviceIds, userContext);
        if (summary.isEmpty()) {
            flash.setConfirm(YukonMessageSourceResolvable
                    .createDefaultWithoutCode("Correlation of device data finished. No mismatches found."));
        } else {
            try {
                return download(response, summary, userContext);
            } catch (Exception e) {
                log.error(e);
                flash.setError(YukonMessageSourceResolvable.createDefaultWithoutCode(e.getMessage()));
            }
        }
        return "redirect:cacheManagement";
    }
    
    @RequestMapping("scheduleCorrelationOfPointData")
    public String scheduleCorrelationOfPointDataPointData(HttpServletResponse response, String[] deviceSubGroups, String hours,
            String email, FlashScope flash,
            YukonUserContext userContext) {
        return "redirect:cacheManagement";
    }
    
    public String download(HttpServletResponse response, List<CorrelationSummary> summary, YukonUserContext userContext)
            throws IOException {
        List<String> headerRow = getHeader();
        List<List<String>> dataRows = getDataRows(summary, userContext);
        String now = dateFormattingService.format(new Date(), DateFormatEnum.FILE_TIMESTAMP, userContext);
        WebFileUtils.writeToCSV(response, headerRow, dataRows, "CacheValuesCorrelation_" + now + ".csv");
        return "";
    }
    
    private List<String> getHeader() {
        ArrayList<String> retValue = new ArrayList<>();
        retValue.add("Device Id");
        retValue.add("Device Name");
        retValue.add("Point Id");
        retValue.add("Point Name");
        retValue.add("Update Backing Service Cache");
        retValue.add("Async Data Source Cache");
        retValue.add("DYNAMICPOINTDISPATCH");
        retValue.add("RPH Value #1");
        retValue.add("RPH Value #2");
        return retValue;
    }
    
    private List<List<String>> getDataRows(List<CorrelationSummary> summary, YukonUserContext userContext) {
        ArrayList<List<String>> retValue = new ArrayList<>();
        summary.forEach(s -> {
            ArrayList<String> row = new ArrayList<>();
            row.add(String.valueOf(s.getPoint().getPaobjectID()));
            row.add(dbCache.getAllPaosMap().get(s.getPoint().getPaobjectID()).getPaoName());
            row.add(String.valueOf(s.getPoint().getLiteID()));
            row.add(s.getPoint().getPointName());
            row.add(s.getPointUpdateCacheFormattedValue());
            row.add(s.getAsyncDataSourceFormattedValue());
            row.add(s.getDispatchValue());
            row.add(s.getHistoricalValue1());
            row.add(s.getHistoricalValue2());
            retValue.add(row);
        });
        return retValue;
    }
}
