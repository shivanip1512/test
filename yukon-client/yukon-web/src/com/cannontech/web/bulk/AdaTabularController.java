package com.cannontech.web.bulk;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.bulk.model.Analysis;
import com.cannontech.common.bulk.model.DevicePointValuesHolder;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.core.dao.ArchiveDataAnalysisDao;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.PointFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.tools.csv.CSVWriter;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("archiveDataAnalysis/tabular/*")
public class AdaTabularController {
    private ArchiveDataAnalysisDao archiveDataAnalysisDao;
    private DateFormattingService dateFormattingService;
    private YukonUserContextMessageSourceResolver messageSourceResolver;
    private PointFormattingService pointFormattingService;
    
    @RequestMapping
    public String view(ModelMap model, int analysisId) {
        Analysis analysis = archiveDataAnalysisDao.getAnalysisById(analysisId);
        model.addAttribute("analysis", analysis);
        
        List<DevicePointValuesHolder> devicePointValuesList = archiveDataAnalysisDao.getAnalysisPointValues(analysisId);
        model.addAttribute("devicePointValuesList", devicePointValuesList);
        
        List<Instant> dateTimeList = analysis.getIntervalEndTimes();
        model.addAttribute("dateTimeList", dateTimeList);
        
        return "archiveDataAnalysis/tabular.jsp";
    }
    
    @RequestMapping
    public String csv(ModelMap model, HttpServletResponse response, YukonUserContext userContext, int analysisId) throws IOException {
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        Analysis analysis = archiveDataAnalysisDao.getAnalysisById(analysisId);
        List<DevicePointValuesHolder> devicePointValuesList = archiveDataAnalysisDao.getAnalysisPointValues(analysisId);
        List<Instant> dateTimeList = analysis.getIntervalEndTimes();
        
        //convert date/times into String array for header
        String[] headerRow = new String[dateTimeList.size()+1];
        headerRow[0] = messageSourceAccessor.getMessage("yukon.web.modules.amr.analysis.tabular.deviceName");
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
        
        //set up output for CSV
        response.setContentType("text/csv");
        response.setHeader("Content-Type", "application/force-download");
        String fileName = "ArchiveDataAnalysisResults.csv";
        fileName = ServletUtil.makeWindowsSafeFileName(fileName);
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName);
        OutputStream outputStream = response.getOutputStream();
        
        //write out the file
        Writer writer = new BufferedWriter(new OutputStreamWriter(outputStream));
        CSVWriter csvWriter = new CSVWriter(writer);
        
        csvWriter.writeNext(headerRow);
        for (String[] line : dataRows) {
            csvWriter.writeNext(line);
        }
        csvWriter.close();
        
        return "";
    }
    
    @Autowired
    public void setArchiveDataAnalysisDao(ArchiveDataAnalysisDao archiveDataAnalysisDao) {
        this.archiveDataAnalysisDao = archiveDataAnalysisDao;
    }
    
    @Autowired
    public void setDateFormattingService(DateFormattingService dateFormattingService) {
        this.dateFormattingService = dateFormattingService;
    }
    
    @Autowired
    public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }
    
    @Autowired
    public void setPointFormattingService(PointFormattingService pointFormattingService) {
        this.pointFormattingService = pointFormattingService;
    }
}
