package com.cannontech.web.bulk;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.cannontech.common.bulk.callbackResult.BackgroundProcessResultHolder;
import com.cannontech.common.bulk.callbackResult.TranslationImportCallbackResult;
import com.cannontech.common.bulk.model.FdrExportData;
import com.cannontech.common.bulk.model.FdrImportFileInterfaceInfo;
import com.cannontech.common.bulk.model.FdrInterfaceDisplayable;
import com.cannontech.common.bulk.service.FdrTranslationManagerCsvHelper;
import com.cannontech.common.bulk.service.FdrTranslationManagerService;
import com.cannontech.common.exception.ImportFileFormatException;
import com.cannontech.common.fdr.FdrInterfaceType;
import com.cannontech.common.fdr.FdrTranslation;
import com.cannontech.common.fdr.FdrUtils;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.tools.csv.CSVReader;
import com.cannontech.tools.csv.CSVWriter;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.exceptions.EmptyImportFileException;
import com.cannontech.web.exceptions.NoImportFileException;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.util.WebFileUtils;
import com.cannontech.web.util.WebFileUtils.CSVDataWriter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Controller
@CheckRoleProperty(YukonRoleProperty.FDR_TRANSLATION_MANAGER)
@RequestMapping("fdrTranslationManager/*")
public class FdrTranslationManagerController {
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private FdrTranslationManagerService fdrTranslationManagerService;
    @Autowired private FdrTranslationManagerCsvHelper fdrTranslationManagerCsvHelper;
    private RecentResultsCache<TranslationImportCallbackResult> recentResultsCache;
    
    @RequestMapping
    public String home(ModelMap model, YukonUserContext userContext) {
        List<FdrInterfaceDisplayable> displayables = fdrTranslationManagerService.getAllInterfaceDisplayables(userContext);
        model.addAttribute("displayableInterfaces", displayables);
        
        FdrInterfaceType[] interfaceTypes = FdrInterfaceType.values();
        Arrays.sort(interfaceTypes, FdrInterfaceType.alphabeticalComparator);
        
        model.addAttribute("interfaceTypes", interfaceTypes);
        
        return "fdrTranslationManager/home.jsp";
    }
    
    @RequestMapping
    public String report(ModelMap model, HttpServletResponse response, String reportInterface) throws IOException {
        List<String> formattedHeaders = Lists.newArrayList();
        fdrTranslationManagerCsvHelper.addDefaultExportColumnsToList(formattedHeaders);
        
        List<FdrTranslation> filteredTranslationsList = fdrTranslationManagerService.getFilteredTranslationList(reportInterface);
        
        //Add all headers for each translation type to the headers list
        fdrTranslationManagerCsvHelper.addHeadersFromTranslations(formattedHeaders, filteredTranslationsList);
        
        final FdrExportData exportData = new FdrExportData();
        exportData.setHeaderRow(formattedHeaders);
        
        //Insert translation data
        fdrTranslationManagerCsvHelper.populateExportData(exportData, filteredTranslationsList);
        
        //Write array values to csv
        WebFileUtils.writeToCSV(response, null, "FdrTranslationsReport.csv", new CSVDataWriter() {
            @Override
            public void writeData(CSVWriter csvWriter) {
                for(String[] line : exportData.asArrays()) {
                    for(int i=0; i<line.length; i++) {
                        if ( "".equals(line[i]) ) {
                            line[i] = FdrUtils.EMPTY;
                        }
                    }
                    csvWriter.writeNext(line);
                }
            }
        });
        
        return null;
    }
    
    @RequestMapping
    public ResponseEntity<Map<String, ? extends Object>> submitImport(HttpServletRequest request,  
                                                                      YukonUserContext userContext, 
                                                                      boolean ignoreInvalidColumns) throws IOException {
        
        //Procure the import file
        if(!ServletFileUpload.isMultipartContent(request)) {
            MessageSourceResolvable errorMsg = new YukonMessageSourceResolvable("yukon.web.modules.amr.fdrTranslationManagement.error.noImportFile");
            return prepareErrorReturn(userContext, errorMsg);
        }
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile dataFile = multipartRequest.getFile("importFile");
        
        //Set up a reader for the file
        CSVReader csvReader;
        try {
            csvReader = WebFileUtils.getTempBackedCsvReaderFromMultipartFile(dataFile);
        } catch(NoImportFileException e) {
            MessageSourceResolvable errorMsg = new YukonMessageSourceResolvable("yukon.web.modules.amr.fdrTranslationManagement.error.noImportFile");
            return prepareErrorReturn(userContext, errorMsg);
        } catch(EmptyImportFileException e) {
            MessageSourceResolvable errorMsg = new YukonMessageSourceResolvable("yukon.web.modules.amr.fdrTranslationManagement.error.emptyImportFile");
            return prepareErrorReturn(userContext, errorMsg);
        }
        
        //Get column headers, strip whitespace, check for duplicates
        String[] headersArray = csvReader.readNext();
        List<String> headers;
        try {
            headers = fdrTranslationManagerCsvHelper.cleanAndValidateHeaders(headersArray);
        } catch(ImportFileFormatException e) {
            MessageSourceResolvable errorMsg = new YukonMessageSourceResolvable("yukon.web.modules.amr.fdrTranslationManagement.error.duplicateColumn", e.getHeaderName());
            return prepareErrorReturn(userContext, errorMsg);
        }
        
        //Check for all default headers
        String missingHeaders = fdrTranslationManagerCsvHelper.checkForMissingDefaultImportHeaders(headers);
        if(missingHeaders != null) {
            //Error - missing default header
            MessageSourceResolvable errorMsg = new YukonMessageSourceResolvable("yukon.web.modules.amr.fdrTranslationManagement.error.defaultHeaderMissing", missingHeaders);
            return prepareErrorReturn(userContext, errorMsg);
        }
        
        //Get list of all interfaces represented in the file and check to make 
        //sure that all required columns are present
        FdrImportFileInterfaceInfo interfaceInfo;
        try {
            interfaceInfo = fdrTranslationManagerService.getInterfaceInfo(headers, ignoreInvalidColumns);
        } catch(ImportFileFormatException e) {
            MessageSourceResolvable errorMsg = new YukonMessageSourceResolvable("yukon.web.modules.amr.fdrTranslationManagement.error.badColumn", e.getHeaderName());
            return prepareErrorReturn(userContext, errorMsg);
        }
        
        try {
            fdrTranslationManagerCsvHelper.validateInterfaceHeadersPresent(interfaceInfo, headers);
        } catch(ImportFileFormatException e) {
            MessageSourceResolvable errorMsg = new YukonMessageSourceResolvable("yukon.web.modules.amr.fdrTranslationManagement.error.missingColumn", e.getHeaderName(), e.getInterfaceName());
            return prepareErrorReturn(userContext, errorMsg);
        }
        
        //Read all file lines
        List<String[]> fileLines = Lists.newArrayList();
        String[] line;
        while((line = csvReader.readNext()) != null) {
            fileLines.add(line);
        }
        
        //start import
        String resultId = fdrTranslationManagerService.startImport(headers, interfaceInfo.getColumnsToIgnore(), fileLines, userContext);
        
        //return parameter map so ajax success function can redirect to results
        Map<String,Object> responseMap = Maps.newHashMapWithExpectedSize(3);
        responseMap.put("resultId", resultId);
        responseMap.put("ignoreInvalidColumns", ignoreInvalidColumns);
        responseMap.put("fileName", dataFile.getOriginalFilename());
        
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.TEXT_HTML);
        
        return new ResponseEntity<Map<String, ? extends Object>>(responseMap, responseHeaders, HttpStatus.CREATED);
    }
    
    @RequestMapping
    public String importResults(ModelMap model, String resultId, String fileName, boolean ignoreInvalidColumns) {
        BackgroundProcessResultHolder result = recentResultsCache.getResult(resultId);
        
        model.addAttribute("resultId", resultId);
        model.addAttribute("result", result);
        model.addAttribute("ignoreInvalidColumns", ignoreInvalidColumns);
        model.addAttribute("fileName", fileName);
        
        return "fdrTranslationManager/import.jsp";
    }
    
    @RequestMapping
    public String downloadFailed(ModelMap model, HttpServletResponse response, String resultId) throws IOException {
        TranslationImportCallbackResult result = recentResultsCache.getResult(resultId);
        List<String> headers = result.getHeaders();
        List<Integer> failedRowNums = result.getFailedRowNumbers();
        List<String> log = result.getLog();
        List<String[]> importRows = result.getImportRows();
        
        headers.add("FAIL_REASON");
        
        //build 2D array with appropriate width and height
        final String[][] dataGrid = new String[failedRowNums.size()][];
        for(int i = 0; i < dataGrid.length; i++) {
            dataGrid[i] = new String[headers.size()];
        }
        
        //add the rest of the rows
        for(int i = 0; i < importRows.size(); i++) {
            if(failedRowNums.contains(i)) {
                String[] row = importRows.get(i);
                
                for(int j = 0; j < row.length; j++) {
                    dataGrid[i][j] = row[j];
                    
                }
                dataGrid[i][row.length] = log.get(i);
            }
        }
        
        //Write array values to csv
        WebFileUtils.writeToCSV(response, headers.toArray(new String[0]), "FailedFdrTranslationImports.csv", new CSVDataWriter() {
            @Override
            public void writeData(CSVWriter csvWriter) {
                for(String[] line : dataGrid) {
                    for( int i=0; i<line.length; i++){
                        if ( "".equals(line[i]) ) {
                            line[i] = FdrUtils.EMPTY;
                        }
                    }
                    csvWriter.writeNext(line);
                }
            }
        });
        
        return null;
    }
    
    @RequestMapping
    public String updateLog(HttpServletResponse response, String resultId) throws IOException {
        TranslationImportCallbackResult result = recentResultsCache.getResult(resultId);
        
        Integer index = result.getLogIndex(); //must get this before getNewLogLines()!
        if(index >= PointImportController.MAX_LOGGED_LINES){
            return null;
        }

        List<String> logLines = result.getNewLogLines();
        List<Integer> failedLines = result.getFailedRowNumbers();
        
        JSONObject jsonObject = new JSONObject();
        
        for(String line : logLines) {
            String quality = "successMessage"; //these match red and green text CSS classes
            if(failedLines.contains(index)) quality = "errorMessage";
            jsonObject.put(line, quality);
            index++;
            if(index >= PointImportController.MAX_LOGGED_LINES){
                break;
            }
        }
        
        PrintWriter writer = response.getWriter();
        String responseJsonStr = jsonObject.toString();
        writer.write(responseJsonStr);
        return null;
    }
    
    private ResponseEntity<Map<String, ? extends Object>> prepareErrorReturn(YukonUserContext userContext, MessageSourceResolvable resolvable) {
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        String errorMessage = messageSourceAccessor.getMessage(resolvable);
        Map<String, String> responseMap = Collections.singletonMap("error", errorMessage);
        
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.TEXT_HTML);
        
        return new ResponseEntity<Map<String, ? extends Object>>(responseMap, responseHeaders, HttpStatus.CREATED);
    }
    
    @Resource(name="recentResultsCache")
    public void setResultsCache(RecentResultsCache<TranslationImportCallbackResult> resultsCache) {
        this.recentResultsCache = resultsCache;
    }
}
