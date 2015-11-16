package com.cannontech.web.bulk.ada;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.bulk.collection.device.ArchiveDataAnalysisCollectionProducer;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.model.Analysis;
import com.cannontech.common.bulk.model.DevicePointValuesHolder;
import com.cannontech.common.bulk.service.ArchiveDataAnalysisService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.core.dao.ArchiveDataAnalysisDao;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.core.service.PointFormattingService;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.util.WebFileUtils;
import com.google.common.collect.Lists;

@CheckRoleProperty(YukonRoleProperty.ARCHIVED_DATA_ANALYSIS)
@Controller
@RequestMapping("archiveDataAnalysis/tabular/*")
public class AdaTabularController {
    
    @Autowired private ArchiveDataAnalysisDao archiveDataAnalysisDao;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private PointFormattingService pointFormattingService;
    @Autowired private ArchiveDataAnalysisService archiveDataAnalysisService;
    @Autowired private ArchiveDataAnalysisCollectionProducer adaCollectionProducer;
    
    @RequestMapping("view")
    public String view(ModelMap model, int analysisId) {
        Analysis analysis = archiveDataAnalysisDao.getAnalysisById(analysisId);
        model.addAttribute("analysis", analysis);
        DeviceCollection collection = adaCollectionProducer.buildDeviceCollection(analysisId);
        model.addAttribute("deviceCollection", collection);
        
        List<DevicePointValuesHolder> devicePointValuesList = archiveDataAnalysisDao.getAnalysisPointValues(analysisId);
        model.addAttribute("devicePointValuesList", devicePointValuesList);
        
        List<Instant> dateTimeList = archiveDataAnalysisService.getIntervalEndTimes(analysis);
        model.addAttribute("dateTimeList", dateTimeList);
        
        return "archiveDataAnalysis/tabular.jsp";
    }
    
    @RequestMapping("csv")
    public String csv(ModelMap model, HttpServletResponse response, YukonUserContext userContext, int analysisId) throws IOException {
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        Analysis analysis = archiveDataAnalysisDao.getAnalysisById(analysisId);
        List<DevicePointValuesHolder> devicePointValuesList = archiveDataAnalysisDao.getAnalysisPointValues(analysisId);
        List<Instant> dateTimeList = archiveDataAnalysisService.getIntervalEndTimes(analysis);
        
        //convert date/times into String array for header
        String[] headerRow = new String[dateTimeList.size()+1];
        headerRow[0] = messageSourceAccessor.getMessage("yukon.web.modules.tools.bulk.analysis.NAME");
        for(int i = 0; i < dateTimeList.size(); i++) {
            headerRow[i+1] = dateFormattingService.format(dateTimeList.get(i), DateFormatEnum.DATEHM, userContext);
        }
        
        //convert point values
        List<String[]> dataRows = Lists.newArrayList();
        for(DevicePointValuesHolder pointValues : devicePointValuesList) {
            List<PointValueHolder> pointValuesList = pointValues.getPointValues();
            int dataRowSize = pointValuesList.size() + 1;
            String[] dataRow = new String[dataRowSize];
            dataRow[0] = pointValues.getDisplayablePao().getName();
            
            for(int i = 0; i < pointValuesList.size(); i++) {
                PointValueHolder pointValue = pointValuesList.get(i);
                if(pointValue==null){
                    dataRow[i+1] = "";
                } else {
                    PointFormattingService.Format format = PointFormattingService.Format.RAWVALUE;
                    String valueString = pointFormattingService.getValueString(pointValue, format, userContext);
                    dataRow[i+1] = valueString;
                }
            }
            dataRows.add(dataRow);
        }
        
        //write out the file
        WebFileUtils.writeToCSV(response, headerRow, dataRows, "ArchiveDataAnalysisResults.csv");
        
        return null;
    }
    
}