package com.cannontech.web.bulk;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.cannontech.common.bulk.callbackResult.BackgroundProcessResultHolder;
import com.cannontech.common.bulk.callbackResult.TranslationImportCallbackResult;
import com.cannontech.common.bulk.model.FdrImportFileInterfaceInfo;
import com.cannontech.common.bulk.model.FdrInterfaceDisplayable;
import com.cannontech.common.bulk.service.FdrTranslationManagerCsvHelper;
import com.cannontech.common.bulk.service.FdrTranslationManagerService;
import com.cannontech.common.exception.ImportFileFormatException;
import com.cannontech.common.fdr.FdrInterfaceType;
import com.cannontech.common.fdr.FdrTranslation;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.tools.csv.CSVReader;
import com.cannontech.tools.csv.CSVWriter;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.exceptions.EmptyImportFileException;
import com.cannontech.web.exceptions.NoImportFileException;
import com.cannontech.web.util.WebFileUtils;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("fdrTranslationManager/*")
public class FdrTranslationManagerController {
    private FdrTranslationManagerService fdrTranslationManagerService;
    private FdrTranslationManagerCsvHelper fdrTranslationManagerCsvHelper;
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
        fdrTranslationManagerCsvHelper.addDefaultColumnsToList(formattedHeaders);
        
        List<FdrTranslation> filteredTranslationsList = fdrTranslationManagerService.getFilteredTranslationList(reportInterface);
        
        //Add all headers for each translation type to the headers list
        fdrTranslationManagerCsvHelper.addHeadersFromTranslations(formattedHeaders, filteredTranslationsList);
        
        //Create data array with dimensions [# of translations + 1][headers]
        String[][] dataGrid = new String[filteredTranslationsList.size()+1][];
        for(int i = 0; i < dataGrid.length; i++) {
            dataGrid[i] = new String[formattedHeaders.size()];
        }
        
        //Insert headers row
        for(int i = 0; i < formattedHeaders.size(); i++) {
            dataGrid[0][i] = formattedHeaders.get(i);
        }
        
        //Insert translation data
        fdrTranslationManagerCsvHelper.populateExportArray(dataGrid, filteredTranslationsList);
        
        //Set up CSV stream
        response.setContentType("text/csv");
        response.setHeader("Content-Type", "application/force-download");
        String fileName = ServletUtil.makeWindowsSafeFileName("FdrTranslationsReport.csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName);
        OutputStream outputStream = response.getOutputStream();
        Writer writer = new BufferedWriter(new OutputStreamWriter(outputStream));
        CSVWriter csvWriter = new CSVWriter(writer);
        
        //Write array values to csv
        for(String[] line : dataGrid) {
            csvWriter.writeNext(line);
        }
        csvWriter.close();
        
        return "";
    }
    
    @RequestMapping
    public String submitImport(ModelMap model, HttpServletRequest request, FlashScope flashScope, YukonUserContext userContext, 
                               boolean ignoreInvalidColumns) throws IOException {
        
        //Procure the import file
        if(!ServletFileUpload.isMultipartContent(request)) {
            MessageSourceResolvable errorMsg = new YukonMessageSourceResolvable("yukon.web.modules.amr.fdrTranslationManagement.error.noImportFile");
            flashScope.setError(errorMsg);
            return "redirect:home";
        }
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile dataFile = multipartRequest.getFile("importFile");
        
        //Set up a reader for the file
        CSVReader csvReader;
        try {
            csvReader = WebFileUtils.getTempBackedCsvReaderFromMultipartFile(dataFile);
        } catch(NoImportFileException e) {
            MessageSourceResolvable errorMsg = new YukonMessageSourceResolvable("yukon.web.modules.amr.fdrTranslationManagement.error.noImportFile");
            flashScope.setError(errorMsg);
            return "redirect:home";
        } catch(EmptyImportFileException e) {
            MessageSourceResolvable errorMsg = new YukonMessageSourceResolvable("yukon.web.modules.amr.fdrTranslationManagement.error.emptyImportFile");
            flashScope.setError(errorMsg);
            return "redirect:home";
        }
        
        //Get column headers, strip whitespace, check for duplicates
        String[] headersArray = csvReader.readNext();
        List<String> headers;
        try {
            headers = fdrTranslationManagerCsvHelper.cleanAndValidateHeaders(headersArray);
        } catch(ImportFileFormatException e) {
            MessageSourceResolvable errorMsg = new YukonMessageSourceResolvable("yukon.web.modules.amr.fdrTranslationManagement.error.duplicateColumn", e.getHeaderName());
            flashScope.setError(errorMsg);
            return "redirect:home";
        }
        
        //Check for all default headers
        String missingHeaders = fdrTranslationManagerCsvHelper.checkForMissingDefaultImportHeaders(headers);
        if(missingHeaders != null) {
            //Error - missing default header
            MessageSourceResolvable errorMsg = new YukonMessageSourceResolvable("yukon.web.modules.amr.fdrTranslationManagement.error.defaultHeaderMissing", missingHeaders);
            flashScope.setError(errorMsg);
            return "redirect:home";
        }
        
        //Get list of all interfaces represented in the file and check to make 
        //sure that all required columns are present
        FdrImportFileInterfaceInfo interfaceInfo;
        try {
            interfaceInfo = fdrTranslationManagerService.getInterfaceInfo(headers, ignoreInvalidColumns);
        } catch(ImportFileFormatException e) {
            MessageSourceResolvable errorMsg = new YukonMessageSourceResolvable("yukon.web.modules.amr.fdrTranslationManagement.error.badColumn", e.getHeaderName());
            flashScope.setError(errorMsg);
            return "redirect:home";
        }
        
        try {
            fdrTranslationManagerCsvHelper.validateInterfaceHeadersPresent(interfaceInfo, headers);
        } catch(ImportFileFormatException e) {
            MessageSourceResolvable errorMsg = new YukonMessageSourceResolvable("yukon.web.modules.amr.fdrTranslationManagement.error.missingColumn", e.getHeaderName(), e.getInterfaceName());
            flashScope.setError(errorMsg);
            return "redirect:home";
        }

        //Read all file lines
        List<String[]> fileLines = Lists.newArrayList();
        String[] line;
        while((line = csvReader.readNext()) != null) {
            fileLines.add(line);
        }
        
        //start import
        String resultId = fdrTranslationManagerService.startImport(headers, interfaceInfo.getColumnsToIgnore(), fileLines, userContext);
        
        model.addAttribute("resultId", resultId);
        model.addAttribute("ignoreInvalidColumns", ignoreInvalidColumns);
        model.addAttribute("fileName", dataFile.getOriginalFilename());
        
        return "redirect:importResults";
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
        String[][] dataGrid = new String[failedRowNums.size()+1][];
        for(int i = 0; i < dataGrid.length; i++) {
            dataGrid[i] = new String[headers.size()];
        }
        
        //add headers
        dataGrid[0] = headers.toArray(new String[headers.size()]);
        int dataGridIndex = 1;
        
        //add the rest of the rows
        for(int i = 0; i < importRows.size(); i++) {
            if(failedRowNums.contains(i)) {
                String[] row = importRows.get(i);
                
                for(int j = 0; j < row.length; j++) {
                    dataGrid[dataGridIndex][j] = row[j];
                    
                }
                dataGrid[dataGridIndex][row.length] = log.get(i);
                dataGridIndex++;
            }
        }

        //Set up CSV stream
        response.setContentType("text/csv");
        response.setHeader("Content-Type", "application/force-download");
        String fileName = ServletUtil.makeWindowsSafeFileName("FailedFdrTranslationImports.csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName);
        OutputStream outputStream = response.getOutputStream();
        Writer writer = new BufferedWriter(new OutputStreamWriter(outputStream));
        CSVWriter csvWriter = new CSVWriter(writer);
        
        //Write array values to csv
        for(String[] line : dataGrid) {
            csvWriter.writeNext(line);
        }
        csvWriter.close();
        
        return "";
    }
    
    @RequestMapping
    public String updateLog(HttpServletResponse response, String resultId) throws IOException {
        TranslationImportCallbackResult result = recentResultsCache.getResult(resultId);
        
        Integer index = result.getLogIndex(); //must get this before getNewLogLines()!
        List<String> logLines = result.getNewLogLines();
        List<Integer> failedLines = result.getFailedRowNumbers();
        
        JSONObject jsonObject = new JSONObject();
        
        for(String line : logLines) {
            String quality = "successMessage"; //these match red and green text CSS classes
            if(failedLines.contains(index)) quality = "errorMessage";
            jsonObject.put(line, quality);
            index++;
        }
        
        PrintWriter writer = response.getWriter();
        String responseJsonStr = jsonObject.toString();
        writer.write(responseJsonStr);
        return null;
    }
    
    @Resource(name="recentResultsCache")
    public void setResultsCache(RecentResultsCache<TranslationImportCallbackResult> resultsCache) {
        this.recentResultsCache = resultsCache;
    }
    
    @Autowired
    public void setFdrTranslationManagerCsvHelper(FdrTranslationManagerCsvHelper fdrTranslationManagerCsvHelper) {
        this.fdrTranslationManagerCsvHelper = fdrTranslationManagerCsvHelper;
    }
    
    @Autowired
    public void setFdrTranslationManagerService(FdrTranslationManagerService fdrTranslationManagerService) {
        this.fdrTranslationManagerService = fdrTranslationManagerService;
    }
}
