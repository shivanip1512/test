package com.cannontech.web.bulk;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.cannontech.common.bulk.callbackResult.BackgroundProcessResultHolder;
import com.cannontech.common.bulk.callbackResult.PointImportCallbackResult;
import com.cannontech.common.bulk.model.PointImportFormats;
import com.cannontech.common.bulk.model.PointImportType;
import com.cannontech.common.bulk.service.CalculationImportService;
import com.cannontech.common.bulk.service.PointImportService;
import com.cannontech.common.csvImport.ImportData;
import com.cannontech.common.csvImport.ImportFileFormat;
import com.cannontech.common.csvImport.ImportFileValidator;
import com.cannontech.common.csvImport.ImportParser;
import com.cannontech.common.exception.DuplicateColumnNameException;
import com.cannontech.common.exception.EmptyImportFileException;
import com.cannontech.common.exception.FileImportException;
import com.cannontech.common.exception.InvalidColumnNameException;
import com.cannontech.common.exception.NoImportFileException;
import com.cannontech.common.exception.RequiredColumnMissingException;
import com.cannontech.common.point.PointCalculation;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.util.WebFileUtils;
import com.cannontech.web.util.WebFileUtils.CSVDataWriter;
import com.google.common.collect.Lists;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

@Controller
@RequestMapping("/pointImport/*")
@CheckRoleProperty(YukonRoleProperty.ADD_REMOVE_POINTS)
public class PointImportController {
    @Resource(name="recentResultsCache")
    private RecentResultsCache<PointImportCallbackResult> recentResultsCache;
    @Autowired PointImportService pointImportService;
    @Autowired CalculationImportService calculationImportService;

    @RequestMapping("upload")
    public String upload(ModelMap model) {
        model.addAttribute("importTypes", PointImportType.values());
        
        model.addAttribute("analogPointFormat", PointImportFormats.ANALOG_POINT_FORMAT);
        model.addAttribute("statusPointFormat", PointImportFormats.STATUS_POINT_FORMAT);
        model.addAttribute("accumulatorPointFormat", PointImportFormats.ACCUMULATOR_POINT_FORMAT);
        model.addAttribute("calcAnalogPointFormat", PointImportFormats.CALC_ANALOG_POINT_FORMAT);
        model.addAttribute("calcStatusPointFormat", PointImportFormats.CALC_STATUS_POINT_FORMAT);
        
        model.addAttribute("calculationFormat", calculationImportService.getFormat());
        return "pointImport/upload.jsp";
    }
    
    @RequestMapping(value="submitImport", method=RequestMethod.POST)
    public String submitImport(ModelMap model, HttpServletRequest request, FlashScope flashScope, 
                               PointImportType importType, YukonUserContext userContext) throws IOException {
        
        boolean ignoreInvalidColumns = ServletRequestUtils.getBooleanParameter(request, "ignoreInvalidColumns", false);
        
        Map<String, PointCalculation> calcMap = null;
        if(importType.needsCalcFile()) {
            //get the calculation file
            if(!ServletFileUpload.isMultipartContent(request)) {
                flashScope.setError(new YukonMessageSourceResolvable("yukon.web.modules.tools.bulk.pointImport.error.noCalcImportFile"));
                return "redirect:upload";
            }
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            MultipartFile dataFile = multipartRequest.getFile("calculationFile");
            
            //Set up a reader for the calculation file
            CSVReader csvReader = null;
            try {
                csvReader = WebFileUtils.getTempBackedCsvReaderFromMultipartFile(dataFile);
            } catch(NoImportFileException e) {
                flashScope.setError(new YukonMessageSourceResolvable("yukon.web.modules.tools.bulk.pointImport.error.noCalcImportFile"));
                return "redirect:upload";
            } catch(EmptyImportFileException e) {
                flashScope.setError(new YukonMessageSourceResolvable("yukon.web.modules.tools.bulk.pointImport.error.errorProcessingCalcFile"));
                return "redirect:upload";
            } catch (FileImportException e) {
                flashScope.setError(new YukonMessageSourceResolvable(
                    "yukon.web.modules.tools.bulk.pointImport.error.errorProcessingCalcFile"));
                return "redirect:upload";
            }
            
            List<String> results = Lists.newArrayList();
            calcMap = calculationImportService.parseCalcFile(csvReader, results, ignoreInvalidColumns);
            if(calcMap == null) {
                String key = results.get(0);
                Object[] args = results.subList(1, results.size()).toArray();
                flashScope.setError(new YukonMessageSourceResolvable(key, args));
                return "redirect:upload";
            }
        }
        
        //get the import file
        if(!ServletFileUpload.isMultipartContent(request)) {
            flashScope.setError(new YukonMessageSourceResolvable("yukon.web.import.error.noImportFile"));
            return "redirect:upload";
        }
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile dataFile = multipartRequest.getFile("dataFile");
        
        //Set up a reader for the import file
        CSVReader csvReader = null;
        try {
            csvReader = WebFileUtils.getTempBackedCsvReaderFromMultipartFile(dataFile);
        } catch (FileImportException e) {
            flashScope.setError(new YukonMessageSourceResolvable(e.getMessage()));
            return "redirect:upload";
        }
        
        ImportFileFormat importFormat = importType.getFormat();
        importFormat.setIgnoreInvalidHeaders(ignoreInvalidColumns);
        ImportParser parser = new ImportParser(importFormat);
        ImportData data = parser.parseFromCsvReader(csvReader);
        csvReader.close();
        
        try {
            ImportFileValidator.validateFileStructure(data);
        } catch(DuplicateColumnNameException e) {
            flashScope.setError(new YukonMessageSourceResolvable("yukon.web.import.error.duplicateColumns", e.getJoinedDuplicateColumnNames()));
            return "redirect:upload";
        } catch(InvalidColumnNameException e) {
            flashScope.setError(new YukonMessageSourceResolvable("yukon.web.import.error.invalidColumns", e.getJoinedInvalidColumnNames()));
            return "redirect:upload";
        } catch(RequiredColumnMissingException e) {
            flashScope.setError(new YukonMessageSourceResolvable("yukon.web.import.error.missingRequiredColumn", e.getJoinedMissingColumnNames()));
            return "redirect:upload";
        }
        
        String resultId = pointImportService.startImport(data, importType, calcMap, userContext);
        model.addAttribute("resultId", resultId);
        model.addAttribute("fileName", dataFile.getOriginalFilename());
        model.addAttribute("ignoreInvalidColumns", ignoreInvalidColumns);
        
        return "redirect:results";
    }
    
    @RequestMapping("results")
    public String results(ModelMap model, String resultId, String fileName, boolean ignoreInvalidColumns) {
        BackgroundProcessResultHolder result = recentResultsCache.getResult(resultId);
        
        model.addAttribute("ignoreInvalidColumns", ignoreInvalidColumns);
        model.addAttribute("result", result);
        model.addAttribute("resultId", resultId);
        model.addAttribute("fileName", fileName);
        return "pointImport/results.jsp";
    }
    
    @RequestMapping("downloadFailed")
    public String downloadFailed(HttpServletResponse response, String resultId) throws IOException {
        //get the required data
        PointImportCallbackResult result = recentResultsCache.getResult(resultId);
        final List<List<String>> originalData = result.getImportData().getOriginalDataAsLists();
        final List<Integer> failedRowNums = result.getFailedRowNumbers();
        final List<String> log = result.getLog();
        
        //write headers
        List<String> headers = originalData.get(0);
        headers.add("FAIL REASON");
        
        //write data rows
        WebFileUtils.writeToCSV(response, headers.toArray(new String[0]), "FailedPointImports.csv", new CSVDataWriter() {
            @Override
            public void writeData(CSVWriter csvWriter) {
                for(int i = 0; i < originalData.size(); i++) {
                    if(failedRowNums.contains(i)) {
                        List<String> line = originalData.get(i+1); //(add one to account for header row)
                        line.add(log.get(i)); //add failed reason from log to each failed line
                        csvWriter.writeNext(line.toArray(new String[0]));
                    }
                }
            }
        });
        
        return null;
    }
    
    @RequestMapping("updateLog")
    @ResponseBody
    public Map<String, String> updateLog(String resultId) {
        PointImportCallbackResult result = recentResultsCache.getResult(resultId);
        Integer index = result.getLogIndex();
        if(index >= CtiUtilities.MAX_LOGGED_LINES){
            return null;
        }

        List<String> logLines = result.getNewLogLines();
        List<Integer> failedLines = result.getFailedRowNumbers();
        
        Map<String, String> json = new HashMap<>();
        
        for(String line : logLines) {
            String quality = failedLines.contains(index) ? "error" : "success";
            json.put(StringEscapeUtils.escapeXml10(line), quality);
            index++;
            if(index >= CtiUtilities.MAX_LOGGED_LINES){
                break;
            }
        }
        
        return json;
    }
    
}
